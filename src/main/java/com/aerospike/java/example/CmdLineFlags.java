package com.aerospike.java.example;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Flags used at the command line
 */
public class CmdLineFlags {
    private static final int DEFAULT_THREAD_COUNT = 1;

    /**
     * Flag to indicate thread count : z
     */
    public static final String THREAD_FLAG = "z";

    /**
     * Flag to indicate query iterations : i
     */
    public static final String QUERY_ITERATIONS_FLAG = "i";

    /**
     * Flag for properties file : d
     */
    public static final String PROPERTIES_FILE_FLAG = "d";

    /**
     * Flag for properties file : d
     */
    public static final String DEBUG_FLAG = "x";

    /**
     * Check type of supplied command line values
     * Throw an exception if there is a problem
     *
     * @param flag  flag indicating which parameter option we're checking
     * @param value value we're checking
     * @throws Utilities.ParseException if value cannot be parsed as expected
     */
    static void checkCommandLineArgumentType(String flag, String value) throws Utilities.ParseException {
        switch (flag) {
            // Absence of breaks is deliberate - all the below are int fields
            case THREAD_FLAG:
            case QUERY_ITERATIONS_FLAG:
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new Utilities.ParseException(String.format("-%s flag should have an integer argument. Argument supplied is %s", flag, value));
                }
                break;
        }
    }

    /**
     * Get default value for command line flags
     *
     * @param flag flag identifier
     * @return default value for flag
     */
    static String getDefaultValue(String flag) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (flag) {
            case THREAD_FLAG:
                return Integer.toString(DEFAULT_THREAD_COUNT);
            default:
                return null;
        }
    }

    /**
     * Get option for optionFlag from a command line object, returning the default value if applicable
     *
     * @param cmd        CommandLine object has command line arguments as called
     * @param optionFlag option value is required for
     * @return value for option flag
     */
    public static String getOptionUsingDefaults(CommandLine cmd, String optionFlag) throws Utilities.ParseException {
        String value = cmd.getOptionValue(optionFlag, getDefaultValue(optionFlag));
        if (cmd.hasOption(optionFlag)) checkCommandLineArgumentType(optionFlag, value);
        return value;
    }

    public static void printOptions(Options options, CommandLine cmd) throws Utilities.ParseException {
        for(Option option : options.getOptions()){
            String value = CmdLineFlags.getOptionUsingDefaults(cmd, option.getOpt());
            System.out.printf("Value for %s flag is %s%n", option.getLongOpt(),value);
        }
    }
}
