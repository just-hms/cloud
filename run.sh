PROJECT="kmeans"
OUTPUT="output-$(uuidgen)"
JAR="$PROJECT-1.0-SNAPSHOT.jar"
CLASS="it.unipi.hadoop.KMeansHadoop"
SSH_KEY="~/.ssh/keys/cloud/key"

echo "Building..."
cd $PROJECT
mvn clean package
if [ $? -ne 0 ]; then
    echo failed to build
    exit
fi
cd ..

echo "Copying .jar and specified files to the remote server"
scp -i $SSH_KEY $PROJECT/target/$JAR $@ hadoop@cloud-hms:repos
if [ $? -ne 0 ]; then
    echo failed to copy
    exit
fi

echo "Running Hadoop job"
ssh hadoop@cloud-hms << EOF
    cd repos
    /opt/hadoop/bin/hadoop jar $JAR $CLASS centroids.csv data.csv $OUTPUT

    /opt/hadoop/bin/hdfs dfs -get $OUTPUT $OUTPUT
    /opt/hadoop/bin/hdfs dfs -rm -r $OUTPUT
EOF

echo "Download $OUTPUT"
mkdir -p data/$OUTPUT
scp -i $SSH_KEY -r hadoop@cloud-hms:repos/$OUTPUT data/

echo "Removing $OUTPUT from the remote machine"
ssh hadoop@cloud-hms "rm -r repos/$OUTPUT"

