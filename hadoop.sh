JAR="cloud-1.0-SNAPSHOT.jar"
SSH_KEY="~/.ssh/keys/cloud/key"
SCRIPT="\033[1;30m[HADOOP.SH]\033[0m"

centroidsFilename="${1##*/}"
dataset="${2##*/}"
OUTPUT=$3

echo "$SCRIPT Building..."
mvn clean package
if [ $? -ne 0 ]; then
    echo failed to build
    exit
fi

echo "$SCRIPT Copying .jar and specified files to the remote server"
rsync -u -e "ssh -i $SSH_KEY" target/$JAR $1 hadoop@cloud-hms:repos
if [ $? -ne 0 ]; then
    echo failed to copy
    exit
fi

rsync -e "ssh -i $SSH_KEY" $1 hadoop@cloud-hms:repos
if [ $? -ne 0 ]; then
    echo failed to copy
    exit
fi

echo "$SCRIPT Running Hadoop job"
ssh hadoop@cloud-hms << EOF
    cd repos
    /opt/hadoop/bin/hadoop fs -rm -r output
    /opt/hadoop/bin/hadoop fs -put -f $centroidsFilename
    /opt/hadoop/bin/hadoop fs -put -f $dataset
    
    /opt/hadoop/bin/hadoop jar $JAR $centroidsFilename $dataset output
EOF


echo "$SCRIPT Download hadoop results into $OUTPUT"
mkdir -p $OUTPUT
rsync -e "ssh -i $SSH_KEY" -r hadoop@cloud-hms:repos/hadoop.stats hadoop@cloud-hms:repos/hadoop.out.csv $OUTPUT

if [ $? -ne 0 ]; then
    touch $OUTPUT/hadoop.stats
fi