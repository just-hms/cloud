# cloud

Cloud computing project

# install

```shell
sudo apt install openjdk-8-jdk
sudo apt install maven
```

# compile

```shell
# go where the pom file is
mvn clean package
```

# deploy

```shell
# go where the pom file is
scp target/executable.jar hadoop@cloud-hms:

# copy the data files 
scp path_to_file hadoop@cloud-hms:

# login to hadoop
ssh cloud-hms
sudo su -- hadoop
cd

# start stuff
start-dfs.sh
start-yarn.sh

# copy the needed files inside the hadoop file system
hadoop fs -put pg100.txt pg100.txt

# remove the old output
hadoop fs -rm output/

# launch the executable
hadoop jar wordcount-1.0-SNAPSHOT.jar it.unipi.hadoop.WordCount pg100.txt output

# list the output files
hadoop fs -ls output

# show the formatted outputs
hadoop fs -cat output/part* | head
```