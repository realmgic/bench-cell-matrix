package com.aerospike.java.example;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.TlsPolicy;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class InsertDataMainMultiThread {

//    Usage : java -cp BenchmarkTest.jar com.aerospike.java.example.InsertDataMainMultiThread -z 4

    private void insertRecords(AerospikeClient aerospikeClient, int nThreads, BenchProperties benchProperties) throws Exception {
        // Clear existing records
        long myStartTime = System.nanoTime();
        //aerospikeClient.truncate(new InfoPolicy(),benchProperties.getAerospikeNamespace(), benchProperties.getAerospikeSet(), null);
        //aerospikeClient.truncate(new InfoPolicy(),benchProperties.getAerospikeNamespace(), benchProperties.getAerospikeSetRI(), null);

        ExecutorService es = Executors.newFixedThreadPool(nThreads);
        InsertDataTaskSync[] insertTaskArray = new InsertDataTaskSync[nThreads];

        // Splitting the record id space into batches for multi-threading purposes
        int batchSize = (benchProperties.getEndCellID() - benchProperties.getStartCellID() + 1) / nThreads;
        for (int i = 0 ; i < nThreads; i++) {
            int rangeStart = benchProperties.getStartCellID() + i*batchSize;
            int rangeEnd = (i != nThreads -1) ? rangeStart + batchSize - 1 : benchProperties.getEndCellID();
            insertTaskArray[i] = new InsertDataTaskSync(aerospikeClient, benchProperties, rangeStart, rangeEnd);
            es.execute(insertTaskArray[i]);
        }
        es.shutdown();
        es.awaitTermination(365, TimeUnit.DAYS);

        int totalCompletedInsertions = 0;
        long totalDurationNanos = 0;

        for(int i=0;i<nThreads;i++){
            totalDurationNanos += insertTaskArray[i].getTotalDurationNanos();
            totalCompletedInsertions += insertTaskArray[i].getCompletedInsertions();
        }
        System.out.printf("Total : %d completed insertions. Average duration %.3f ms%n",totalCompletedInsertions,
                (double) totalDurationNanos /totalCompletedInsertions/Math.pow(10,6));
        System.out.printf("Run time %.3f seconds %n",(System.nanoTime() - myStartTime)/(double)1000000/1000);
    }

    public static void main(String[] args) throws Exception{
        // Get the command line options
        CommandLine cmd = InsertDataOptionsHelper.getArguments(args);
        // Check the command line options are as expected
        CommandLineParser parser = new DefaultParser();
        parser.parse(InsertDataOptionsHelper.cmdLineOptions(), args);
        CmdLineFlags.printOptions(InsertDataOptionsHelper.cmdLineOptions(),cmd);

        // Get the configuration data from the properties file
        // and extract the required values
        String propertiesFile = CmdLineFlags.getOptionUsingDefaults(cmd, CmdLineFlags.PROPERTIES_FILE_FLAG);
        BenchProperties benchProperties = new BenchProperties(propertiesFile);
        benchProperties.printProperties();

//        Initialize Client Policy and AerospikeClient
        String tlsName = benchProperties.getTLSName();
        Host[] hosts = new Host[1];
        if (tlsName.length() > 0) {
            hosts[0] = new Host(benchProperties.getAerospikeServer(), tlsName, benchProperties.getAerospikePort());
        } else {
            hosts[0] = new Host(benchProperties.getAerospikeServer(),  benchProperties.getAerospikePort());
        }
        ClientPolicy clientPolicy = new ClientPolicy();
        clientPolicy.maxConnsPerNode = benchProperties.getMaxClientConnectionsPerNode();
        clientPolicy.minConnsPerNode = benchProperties.getMinClientConnectionsPerNode();
        //clientPolicy.timeout = 10000;
        clientPolicy.user= benchProperties.getAerospikeUser();
        clientPolicy.password= benchProperties.getAerospikePassword();

        if (tlsName.length() > 0)
        {
            TlsPolicy tlsPolicy = new TlsPolicy();
            clientPolicy.tlsPolicy = tlsPolicy;
        }

        AerospikeClient aerospikeClient = new AerospikeClient(clientPolicy, hosts);

        // and extract the required values
        int nThreads = Integer.parseInt(CmdLineFlags.getOptionUsingDefaults(cmd, CmdLineFlags.THREAD_FLAG));

        InsertDataMainMultiThread insertDataMainMultiThread = new InsertDataMainMultiThread();
        insertDataMainMultiThread.insertRecords(aerospikeClient,nThreads, benchProperties);

    }
}
