#installer for cypher
echo "===INSTALLING CYPHER DEPENDENCIES==="
sudo apt-get -y remove docker docker-engine docker.io
sudo apt-get update 
sudo apt-get -y install apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
sudo apt-get -y install docker-ce
sudo groupadd docker 
sudo usermod -aG docker $USER
echo "===FINISHED INSTALLING CYPHER DEPENDENCIES==="

if docker ps -a ; then
    echo "Downloading Docker image 'python'..."
    docker pull python
    echo "Downloaded Docker image 'python'."
    echo "Downloading Docker image 'openjdk'..."
    docker pull openjdk
    echo "Downloaded Docker image 'openjdk'."
    echo "Downloading Docker image 'gcc'..."
    docker pull gcc
    echo "Downloaded Docker image 'gcc'."
    echo "Downloading Docker image 'cypher'..."
    docker pull hewhodocks/cypher:latest
    echo "Downloaded Docker image 'cypher'."
else
    echo "Error: Failed to pull docker images"
fi