package com.aerospike.java.example;

import com.aerospike.client.*;
import com.aerospike.client.cdt.*;
import com.aerospike.client.policy.WritePolicy;

import java.util.*;

public class InsertDataTask {

    private static final ListPolicy DEFAULT_LIST_POLICY = new ListPolicy(ListOrder.UNORDERED,
            ListWriteFlags.PARTIAL | ListWriteFlags.NO_FAIL);
    private static final ListPolicy DEFAULT_LIST_POLICY_UNIQUE = new ListPolicy(ListOrder.UNORDERED,
            ListWriteFlags.ADD_UNIQUE | ListWriteFlags.PARTIAL | ListWriteFlags.NO_FAIL);

    protected int insertDataList(AerospikeClient aerospikeClient, BenchProperties benchProperties, int startOfRecords, int endOfRecords)
            throws BenchProperties.PropertyNotIntegerException {
        WritePolicy writePolicy = new WritePolicy();
        writePolicy.totalTimeout = 10000;
        writePolicy.expiration = benchProperties.getTTL();
        // writePolicy.recordExistsAction = RecordExistsAction.CREATE_ONLY;

        int cols = benchProperties.getMaxCellColumns();
        int rows = benchProperties.getMaxCellRows();

        ArrayList<Float> cellArray = new ArrayList<>();
        for (int idx = 0; idx < rows * cols; idx++) {
            cellArray.add(new Random().nextFloat());
        }

        int recordsInserted = 0;
//      Start with startOfRecords and keep writing till endOfRecords
        for (int cellID = startOfRecords; cellID <= endOfRecords; cellID++) {
            String cellIDAsString = String.format("%09d", cellID);

            Key key = new Key(benchProperties.getAerospikeNamespace(), benchProperties.getAerospikeSet(), cellIDAsString);
            Bin pkBin = new Bin(QueryConstants.CELL_ID_BIN_NAME, cellIDAsString);
            Bin cellBinList = new Bin (QueryConstants.LIST_BIN_NAME, cellArray);

            try {
                aerospikeClient.operate(writePolicy, key, Operation.put(pkBin), Operation.put(cellBinList));
                recordsInserted++;
            } catch (AerospikeException e) {
                System.out.println("Record insertion error" + e.getMessage());
            }

        }
        return recordsInserted;
    }

    protected int insertDataList2(AerospikeClient aerospikeClient, BenchProperties benchProperties, int startOfRecords, int endOfRecords)
            throws BenchProperties.PropertyNotIntegerException {
        WritePolicy writePolicy = new WritePolicy();
        writePolicy.totalTimeout = 10000;
        // writePolicy.recordExistsAction = RecordExistsAction.CREATE_ONLY;

        int recordsInserted = 0;
//      Start with startOfRecords and keep writing till endOfRecords
        for (int cellID = startOfRecords; cellID <= endOfRecords; cellID++) {
            for (int i = 0; i < 255; i++) {
                String cellIDAsString = String.format("%09d:%03d", cellID, i);

                Key key = new Key(benchProperties.getAerospikeNamespace(), benchProperties.getAerospikeSet(), cellIDAsString);
                Bin pkBin = new Bin(QueryConstants.CELL_ID_BIN_NAME, cellIDAsString);

                ArrayList<Float> cellArray = new ArrayList<>();
                for (int idx = 0; idx < 255; idx++) {
                    cellArray.add(new Random().nextFloat());
                }

                Bin cellBinList = new Bin (QueryConstants.LIST_BIN_NAME, cellArray);

                try {
                    aerospikeClient.operate(writePolicy, key, Operation.put(pkBin), Operation.put(cellBinList));
                    recordsInserted++;
                } catch (AerospikeException e) {
                    System.out.println("Record insertion error" + e.getMessage());
                }

            }
        }
        return recordsInserted;
    }
}
