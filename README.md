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

