package com.aerospike.java.example;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.cdt.ListOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetCellDataTask {
    // Run the query for the given neighbour id. The neighbour id is generated randomly and passed here.
    public int getData(AerospikeClient aerospikeClient, BenchProperties benchProperties, int cell, Map<Integer, List<Integer>> range) throws BenchProperties.PropertyNotIntegerException {
        int recordFoundCount = 0;

        Key key = new Key(benchProperties.getAerospikeNamespace(), benchProperties.getAerospikeSet(), String.format("%09d", cell));
        int rows = benchProperties.getMaxCellRows();

        ArrayList<Operation> ops = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> entry : range.entrySet()) {
            int offset = entry.getKey();
            int begin = (int) entry.getValue().get(0);
            int end = (int) entry.getValue().get(1);

            if (Utilities.isDebugMode())
                System.out.printf("offset: %d, begin: %d, end: %d\n", offset, begin, end);

            ops.add(ListOperation.getRange(QueryConstants.LIST_BIN_NAME, offset * rows + begin, end - begin));
        }
        // ops.add(Operation.touch());

        Record rec = aerospikeClient.operate(aerospikeClient.writePolicyDefault, key, ops.toArray(new Operation[ops.size()]));

        if (Utilities.isDebugMode())
            System.out.println(rec);

        recordFoundCount = rec.getList(QueryConstants.LIST_BIN_NAME).size();

        return recordFoundCount;
    }

}
