# Cloud

Cloud computing project

# Install

```shell
sudo apt install openjdk-8-jdk
sudo apt install maven
```

# 🚀 Run

```shell
./run.sh path_to_starting_points.csv path_to_dataset.csv
```

# 🥃 Old fashioned

## Compile

```shell
# go where the pom file is
mvn clean package
```

## Deploy

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
hadoop fs -rm -r output/
 
# launch the executable
hadoop jar wordcount-1.0-SNAPSHOT.jar it.unipi.hadoop.WordCount pg100.txt output

# list the output files
hadoop fs -ls output

# show the formatted outputs
hadoop fs -cat output/part* | head
```