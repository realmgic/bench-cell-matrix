package com.aerospike.java.example;

import com.aerospike.client.AerospikeClient;

public class InsertDataTaskSync extends InsertDataTask implements Runnable{
    private final AerospikeClient aerospikeClient;
    private final int startRecords;
    private final int endRecords;
    private final BenchProperties benchProperties;
    private long totalDurationNanos = 0;
    private int completedInsertions = 0;

    InsertDataTaskSync(AerospikeClient aerospikeClient, BenchProperties benchProperties, int startRecords, int endRecords ) {
        this.aerospikeClient = aerospikeClient;
        this.startRecords = startRecords;
        this.endRecords = endRecords;
        this.benchProperties = benchProperties;
    }

    long getTotalDurationNanos(){
        return totalDurationNanos;
    }

    int getCompletedInsertions() { return completedInsertions; };

    @SuppressWarnings("ThrowablePrintedToSystemOut")
    @Override
    public void run() {
//        Capture start time of processing
        long startTime = System.nanoTime();

        try {
            completedInsertions = insertDataList(aerospikeClient, benchProperties, startRecords, endRecords);
            //completedInsertions = insertDataList2(aerospikeClient, benchProperties, startRecords, endRecords);

        }
        catch(Exception e){
            System.out.println(e);
        }
        totalDurationNanos = System.nanoTime() - startTime;
        System.out.printf("Time to insert %d records = %d ms%n",completedInsertions, totalDurationNanos / 1000000);
    }
}
