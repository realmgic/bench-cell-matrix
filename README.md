## Secondary Index Benchmark Testing

This repository enables doing a benchmark using secondary indexes in Aerospike. There are 2 broad functionalities captured
1. Insert dummy data into Aerospike
2. Run Secondary Index Queries on this data

### Data Model for Dummy Data
The dummy data generated has the following model. Each node-id has multiple neighbors. Each neighbor has in turn multiple attributes as a map.

   *node-id1 -> \[neighbor1: { attribute1: attr1val, attribute2: attr2val },
                 neighbor2: { attribute3: attr1val}\]*

### Properties

Most of the required constants for this benchmarking exercise are set in a properties file, *sibench.properties*, found under *src/main/resources* in the codebase.

The properties and default values are as follows

`
AEROSPIKE_SERVER_NAME=localhost
AEROSPIKE_NAMESPACE=test
AEROSPIKE_PORT=3000
AEROSPIKE_SET=graphSet
START_CELL_ID=1
END_CELL_ID=10
AVERAGE_NEIGHBOUR_COUNT=10
AVERAGE_ATTRIBUTE_COUNT=10
ATTRIBUTE_VALUE_STRING_LENGTH=4
MIN_CLIENT_CONNECTIONS_PER_NODE=100
MAX_CLIENT_CONNECTIONS_PER_NODE=100
`

It should be fairly obvious what the interpretation of each of these properties is.

These can be overiden, by creating a properties file containing the new values and referencing it using the ```-d``` flag. So you could create a file containing 

`
END_NODE_ID=1000
`

& locate it at ```/this/that/myproperties``` and reference it on the command line invocation using ```-d /this/that/myproperties```. This would have the effect of overriding the default value of ```END_NODE_ID```.


### Data Insertion

Insert records using the following command 

`java -cp BenchmarkTest-1.0-SNAPSHOT-jar-with-dependencies.jar com.aerospike.java.example.InsertDataMainMultiThread -z <THREAD_COUNT> -d <PROPERTIES_FILE>`

The default thread count is 1 & the properties file will default to *sibench.properties*, found under *src/main/resources* in the codebase.

### Queries on Data
The next step is to run multiple parallel queries on the data using the following command.

`java -cp BenchmarkTest-1.0-SNAPSHOT-jar-with-dependencies.jar com.aerospike.java.example.GetCellDataMainMultiThread -z <THREADCOUNT> -d <PROPERTIES_FILE> -i <ITERATIONS> -s -x`

The default thread count is 1. 
The properties file will default to *sibench.properties*, found under *src/main/resources* in the codebase. 
Default iterations = 10
-x will show additional debug statements

The work will be split equally between the available threads. The tool will report on average duration per query, and overall.

