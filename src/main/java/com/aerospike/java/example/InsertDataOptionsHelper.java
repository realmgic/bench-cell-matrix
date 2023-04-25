package com.aerospike.java.example;

import org.apache.commons.cli.*;

public class InsertDataOptionsHelper {
    public static Options cmdLineOptions() {
        Options cmdLineOptions = new Options();

        Option threadOption = new Option(CmdLineFlags.THREAD_FLAG, "thread", true, "No. of threads to use");
        Option propertiesFileOption = new Option(CmdLineFlags.PROPERTIES_FILE_FLAG,"propertiesFile",true,"Properties file");

        threadOption.setRequired(false);
        propertiesFileOption.setRequired(false);

        cmdLineOptions.addOption(threadOption);
        cmdLineOptions.addOption(propertiesFileOption);
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
