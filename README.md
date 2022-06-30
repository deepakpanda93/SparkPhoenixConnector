
# Spark Phoenix Connector

Access records from/to Phoenix using Spark Phoenix Connector.


## Tech Stack

* Spark
* Phoenix
## Usage/Examples

### Pre-requisites:
- Before you start using Phoenix with HBase, you must configure HBase to recognize Phoenix operations.

- Configure the following HBase properties using Cloudera Manager:

 ```bash
1. Go to the HBase service.

2. Click the Configuration tab.

3. Locate Write-Ahead Log (WAL) Codec Class property or search for hbase.regionserver.wal.codec. Add the following property value.

org.apache.hadoop.hbase.regionserver.wal.IndexedWALEditCodec

6. Set the HBase Service Advanced Configuration Snippet and HBase Client Advanced Configuration Snippet properties to enable user-defined functions. 
Locate the property or search for HBase Service Advanced Configuration Snippet (Safety Valve) for hbase-site.xml and HBase Client Advanced Configuration Snippet (Safety Valve) for hbase-site.xml.

Add the following property value:

<property>
  <name>phoenix.functions.allowUserDefinedFunctions</name>
  <value>true</value>
</property>

7. Enter a Reason for change, and then click Save Changes to commit the changes.

8. Restart the role and service when Cloudera Manager prompts you to restart.
```

### Steps to perform:

Step 1. Launch the  SQLLine.

```bash
$ /opt/cloudera/parcels/CDH/lib/phoenix/bin/sqlline.py $ZOOKEEPER_HOST:$ZOOKEEPER_PORT:$ZNODE_PARENT
```

Step 2. Creating the Phoenix table.

```bash
0: jdbc:phoenix:HOST> CREATE TABLE IF NOT EXISTS EMPLOYEE (
  ID BIGINT NOT NULL, 
  NAME VARCHAR(40), 
  AGE INTEGER, 
  SALARY FLOAT
  CONSTRAINT emp_pk PRIMARY KEY (ID)
);
```

Step 3. List all tables in Phoenix.

```bash
0: jdbc:phoenix:HOST> !tables
```
![TableList](https://github.com/deepakpanda93/SparkPhoenixConnector/blob/master/images/!tables.png?raw=true)

Step 4. Insert the data to phoenix table

```bash
$ 0: jdbc:phoenix:HOST> UPSERT INTO EMPLOYEE (ID, NAME, AGE, SALARY) VALUES (1, 'Employee1', 32, 10000.0);
. . . . . . . . . . . . . . . . . . . . . . .> UPSERT INTO EMPLOYEE (ID, NAME, AGE, SALARY) VALUES (2, 'Employee2', 2, 30000.0);
. . . . . . . . . . . . . . . . . . . . . . .> UPSERT INTO EMPLOYEE (ID, NAME, AGE, SALARY) VALUES (3, 'Employee3', 52, 60000.0);
. . . . . . . . . . . . . . . . . . . . . . .> UPSERT INTO EMPLOYEE (ID, NAME, AGE, SALARY) VALUES (4, 'Employee4', 10, 8000.0);
. . . . . . . . . . . . . . . . . . . . . . .> UPSERT INTO EMPLOYEE (ID, NAME, AGE, SALARY) VALUES (5, 'Employee5', 16, 15000.0);
1 row affected (0.144 seconds)
1 row affected (0.011 seconds)
1 row affected (0.008 seconds)
1 row affected (0.007 seconds)
1 row affected (0.007 seconds)
```

Step 5. Selecting the phoenix table data

```bash
0: jdbc:phoenix:HOST> SELECT * FROM EMPLOYEE;
```
![Employee](https://github.com/deepakpanda93/SparkPhoenixConnector/blob/master/images/AllRecords_Phoenix.png?raw=true)

Ste 6. Delete the data.

```bash
0: jdbc:phoenix:HOST> DELETE FROM EMPLOYEE WHERE ID=3;
```

Step 7. Selecting the phoenix table data

```bash
0: jdbc:phoenix:HOST> SELECT * FROM EMPLOYEE;
```
![Employee](https://github.com/deepakpanda93/SparkPhoenixConnector/blob/master/images/AfterDelete.png?raw=true)

Step 8. Quit the SQLLine shell.

```bash
0: jdbc:phoenix:HOST> !quit
```

Step 9. Login to HBase shell and Check the data in HBase

```bash
$ hbase shell
hbase:003:0> scan 'EMPLOYEE'
```
![Employee_HBase](https://github.com/deepakpanda93/SparkPhoenixConnector/blob/master/images/HBase_Record.png?raw=true)

Step 10. Quit Hbase

```bash
$ hbase:005:0> quit
```

Step 11. The Phoenix-Spark connector allows Spark to load Phoenix tables as Resilient Distributed Datasets (RDDs) or DataFrames and lets you save them back to Phoenix.

Launch the spark-shell.

```bash
$ sudo -u hdfs spark-shell --jars /opt/cloudera/parcels/CDH/lib/phoenix_connectors/phoenix5-spark-shaded-6.0.0.7.1.7.1000-141.jar,/opt/cloudera/parcels/CDH/lib/hbase_connectors/lib/hbase-spark-1.0.0.7.1.7.1000-141.jar,\
/opt/cloudera/parcels/CDH/lib/hbase_connectors/lib/hbase-spark-protocol-shaded-1.0.0.7.1.7.1000-141.jar,/opt/cloudera/parcels/CDH/lib/phoenix/phoenix-client-hbase-2.2-5.1.1.7.1.7.1000-141.jar,\
/opt/cloudera/parcels/CDH/jars/phoenix-core-5.1.1.7.1.7.1000-141.jar \
--files /etc/hbase/conf/hbase-site.xml \
--conf spark.driver.extraClassPath=/opt/cloudera/parcels/CDH/lib/phoenix_connectors/phoenix5-spark-shaded-6.0.0.7.1.7.1000-141.jar:/etc/hbase/conf/* \
--conf spark.executor.extraClassPath=/opt/cloudera/parcels/CDH/lib/phoenix_connectors/phoenix5-spark-shaded-6.0.0.7.1.7.1000-141.jar:/etc/hbase/conf/*
Setting default log level to "WARN".
To adjust logging level use sc.setLogLevel(newLevel). For SparkR, use setLogLevel(newLevel).
22/06/30 17:19:52 WARN cluster.YarnSchedulerBackend$YarnSchedulerEndpoint: Attempted to request executors before the AM has registered!
22/06/30 17:19:53 ERROR utils.KafkaUtils: Unable to add JAAS configuration for client [KafkaClient] as it is missing param [KafkaClient.loginModuleName]. Skipping JAAS config for [KafkaClient]
Spark context Web UI available at http://node4.example.com:4040
Spark context available as 'sc' (master = yarn, app id = application_1656592287520_0008).
Spark session available as 'spark'.
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 2.4.7.7.1.7.1000-141
      /_/

Using Scala version 2.11.12 (OpenJDK 64-Bit Server VM, Java 1.8.0_232)
Type in expressions to have them evaluated.
Type :help for more information.

scala> val zkUrl = "ZK_HOST:2181"
zkUrl: String = ZK_HOST:2181

scala> val tableName = "EMPLOYEE"
tableName: String = EMPLOYEE

scala> val df = spark.sqlContext.load("org.apache.phoenix.spark", Map("table" -> tableName, "zkUrl" -> zkUrl))
warning: there was one deprecation warning; re-run with -deprecation for details
22/06/30 17:20:21 WARN impl.MetricsConfig: Cannot locate configuration: tried hadoop-metrics2-phoenix.properties,hadoop-metrics2.properties
df: org.apache.spark.sql.DataFrame = [ID: bigint, NAME: string ... 2 more fields]

scala> df.show()
+---+---------+---+-------+
| ID|     NAME|AGE| SALARY|
+---+---------+---+-------+
|  1|Employee1| 32|10000.0|
|  2|Employee2|  2|30000.0|
|  4|Employee4| 10| 8000.0|
|  5|Employee5| 16|15000.0|
+---+---------+---+-------+


scala> df.filter(df("AGE") > 15).show()
+---+---------+---+-------+
| ID|     NAME|AGE| SALARY|
+---+---------+---+-------+
|  1|Employee1| 32|10000.0|
|  5|Employee5| 16|15000.0|
+---+---------+---+-------+


scala> case class Employee(id:Long, name:String, age:Short, salary:Float)
defined class Employee

scala> val employeeDF = Seq(Employee(6L, "Employee6", 49, 70000.5f),Employee(7L, "Employee7", 34, 15000.2f)).toDF()
employeeDF: org.apache.spark.sql.DataFrame = [id: bigint, name: string ... 2 more fields]

scala> employeeDF.write.format("org.apache.phoenix.spark").mode("overwrite").options(Map("table" -> tableName, "zkUrl" -> zkUrl)).save()

scala> 22/06/30 17:21:36 WARN sql.CommandsHarvester$: Missing unknown leaf node: LocalRelation [id#88L, name#89, age#90, salary#91]

22/06/30 17:21:36 WARN sql.CommandsHarvester$: Missing output entities: SaveIntoDataSourceCommand org.apache.phoenix.spark.DefaultSource@7ae2d92, Map(table -> EMPLOYEE, zkurl -> *********(redacted)), Overwrite
   +- LocalRelation [id#88L, name#89, age#90, salary#91]

22/06/30 17:21:36 WARN producer.ProducerConfig: The configuration 'key.deserializer' was supplied but isn't a known config.
22/06/30 17:21:36 WARN producer.ProducerConfig: The configuration 'value.deserializer' was supplied but isn't a known config.
22/06/30 17:21:36 WARN producer.ProducerConfig: The configuration 'max.poll.records' was supplied but isn't a known config.
22/06/30 17:21:36 WARN producer.ProducerConfig: The configuration 'zookeeper.connection.timeout.ms' was supplied but isn't a known config.
22/06/30 17:21:36 WARN producer.ProducerConfig: The configuration 'zookeeper.session.timeout.ms' was supplied but isn't a known config.
22/06/30 17:21:36 WARN producer.ProducerConfig: The configuration 'enable.auto.commit' was supplied but isn't a known config.
22/06/30 17:21:36 WARN producer.ProducerConfig: The configuration 'zookeeper.connect' was supplied but isn't a known config.
22/06/30 17:21:36 WARN producer.ProducerConfig: The configuration 'zookeeper.sync.time.ms' was supplied but isn't a known config.
22/06/30 17:21:36 WARN producer.ProducerConfig: The configuration 'session.timeout.ms' was supplied but isn't a known config.
22/06/30 17:21:36 WARN producer.ProducerConfig: The configuration 'auto.offset.reset' was supplied but isn't a known config.


scala> val dfNew = spark.sqlContext.load("org.apache.phoenix.spark", Map("table" -> tableName, "zkUrl" -> zkUrl))
warning: there was one deprecation warning; re-run with -deprecation for details
dfNew: org.apache.spark.sql.DataFrame = [ID: bigint, NAME: string ... 2 more fields]

scala> dfNew.show()
+---+---------+---+-------+
| ID|     NAME|AGE| SALARY|
+---+---------+---+-------+
|  1|Employee1| 32|10000.0|
|  2|Employee2|  2|30000.0|
|  4|Employee4| 10| 8000.0|
|  5|Employee5| 16|15000.0|
|  6|Employee6| 49|70000.5|
|  7|Employee7| 34|15000.2|
+---+---------+---+-------+
```

***Note***
- While performing the filter operation you may face the below issue:
```bash
scala> df.filter(df("AGE") > 15).show()
[Stage 1:>                                                          (0 + 1) / 1]22/06/30 16:59:27 WARN scheduler.TaskSetManager: Lost task 0.0 in stage 1.0 (TID 1, node4.example.com, executor 2): org.apache.hadoop.hbase.DoNotRetryIOException: java.lang.ClassNotFoundException: org.apache.phoenix.filter.SingleCQKeyValueComparisonFilter
	at org.apache.hadoop.hbase.protobuf.ProtobufUtil.toFilter(ProtobufUtil.java:1440)
	at org.apache.hadoop.hbase.protobuf.ProtobufUtil.toScan(ProtobufUtil.java:1004)
	at org.apache.phoenix.mapreduce.PhoenixInputSplit.readFields(PhoenixInputSplit.java:91)
	at org.apache.spark.serializer.JavaDeserializationStream.readObject(JavaSerializer.scala:75)
	at org.apache.spark.serializer.JavaSerializerInstance.deserialize(JavaSerializer.scala:114)
	at org.apache.spark.executor.Executor$TaskRunner.run(Executor.scala:381)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
Caused by: java.lang.ClassNotFoundException: org.apache.phoenix.filter.SingleCQKeyValueComparisonFilter
	at java.net.URLClassLoader.findClass(URLClassLoader.java:382)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:418)
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352)
```
- Please follow the below steps to fix the above issue.
```bash
1. Go to the HBase service.

2. Click the Configuration tab.

3. Search for “regionserver environment”

4. Add the Key-value pair. You must change the version of your jar.

Key: HBASE_CLASSPATH

Value: /opt/cloudera/parcels/CDH/lib/hbase_connectors/lib/hbase-spark-1.0.0.7.1.7.1000-141.jar:/opt/cloudera/parcels/CDH/lib/hbase_connectors/lib/hbase-spark-protocol-shaded-1.0.0.7.1.7.1000-141.jar:/opt/cloudera/parcels/CDH/jars/scala-library-2.11.12.jar:/opt/cloudera/parcels/CDH/jars/phoenix-core-5.1.1.7.1.7.1000-141.jar

```


### Working with IDE

- We can access Phoenix tables from IDE

Step 1. Create a Spark Code file to read/write data from/to Phoenix tables.

```bash
$ cat SparkPhoenixConnector.scala

import org.apache.spark.sql.SparkSession

case class Employee(id:Long, name:String, age:Short, salary:Float)
object SparkPhoenixConnector {

  def main(args: Array[String]): Unit = {

    val appName = "Spark Phoenix Integration"

    val spark: SparkSession = SparkSession.builder().master("local").appName(appName).getOrCreate()
    val zkUrl = "ZK_HOST:2181"
    val tableName = "EMPLOYEE1"
    import spark.implicits._

    val inputDF = Seq(Employee(1L, "Employee1", 29, 70000.5f),Employee(2L, "Employee2", 34, 15000.2f),Employee(3L, "Employee3", 37, 17000.2f)).toDF()
    inputDF.write.format("org.apache.phoenix.spark").mode("OverWrite").options(Map("table" -> tableName, "zkUrl" -> zkUrl)).save()

    val df = spark.sqlContext.load("org.apache.phoenix.spark", Map("table" -> tableName, "zkUrl" -> zkUrl))
    df.show()

    df.filter(df("AGE") > 30).show()

    val employeeDF = Seq(Employee(4L, "Employee4", 49, 70000.5f),Employee(5L, "Employee5", 43, 15000.2f)).toDF()
    employeeDF.write.format("org.apache.phoenix.spark").mode("OverWrite").options(Map("table" -> tableName, "zkUrl" -> zkUrl)).save()

    val dfNew = spark.sqlContext.load("org.apache.phoenix.spark", Map("table" -> tableName, "zkUrl" -> zkUrl))
    dfNew.show()

  }
}
```

Step 2. Prepare a pom.xml file for your project

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>Spark-Phoenix-Connector</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.build.targetEncoding>UTF-8</project.build.targetEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>

        <maven.compiler.plugin.version>3.8.1</maven.compiler.plugin.version>
        <maven-shade-plugin.version>3.2.3</maven-shade-plugin.version>
        <scala-maven-plugin.version>3.2.2</scala-maven-plugin.version>
        <scalatest-maven-plugin.version>2.0.0</scalatest-maven-plugin.version>

        <scala.version>2.11.8</scala.version>
        <scala.binary.version>2.11</scala.binary.version>
        <spark.version>2.4.7</spark.version>
        <jackson.version>2.11.0</jackson.version>
        <spark.scope>provided</spark.scope>
    </properties>

    <!-- Developers -->
    <developers>
        <developer>
            <id>deepakpanda93</id>
            <name>Deepak Panda</name>
            <email>deepakpanda93@gmail.com</email>
            <url>https://github.com/deepakpanda93</url>
        </developer>
    </developers>

    <!-- Repositories -->
    <repositories>
        <repository>
            <id>central</id>
            <name>Maven Central</name>
            <url>https://repo1.maven.org/maven2</url>
        </repository>

        <repository>
            <id>cldr-repo</id>
            <name>Cloudera Public Repo</name>
            <url>https://repository.cloudera.com/artifactory/cloudera-repos/</url>
        </repository>

        <repository>
            <id>hdp-repo</id>
            <name>Hortonworks Public Repo</name>
            <url>https://repo.hortonworks.com/content/repositories/releases/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>org.scala-lang</groupId>
            <artifactId>scala-library</artifactId>
            <version>${scala.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_2.11</artifactId>
            <version>${spark.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_2.11</artifactId>
            <version>${spark.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.phoenix</groupId>
            <artifactId>phoenix-spark</artifactId>
            <version>1.0</version>
            <scope>system</scope>
            <systemPath>/Users/dpanda/Documents/phoenix5-spark-shaded-6.0.0.7.1.7.1000-141.jar</systemPath>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.apache.hbase.connectors.spark/hbase-spark-protocol-shaded -->
        <dependency>
            <groupId>org.apache.hbase.connectors.spark</groupId>
            <artifactId>hbase-spark-protocol-shaded</artifactId>
            <version>1.0.0.7.1.7.1000-141</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.apache.hbase.connectors.spark/hbase-spark -->
        <dependency>
            <groupId>org.apache.hbase.connectors.spark</groupId>
            <artifactId>hbase-spark</artifactId>
            <version>1.0.0.7.1.7.1000-141</version>
        </dependency>
        <dependency>
            <groupId>org.apache.phoenix</groupId>
            <artifactId>phoenix-client-hbase-2.2</artifactId>
            <version>5.1.1.7.1.7.1000-141</version>
            <scope>system</scope>
            <systemPath>/Users/dpanda/Documents/phoenix-client-hbase-2.2-5.1.1.7.1.7.1000-141.jar</systemPath>
        </dependency>
        <dependency>
            <groupId>org.apache.hbase</groupId>
            <artifactId>hbase-client</artifactId>
            <version>1.1.2</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>

            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${maven.compiler.plugin.version}</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <compilerArgs>
                        <arg>-Xlint:all,-serial,-path</arg>
                    </compilerArgs>
                </configuration>
            </plugin>

            <!-- Scala Maven Plugin -->
            <plugin>
                <groupId>net.alchim31.maven</groupId>
                <artifactId>scala-maven-plugin</artifactId>
                <version>${scala-maven-plugin.version}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>testCompile</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <scalaVersion>${scala.version}</scalaVersion>
                    <scalaCompatVersion>${scala.binary.version}</scalaCompatVersion>
                    <checkMultipleScalaVersions>true</checkMultipleScalaVersions>
                    <failOnMultipleScalaVersions>true</failOnMultipleScalaVersions>
                    <recompileMode>incremental</recompileMode>
                    <charset>${project.build.sourceEncoding}</charset>
                    <args>
                        <arg>-unchecked</arg>
                        <arg>-deprecation</arg>
                        <arg>-feature</arg>
                    </args>
                    <jvmArgs>
                        <jvmArg>-Xss64m</jvmArg>
                        <jvmArg>-Xms1024m</jvmArg>
                        <jvmArg>-Xmx1024m</jvmArg>
                        <jvmArg>-XX:ReservedCodeCacheSize=1g</jvmArg>
                    </jvmArgs>
                    <javacArgs>
                        <javacArg>-source</javacArg>
                        <javacArg>${java.version}</javacArg>
                        <javacArg>-target</javacArg>
                        <javacArg>${java.version}</javacArg>
                        <javacArg>-Xlint:all,-serial,-path</javacArg>
                    </javacArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```



## Run Locally

Run the Spark job from IDE

```bash
I used InteliJ to run the project. But one can build the project, deploy the JAR on the cluster and execute using spark-submit
```




## Screenshots

### Initial records of Phoenix table while working with IDE (Employee1 table)

![Employee1_Initial](https://github.com/deepakpanda93/SparkPhoenixConnector/blob/master/images/Employee1_Initial.png?raw=true)

### Final records of Phoenix table while working with IDE (Employee1 table)

![Employee1_Final](https://github.com/deepakpanda93/SparkPhoenixConnector/blob/master/images/Employee1_Final.png?raw=true)

### Final output in IDE terminal

![IDE_Output](https://github.com/deepakpanda93/SparkPhoenixConnector/blob/master/images/Spark_Output.png?raw=true)

## 🚀 About Me

## Hi, I'm Deepak! 👋

I'm a Big Data Engineer...




## 🔗 Links
[![portfolio](https://img.shields.io/badge/my_portfolio-000?style=for-the-badge&logo=ko-fi&logoColor=white)](https://deepakpanda93.gitbook.io/integrated-spark)
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/deepakpanda93)

