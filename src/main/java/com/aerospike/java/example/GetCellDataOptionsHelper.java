package com.aerospike.java.example;

import org.apache.commons.cli.*;

public class GetCellDataOptionsHelper {
        public static Options cmdLineOptions() {
            Options cmdLineOptions = new Options();

            Option threadOption = new Option(CmdLineFlags.THREAD_FLAG, "thread", true, "No. of threads to use");
            Option queryIterationsOption = new Option(CmdLineFlags.QUERY_ITERATIONS_FLAG,"queryIterations",true,"No of query iterations");
            Option propertiesFileOption = new Option(CmdLineFlags.PROPERTIES_FILE_FLAG,"propertiesFile",true,"Properties file");
            Option debugOption = new Option(CmdLineFlags.DEBUG_FLAG,"debug",false,"show debug statements");

            threadOption.setRequired(false);
            queryIterationsOption.setRequired(true);
            propertiesFileOption.setRequired(false);
            debugOption.setRequired(false);

            cmdLineOptions.addOption(threadOption);
            cmdLineOptions.addOption(queryIterationsOption);
            cmdLineOptions.addOption(propertiesFileOption);
            cmdLineOptions.addOption(debugOption);
            return cmdLineOptions;
        }

    /**
     * Return a CommandLine object given String[] args
     *
     * @param args String[] as would be passed at cmd line
     * @return CommandLine object
     * @throws ParseException if options cannot be parsed
     */
    public static CommandLine getArguments(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(cmdLineOptions(), args);
    }
}
