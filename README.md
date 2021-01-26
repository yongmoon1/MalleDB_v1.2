# ChulWoo
# [Docker Hub](https://hub.docker.com/r/lambent41/iesl-project)

sudo apt update

sudo apt install apt-transport-https ca-certificates curl software-properties-common

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable"

sudo apt update

apt-cache policy docker-ce

sudo apt install docker-ce

docker pull lambent41/iesl-project

docker run -it lambent41/iesl-project:v0.01 /bin/bash
