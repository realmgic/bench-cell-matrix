## Cell Matrix (in memory) Benchmark Testing

This repository is doing a benchmark using List/Map operations in Aerospike. There are 2 broad functionalities captured
1. Insert dummy data into Aerospike
2. Run get operations on the data

### Data Model for Dummy Data
The dummy data generated has the following model. Each node-id has multiple neighbors. Each neighbor has in turn multiple attributes as a map.

   *cell-id -> \[floats\]*

TODO: alternative:
    *cell-id -> \{row:\[float\]}*

The number of floats is defined using properties.

### Properties

Most of the required constants for this benchmarking exercise are set in a properties file, *bench.properties*, found under *src/main/resources* in the codebase.

The properties and default values are as follows

```
AEROSPIKE_SERVER_NAME=localhost
AEROSPIKE_NAMESPACE=test
AEROSPIKE_PORT=3000
AEROSPIKE_SET=graphSet
MIN_CLIENT_CONNECTIONS_PER_NODE=100
MAX_CLIENT_CONNECTIONS_PER_NODE=100

# number of cells
START_CELL_ID=100000
END_CELL_ID=150000

# number of floats
MAX_CELL_ROWS=255
MAX_CELL_COLUMNS=255

# number of items to read from the matrix in each query
READ_CELL_ROWS=10
READ_CELL_COLUMNS=100
```

It should be fairly obvious what the interpretation of each of these properties is.

These can be overriden, by creating a properties file containing the new values and referencing it using the ```-d``` flag. So you could create a file containing 

`
READ_CELL_ROWS=3
`

& locate it at ```/this/that/myproperties``` and reference it on the command line invocation using ```-d /this/that/myproperties```. This would have the effect of overriding the default value of ```READ_CELL_ROWS```.


### Data Insertion

Insert records using the following command 

`java -cp BenchmarkTest-1.0-SNAPSHOT-jar-with-dependencies.jar com.aerospike.java.example.InsertDataMainMultiThread -z <THREAD_COUNT> -d <PROPERTIES_FILE>`

The default thread count is 1 & the properties file will default to *bench.properties*, found under *src/main/resources* in the codebase.

### Queries on Data
The next step is to run multiple parallel queries on the data using the following command.

`java -cp BenchmarkTest-1.0-SNAPSHOT-jar-with-dependencies.jar com.aerospike.java.example.GetCellDataMainMultiThread -z <THREADCOUNT> -d <PROPERTIES_FILE> -i <ITERATIONS> -s -x`

The default thread count is 1. 
The properties file will default to *bench.properties*, found under *src/main/resources* in the codebase. 
Default iterations = 10
-x will show additional debug statements

The work will be split equally between the available threads. The tool will report on average duration per query, and overall.

