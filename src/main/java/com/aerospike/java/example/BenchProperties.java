package com.aerospike.java.example;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class BenchProperties {
    private static final String AEROSPIKE_SERVER_PROPERTY_NAME = "AEROSPIKE_SERVER_NAME";
    private static final String AEROSPIKE_PORT_PROPERTY_NAME = "AEROSPIKE_PORT";
    private static final String AEROSPIKE_NAMESPACE_PROPERTY_NAME = "AEROSPIKE_NAMESPACE";
    private static final String AEROSPIKE_SET_PROPERTY_NAME = "AEROSPIKE_SET";
    private static final String AEROSPIKE_SET_RI_PROPERTY_NAME = "AEROSPIKE_SET_RI";
    private static final String AEROSPIKE_USER_PROPERTY_NAME = "AEROSPIKE_USER";
    private static final String AEROSPIKE_PASSWORD_PROPERTY_NAME = "AEROSPIKE_PASSWORD";
    private static final String START_CELL_ID_PROPERTY_NAME = "START_CELL_ID";
    private static final String END_CELL_ID_PROPERTY_NAME ="END_CELL_ID";
    private static final String MAX_CELL_ROWS ="MAX_CELL_ROWS";
    private static final String MAX_CELL_COLUMNS ="MAX_CELL_COLUMNS";
    private static final String READ_CELL_ROWS ="READ_CELL_ROWS";
    private static final String READ_CELL_COLUMNS ="READ_CELL_COLUMNS";
    private static final String ATTRIBUTE_VALUE_STRING_LENGTH_PROPERTY_NAME="ATTRIBUTE_VALUE_STRING_LENGTH";
    private static final String MIN_CLIENT_CONNECTIONS_PER_NODE_PROPERTY_NAME="MIN_CLIENT_CONNECTIONS_PER_NODE";
    private static final String MAX_CLIENT_CONNECTIONS_PER_NODE_PROPERTY_NAME="MAX_CLIENT_CONNECTIONS_PER_NODE";
    private static final String TLS_NAME_PROPERTY_NAME="TLS_NAME";

    private final Properties benchProperties;

    /**
     * Construct migration properties object from properties file
     * @param propertiesFilePath path to properties file
     * @throws IOException if path not found
     */
    public BenchProperties(String propertiesFilePath) throws IOException{
        benchProperties = getProperties(propertiesFilePath);
    }

    /**
     * Aerospike server
     * @return aerospike server - uses default value if property not found
     */
    public String getAerospikeServer(){
        return benchProperties.getProperty(AEROSPIKE_SERVER_PROPERTY_NAME);
    }

    /**
     * Aerospike port
     * @return aerospike port - uses default value if property not found
     * @throws PropertyNotIntegerException if property is not an integer
     */
    public int getAerospikePort() throws PropertyNotIntegerException{
        String aerospikePortAsString = benchProperties.getProperty(AEROSPIKE_PORT_PROPERTY_NAME);
        int aerospikePortAsInt;
        try {
            aerospikePortAsInt = Integer.parseInt(aerospikePortAsString);
        }
        catch(NumberFormatException e){
            throw new PropertyNotIntegerException(AEROSPIKE_PORT_PROPERTY_NAME,aerospikePortAsString);
        }
        return aerospikePortAsInt;
    }

    /**
     * Aerospike set to insert to / read from
     * @return aerospike set - uses default value if property not found
     */
    public String getAerospikeSet(){
        return benchProperties.getProperty(AEROSPIKE_SET_PROPERTY_NAME);
    }

    public String getAerospikeSetRI(){
        return benchProperties.getProperty(AEROSPIKE_SET_RI_PROPERTY_NAME);
    }

    /**
     * Aerospike user
     * @return aerospike user - uses default value if property not found
     */
    public String getAerospikeUser(){
        return benchProperties.getProperty(AEROSPIKE_USER_PROPERTY_NAME);
    }

    /**
     * Aerospike password
     * @return aerospike user - uses default value if property not found
     */
    public String getAerospikePassword(){
        return benchProperties.getProperty(AEROSPIKE_PASSWORD_PROPERTY_NAME);
    }

    /**
     * Aerospike namespace
     * @return Aerospike namespace
     */
    public String getAerospikeNamespace(){
        return benchProperties.getProperty(AEROSPIKE_NAMESPACE_PROPERTY_NAME);
    }

    /**
     * Start cell id
     * @return cell id to start from when creating / querying data
     * @throws PropertyNotIntegerException if property is not an integer
     */
    public int getStartCellID() throws PropertyNotIntegerException{
        return getNumericProperty(START_CELL_ID_PROPERTY_NAME);
    }

    /**
     * End cell id
     * @return final cell id when creating / querying data
     * @throws PropertyNotIntegerException if property is not an integer
     */
    public int getEndCellID() throws PropertyNotIntegerException{
        return getNumericProperty(END_CELL_ID_PROPERTY_NAME);
    }

    /**
     * Max cell rows
     * @return number of rows in cell matrix
     * @throws PropertyNotIntegerException if property is not an integer
     */
    public int getMaxCellRows() throws PropertyNotIntegerException{
        return getNumericProperty(MAX_CELL_ROWS);
    }

    /**
     * Max Cell columns
     * @return number of columns in cell matrix
     * @throws PropertyNotIntegerException if property is not an integer
     */
    public int getMaxCellColumns() throws PropertyNotIntegerException{
        return getNumericProperty(MAX_CELL_COLUMNS);
    }

    /**
     * Read cell rows size
     * @return number of rows to read in cell matrix
     * @throws PropertyNotIntegerException if property is not an integer
     */
    public int getReadCellRows() throws PropertyNotIntegerException{
        return getNumericProperty(READ_CELL_ROWS);
    }

    /**
     * Read cell columns size
     * @return number of columns to read in cell matrix
     * @throws PropertyNotIntegerException if property is not an integer
     */
    public int getReadCellColumns() throws PropertyNotIntegerException{
        return getNumericProperty(READ_CELL_COLUMNS);
    }

    /**
     * Average attribute count
     * @return average attribute count
     * @throws PropertyNotIntegerException if property is not an integer
     */
    public int getAttributeValueStringLength() throws PropertyNotIntegerException{
        return getNumericProperty(ATTRIBUTE_VALUE_STRING_LENGTH_PROPERTY_NAME);
    }

    /**
     * Min client connections per node
     * @return Min client connections per node
     * @throws PropertyNotIntegerException if property is not an integer
     */
    public int getMinClientConnectionsPerNode() throws PropertyNotIntegerException{
        return getNumericProperty(MIN_CLIENT_CONNECTIONS_PER_NODE_PROPERTY_NAME);
    }
    public String getTLSName() throws PropertyNotIntegerException{
        return benchProperties.getProperty(TLS_NAME_PROPERTY_NAME);
    }

    /**
     * Max client connections per node
     * @return Max client connections per node
     * @throws PropertyNotIntegerException if property is not an integer
     */
    public int getMaxClientConnectionsPerNode() throws PropertyNotIntegerException{
        return getNumericProperty(MAX_CLIENT_CONNECTIONS_PER_NODE_PROPERTY_NAME);
    }

    /**
     * Get default properties and then overwrite with properties from a named file, if it exists
     * @param filename path to file
     * @return Properties object
     * @throws IOException if file not found / accessible
     */
    private Properties getProperties(String filename) throws IOException {
        Properties properties = new Properties();
        // Load the default properties
        InputStream defaultInput = getClass().getClassLoader().getResourceAsStream(QueryConstants.DEFAULT_PROPERTIES_FILE);
        properties.load(defaultInput);
        if(filename != null) {
            Properties cmdLineProperties = new Properties();
            try {
                InputStream cmdLinePropertiesFile = Files.newInputStream(Paths.get(filename));
                cmdLineProperties.load(cmdLinePropertiesFile);
            } catch (Exception e) {
                System.out.printf("Sorry, unable to find %s, using defaults%n", filename);
                throw e;
            }
            properties.putAll(cmdLineProperties);
        }
        return properties;
    }

    /**
     * Property not found exception
     */
    public static class PropertyNotFoundException extends Exception{
        @SuppressWarnings("unused")
        public PropertyNotFoundException(String propertyName){
            super(String.format("Property %s not found",propertyName));
        }
    }

    /**
     * Property is not an integer exception
     */
    public static class PropertyNotIntegerException extends Exception{
        public PropertyNotIntegerException(String propertyName,String propertyValue){
            super(String.format("Value for property %s is %s. Should be an integer. ",propertyName,propertyValue));
        }
    }

    public void printProperties(){
        List<String> sortedPropertyNames = new Vector<>(benchProperties.stringPropertyNames());
        Collections.sort(sortedPropertyNames);
        for(String propertyName : sortedPropertyNames) {
            System.out.printf("Property %s set to %s%n", propertyName, benchProperties.getProperty(propertyName));
        }
    }

    private int getNumericProperty(String propertyName) throws PropertyNotIntegerException{
        String propertyAsString = benchProperties.getProperty(propertyName);
        int propertyAsInt;
        try {
            propertyAsInt = Integer.parseInt(propertyAsString);
        }
        catch(NumberFormatException e){
            throw new PropertyNotIntegerException(propertyName,propertyAsString);
        }
        return propertyAsInt;
    }

}
