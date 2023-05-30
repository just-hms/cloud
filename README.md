# Cloud

Cloud computing project

# Install

```shell
sudo apt install openjdk-8-jdk
sudo apt install maven
```

# 🚀 Run

```shell
./run.sh path/to/outputfolder/ path/to/dataset.csv K 
```

## Python

### `dataset_generator.py`

```shell
py dataset_generator.py path/to/output/folder
```

### `kmeans.py`

```shell
py kmeans.py path/to/dataset.csv path/to/output/folder 5 
```

## Hadoop

```shell
./hadoop.sh path/to/starting_points.csv path/to/dataset.csv
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