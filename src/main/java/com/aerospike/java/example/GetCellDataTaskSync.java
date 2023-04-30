package com.aerospike.java.example;

import com.aerospike.client.AerospikeClient;


import java.util.*;

public class GetCellDataTaskSync extends GetCellDataTask implements Runnable{
    private final AerospikeClient aerospikeClient;
    private final BenchProperties benchProperties;
    private final int iterations;
    private long totalDurationNanos = 0;
    private int completedIterations = 0;
    private int totalRecordFoundCount = 0;
    private final static Random random  = new Random();

    GetCellDataTaskSync(AerospikeClient aerospikeClient, BenchProperties benchProperties, int iterations) {
        this.aerospikeClient = aerospikeClient;
        this.iterations = iterations;
        this.benchProperties = benchProperties;
    }

    private static int getRandomCellID(BenchProperties benchProperties) throws BenchProperties.PropertyNotIntegerException {
        return benchProperties.getStartCellID() + random.nextInt(benchProperties.getEndCellID() - benchProperties.getStartCellID());
    }

    long getTotalDurationNanos(){
        return totalDurationNanos;
    }

    int getCompletedIterations() { return completedIterations; }

    int getTotalRecordsFound() { return totalRecordFoundCount; }

    @Override
    public void run() {
        Random random = new Random();
        try {
            int rows = benchProperties.getReadCellRows();
            int cols = benchProperties.getReadCellColumns();
            int maxRows = benchProperties.getMaxCellRows();
            int maxCols = benchProperties.getMaxCellColumns();
            int ttl = benchProperties.getTTL();
            int lruTolerance =  benchProperties.getLRUTolerance();

            int row_start = random.nextInt(maxRows - rows);
            int randomPoint = random.nextInt(maxCols - cols);

            for (int i = 0; i < iterations; i++) {
                int key = getRandomCellID(benchProperties);

                TreeMap<Integer, List<Integer>> range = new TreeMap<>();

                for (int row = row_start; row < rows + row_start; row++) {
                    List<Integer> tmpRange = new ArrayList<>();
                    tmpRange.add(randomPoint); // from col
                    tmpRange.add(tmpRange.get(0) + cols); // to col
                    range.put(row, tmpRange);
                }

                if (Utilities.isDebugMode())
                    System.out.printf("Looking for key %d range id %s\n", key, range);

                // Capture start of query run
                long startTime = System.nanoTime();
                int recordsFound = getData(aerospikeClient, benchProperties, key, range, ttl, lruTolerance);
                totalRecordFoundCount += recordsFound;
                totalDurationNanos += System.nanoTime() - startTime;
                completedIterations++;
            }
        }
        catch(BenchProperties.PropertyNotIntegerException e){
            //noinspection ThrowablePrintedToSystemOut
            System.out.println(e);
        }
        System.out.printf("Completed %d iterations. Average duration %.3f ms%n",iterations,(double) totalDurationNanos /iterations/Math.pow(10,6));
    }
}
