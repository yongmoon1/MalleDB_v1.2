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

# For Compile
export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8

# MySQL Setting

sudo mysql -u root

mysql> create user iesl@localhost identified by '12345678';

mysql> grant all privileges on *.* to 'iesl'@'localhost';

mysql> create database malledb;

