# ChulWoo

# Previous Version <<MalleDB>>
1. MalleDB also supports MySQL for storing Meta data and actual Key-Value data.
2. MalleDB also supports MySQL for storing Meta data and Leveldb for actual Key-Value data.
3. MalleDB also supports MySQL for storing Meta data and Cassandra for actual Key-Value data.
4. MalleDB using the leveldb for storing Meta data and actual Key-Value data.
5. MalleDB also supports radis for storing Meta data and actual Key-Value data.

## In MalleDBv1.0 We Added the following functionalities.

1. MalleDB supports Insert & Read (Big & Medium value size) Flushing for redis and leveldb.

* Issue 1. when MalleDB supports Cassandra for storing Meta data and actual Key-Value data.

* Issue 2. When Deleting Key-Value Data for given file, It will not delete all corresponding values from DB. --> 
  SubDB.deleteAll using the readAll.

* Issue 3. Flushing for small Data needs to perform separately in the next version.

## [Docker Hub](https://hub.docker.com/r/lambent41/iesl-project)

sudo apt update

sudo apt install apt-transport-https ca-certificates curl software-properties-common

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable"

sudo apt update

apt-cache policy docker-ce

sudo apt install docker-ce

# For Building Docker Image

- At the same directory with Dockerfile, to build an image as hello:0.1

sudo docker build --tag hello:0.1 .
- We can check all of images in the host by

sudo docker images -a

# For Pulling Images from Docker Hub

docker pull lambent41/iesl-project

# For Running a Container
docker run -it lambent41/iesl-project:v0.01 /bin/bash

# For Compile
export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8

# MySQL Setting

sudo mysql -u root

mysql> create user iesl@localhost identified by '12345678';

mysql> grant all privileges on *.* to 'iesl'@'localhost';

mysql> create database malledb;

# Jedis Implemntation

https://mvnrepository.com/artifact/redis.clients/jedis/3.5.1

Get .jar file from link above and add dependency using IntelliJ.

import redis.clients.jedis.Jedis;


<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the Project](#about-the-project)

* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [License](#license)
* [Contact](#contact)



<!-- ABOUT THE PROJECT -->
## About The Project

MalleDB is not solo database, but a combination of other best databases.
It is fully configurable based on the system requirements

Here is list of available databases:
* MySQL
* LevelDB
* Cassandra

<!-- GETTING STARTED -->
## Getting Started

In order to use the MalleDB library, you should set up the systems first

### Prerequisites

The current version of MalleDB requires below prerequisites:
* MySQL server
* Cassandra Server
* CQL version 3.4.4
* Maven
* Java Virtual Machine (JDK8)


### Installation

1. Add .jar file into project directory
2. Enjoy using MalleDB library!

<!-- USAGE EXAMPLES -->
## Usage

###Provided API:

* init() -> Initialize the database with default configuration
* init(Options options) -> Initialize the database with custom configuration
* create() -> Creates required db/keyspace and tables
* close() -> Closes the connection


* insert(String key, String value) -> inserts key-value pairs
* read(String key) -> reads the value for the given key
* update(String key, String value) -> updates the old value with the new one
* delete(String key) -> deletes the item for given key

###Configuration

The Options class is used to configure the database

#####Constructors:
* Options() -> default configuration
* Options(DB_TYPE blockdb) -> using only one sub database
* Options(DB_TYPE mdb, DB_TYPE bdb, DB_TYPE tdb) -> using different sub databases

#####Set Parameters:
* setMySQLCONF(String server, String user, String passw, String db, String[] tables)
* setCassandraConf(String server, int port, String rep_strategy, int rep_factor, String keyspace, String[] tables)
* setLevelDBConf(String db)

###Example

Here is an example of MalleDB library usage:

```sh
MalleDB malleDB = new MalleDB();
Options options = new Options(Options.DB_TYPE.MYSQL, Options.DB_TYPE.CASSANDRA, Options.DB_TYPE.LEVELDB);
malleDB.init(options);
malleDB.create();


String key = generateRandomString(20);
String value = generateRandomString(500);

malleDB.insert(key, value);

malleDB.read(key);

malleDB.delete(key);

malleDB.close();
```

<!-- LICENSE -->
## License

Distributed under the IESL License. See `LICENSE` for more information.



<!-- CONTACT -->
## Contact

Tillo - [@ti11o](https://github.com/ti11o) - mr.khikmatillo@gmail.com
