package com.aerospike.java.example;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GetCellDataMainMultiThread {
    //  Usage : java -cp BenchmarkTest.jar com.aerospike.java.example.GetCellDataMainMultiThread -z 4 -i 10000
    private void runTest(AerospikeClient aerospikeClient, BenchProperties benchProperties, int nThreads, int totalIterations) throws Exception {
        // Set up threads and run
        long myStartTime = System.nanoTime();

        ExecutorService es = Executors.newFixedThreadPool(nThreads);
        int batchSize = totalIterations / nThreads;
        GetCellDataTaskSync[] getCellTaskArray = new GetCellDataTaskSync[nThreads];
        for (int i = 0 ; i < nThreads; i++) {
            int taskIterations = (i != nThreads -1) ? batchSize : totalIterations - batchSize * (nThreads -1);
            getCellTaskArray[i] = new GetCellDataTaskSync(aerospikeClient, benchProperties, taskIterations);
            es.execute(getCellTaskArray[i]);
        }
        es.shutdown();
        es.awaitTermination(365, TimeUnit.DAYS);

        int totalCompletedIterations = 0;
        long totalDurationNanos = 0;
        int totalRecordFoundCount = 0;

        for(int i=0;i<nThreads;i++){
            totalDurationNanos += getCellTaskArray[i].getTotalDurationNanos();
            totalCompletedIterations += getCellTaskArray[i].getCompletedIterations();
            totalRecordFoundCount += getCellTaskArray[i].getTotalRecordsFound();
        }
        System.out.printf("Total : %d completed iterations. Average duration %.3f ms. Record found count %d%n",totalCompletedIterations,
                (double) totalDurationNanos /totalCompletedIterations/Math.pow(10,6),totalRecordFoundCount);
        System.out.printf("Run time %.3f seconds %n",(System.nanoTime() - myStartTime)/(double)1000000/1000);


    }

    public static void main(String[] args) throws Exception{
        // Get the command line options
        CommandLine cmd = GetCellDataOptionsHelper.getArguments(args);
        // Check the command line options are as expected
        CommandLineParser parser = new DefaultParser();
        parser.parse(GetCellDataOptionsHelper.cmdLineOptions(), args);
        CmdLineFlags.printOptions(GetCellDataOptionsHelper.cmdLineOptions(),cmd);

        // Get the configuration data from the properties file
        // and extract the required values
        String propertiesFile = CmdLineFlags.getOptionUsingDefaults(cmd, CmdLineFlags.PROPERTIES_FILE_FLAG);
        BenchProperties benchProperties = new BenchProperties(propertiesFile);
        benchProperties.printProperties();

//        Initialize Client Policy and AerospikeClient
        Host[] hosts = new Host[]{new Host(benchProperties.getAerospikeServer(), benchProperties.getAerospikePort())};
        ClientPolicy clientPolicy = new ClientPolicy();
        clientPolicy.minConnsPerNode = benchProperties.getMinClientConnectionsPerNode();
        clientPolicy.maxConnsPerNode = benchProperties.getMaxClientConnectionsPerNode();
        clientPolicy.user= benchProperties.getAerospikeUser();
        clientPolicy.password= benchProperties.getAerospikePassword();

        AerospikeClient aerospikeClient = new AerospikeClient(clientPolicy, hosts);

        // and extract the required values
        int nThreads = Integer.parseInt(CmdLineFlags.getOptionUsingDefaults(cmd, CmdLineFlags.THREAD_FLAG));
        int queryIterations = Integer.parseInt(CmdLineFlags.getOptionUsingDefaults(cmd, CmdLineFlags.QUERY_ITERATIONS_FLAG));
        Utilities.debugMode = cmd.hasOption(CmdLineFlags.DEBUG_FLAG);

        GetCellDataMainMultiThread getCellsMainMultiThread = new GetCellDataMainMultiThread();
        getCellsMainMultiThread.runTest(aerospikeClient, benchProperties, nThreads,queryIterations);
    }
}
