# TODO get this as input
OUTPUT="data/$(uuidgen)"
JAR="cloud-1.0-SNAPSHOT.jar"
SSH_KEY="~/.ssh/keys/cloud/key"

SCRIPT="\033[1;30m[SCRIPT]\033[0m"
# TODO had check for edits
echo "$SCRIPT Building..."
mvn clean package
if [ $? -ne 0 ]; then
    echo failed to build
    exit
fi

echo "$SCRIPT Copying .jar and specified files to the remote server"
rsync -u -e "ssh -i $SSH_KEY" target/$JAR $1 $2 hadoop@cloud-hms:repos
if [ $? -ne 0 ]; then
    echo failed to copy
    exit
fi

centroidsFilename="${1##*/}"
datset="${2##*/}"

echo "$SCRIPT Running Hadoop job"
ssh hadoop@cloud-hms << EOF
    cd repos
    /opt/hadoop/bin/hdfs dfs -rm -r output

    /opt/hadoop/bin/hdfs dfs -put -f $centroidsFilename
    /opt/hadoop/bin/hdfs dfs -put -f $datset
    
    /opt/hadoop/bin/hadoop jar $JAR $centroidsFilename $datset output $3
EOF


echo "$SCRIPT Download hadoop results into $OUTPUT"
mkdir -p $OUTPUT
rsync -e "ssh -i $SSH_KEY" -r hadoop@cloud-hms:repos/hadoop.stats hadoop@cloud-hms:repos/hadoop.out.csv $OUTPUT

if [ $? -ne 0 ]; then
    touch $OUTPUT/hadoop.stats
    echo "failed" >> $OUTPUT/hadoop.stats
fi
