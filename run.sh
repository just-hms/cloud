OUTPUT="output-$(uuidgen)"
JAR="cloud-1.0-SNAPSHOT.jar"
SSH_KEY="~/.ssh/keys/cloud/key"

# TODO had check for edits
echo "Building..."
mvn clean package
if [ $? -ne 0 ]; then
    echo failed to build
    exit
fi

# TODO remember to cache datasets
echo "Copying .jar and specified files to the remote server"
scp -i $SSH_KEY target/$JAR $1 $2 hadoop@cloud-hms:repos
if [ $? -ne 0 ]; then
    echo failed to copy
    exit
fi

centroidsFilename="${1##*/}"
datset="${2##*/}"

echo "Running Hadoop job"
ssh hadoop@cloud-hms << EOF
    cd repos
    /opt/hadoop/bin/hdfs dfs -rm -r output

    /opt/hadoop/bin/hdfs dfs -put -f $centroidsFilename
    /opt/hadoop/bin/hdfs dfs -put -f $datset
    
    /opt/hadoop/bin/hadoop jar $JAR $centroidsFilename $datset output
EOF


# TODO something to download the log of the iterations

# echo "Download $OUTPUT"
# mkdir -p data/$OUTPUT
# scp -i $SSH_KEY -r hadoop@cloud-hms:repos/$OUTPUT data/

