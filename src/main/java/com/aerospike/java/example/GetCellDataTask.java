package com.aerospike.java.example;

import com.aerospike.client.*;
import com.aerospike.client.Record;
import com.aerospike.client.cdt.ListOperation;
import com.aerospike.client.exp.Exp;
import com.aerospike.client.exp.Expression;
import com.aerospike.client.policy.CommitLevel;
import com.aerospike.client.policy.WritePolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetCellDataTask {
    // Run the query for the given neighbour id. The neighbour id is generated randomly and passed here.
    public int getData(AerospikeClient aerospikeClient, BenchProperties benchProperties, int cell, Map<Integer, List<Integer>> range) throws BenchProperties.PropertyNotIntegerException {
        int recordFoundCount;

        Key key = new Key(benchProperties.getAerospikeNamespace(), benchProperties.getAerospikeSet(), String.format("%09d", cell));
        int rows = benchProperties.getMaxCellRows();

        ArrayList<Operation> ops = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> entry : range.entrySet()) {
            int offset = entry.getKey();
            int begin = entry.getValue().get(0);
            int end = entry.getValue().get(1);

            if (Utilities.isDebugMode())
                System.out.printf("offset: %d, begin: %d, end: %d\n", offset, begin, end);

            ops.add(ListOperation.getRange(QueryConstants.LIST_BIN_NAME, offset * rows + begin, end - begin));
        }
        // ops.add(Operation.touch());

        Record rec = aerospikeClient.operate(aerospikeClient.writePolicyDefault, key, ops.toArray(new Operation[0]));

        if (Utilities.isDebugMode())
            System.out.println(rec);

        if (rec != null) {
            recordFoundCount = rec.getList(QueryConstants.LIST_BIN_NAME).size();
        } else {
            recordFoundCount = 0;
        }

        if (benchProperties.getLRUEnabled()) {
            int ttl = benchProperties.getTTL();
            Expression exp = Exp.build(Exp.lt(Exp.ttl(), Exp.val((ttl - benchProperties.getLRUTolerance()))));

            WritePolicy writePolicy = new WritePolicy();
            writePolicy.commitLevel = CommitLevel.COMMIT_MASTER;
            writePolicy.filterExp = exp;
            writePolicy.expiration = ttl;
            writePolicy.failOnFilteredOut = true;

            if (Utilities.isDebugMode())
                System.out.println(key);

            try {
                aerospikeClient.touch(writePolicy, key);
                System.out.println("No Skip: " + key);
            } catch (AerospikeException aex) {
                if (aex.getResultCode() == 27) {
                    if (Utilities.isDebugMode())
                        System.out.println("Skip: " + key);

                } else {
                    throw  aex;
                }
            }

        }

        return recordFoundCount;
    }

}
