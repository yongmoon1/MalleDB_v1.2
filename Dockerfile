FROM ubuntu:20.04
MAINTAINER lambent41 <lambent41@gmail.com>

RUN apt-get update
RUN apt-get -y upgrade
# install Mysql
RUN apt-get install -y mysql-server
# install Maven
RUN apt-get install -y maven
# install Cassandra
RUN apt-get install -y curl
RUN apt-get install -y gnupg2
RUN echo "deb https://downloads.apache.org/cassandra/debian 311x main" | tee -a /etc/apt/sources.list.d/cassandra.sources.list
RUN curl https://downloads.apache.org/cassandra/KEYS | apt-key add -
RUN apt-get update
RUN DEBIAN_FRONTEND='noninteractive' apt-get install -y cassandra
# install levelDB

# install JDK
RUN apt-get install -y openjdk-8-jdk
# install MalleDB
RUN apt-get install -y git
#RUN git init
RUN git clone https://github.com/expertcoding/ChulWoo.git

WORKDIR /ChulWoo/master/NodeMaster/

CMD ["java","-cp",".:./MalleDB.jar","NodeMaster"]
