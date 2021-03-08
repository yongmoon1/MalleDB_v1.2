# ChulWoo

# Previous Version <<MalleDB>>
1. MalleDB also supports PostgreSQL, MySQL for storing Meta data and MySQL, PostgreSQL LevelDB, Cassandra, Redis for 
   actual 
   Key-Value data.
2. MalleDB using the leveldb, redis for storing Meta data and actual Key-Value data.
3. When Deleting Key-Value Data for given file, It will delete all corresponding values from DB.
4. MalleDB also supports redis and leveldb for storing Meta data and actual Key-Value data. (+ pipelining)
5. MalleDB supports File, MetaFile Insert, Read, Update, Delete.
6. MalleDB supports Direct Instructions for Redis, LevelDB and Cassandra.
7. MalleDB supports Query Methods (select, execute, flush) for MySQL and PostgreSQL.

## In MalleDBv1.2 We Added the following functionalities.

* Issue 1. When MalleDB supports Cassandra for storing Meta data and actual Key-Value data, It doesn't work well.
* Issue 2. MalleDB supports Small Files and Big Filese for Inserting, Updating, and Deleting in local file system.
* Issue 3. MalleDB supports Small Files and Big Filese for Inserting, Updating, and Deleting in API Server.
* Issue 4. YCSB supports for existing MalleDB.
* Issue 5. MalleDB supports fault tolerance using Erasure Coding.
* Issue 6. MalleDB supports data Deduplication.
* Issue 7. MalleDB supports data compression.
* Issue 8. MalleDB supports garbage collection.
* Issue 9. MalleDB supports partial update for small, medium, big data in general.
* Issue 10. MalleDB supports distributed storage gateway.
* Issue 11. MalleDb supports semantic storage tagging such as time-series data, JEO data, video data and audio data.
* Issue 12. MalleDB supports smart classification and deep learning functions.

# MalleDB will deploy in AWS Kubernetes Environment.

# For Compile
export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8

# MySQL Setting

sudo mysql -u root

mysql> create user iesl@localhost identified by '12345678';

mysql> grant all privileges on *.* to 'iesl'@'localhost';

mysql> create database malledb;

