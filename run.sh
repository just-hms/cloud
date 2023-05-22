PROJECT="kmeans"
OUTPUT="output-$(uuidgen)"
JAR="$PROJECT-1.0-SNAPSHOT.jar"
CLASS="it.unipi.hadoop.KMeansHadoop"
SSH_KEY="~/.ssh/keys/cloud/key"

echo "Building..."
cd $PROJECT
mvn clean package
cd ..

echo "Copying .jar and specified files to the remote server"
scp -i $SSH_KEY $PROJECT/target/$JAR "$@" hadoop@cloud-hms:

echo "Running Hadoop job"
ssh hadoop@cloud-hms << EOF
    /opt/hadoop/bin/hadoop jar $JAR $CLASS data.csv $OUTPUT centroids.csv

    /opt/hadoop/bin/hdfs dfs -get $OUTPUT $OUTPUT
    /opt/hadoop/bin/hdfs dfs -rm -r $OUTPUT

    exit
EOF

echo "Getting the output"

cd ..
mkdir -p data/$OUTPUT
scp -i $SSH_KEY -r hadoop@cloud-hms:$OUTPUT data/
