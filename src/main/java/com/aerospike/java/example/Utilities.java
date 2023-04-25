package com.aerospike.java.example;

import java.util.Random;

public class Utilities {
    static boolean debugMode = false;

    static boolean isDebugMode(){return debugMode;};

    // Utility function for generating random strings
    @SuppressWarnings("SpellCheckingInspection")
    public static String generateRandomString(int stringLength){
        Random r = new Random();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder randomStringBuffer = new StringBuilder();
        for(int i = 0;i<stringLength;i++){
            String randomChar = String.valueOf(chars.charAt(r.nextInt(chars.length())));
            randomStringBuffer.append(randomChar);
        }
        return randomStringBuffer.toString();
    }

    /**
     * Turn combination of node ID and neighbour ID into a single string identifier
     * @param nodeID as int
     * @param neighbourID as int
     * @return neighbour node identifier as String
     */
    static String getNeighbourIDAsString(int nodeID, int neighbourID){
        return String.format("%09d.%02d",nodeID,neighbourID);
    }
    /**
     * Throw this error if we have parsing exceptions
     */
    public static class ParseException extends Exception {
        /**
         * Parse Exception constructor
         *
         * @param message error message
         */
        public ParseException(String message) {
            super(message);
        }
    }
}
