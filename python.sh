# TODO get this as input
SCRIPT="\033[1;30m[PYTHON.SH]\033[0m"
SSH_KEY="~/.ssh/keys/cloud/key"


exePath="compare/kmeans.py"
exeName="kmeans.py"
dataset="${2##*/}"
OUTPUT="$1"

# TODO check if the vpn is working
echo "$SCRIPT Copying .py and specified files to the remote server"
rsync -u -e "ssh -i $SSH_KEY" $exePath $2 hadoop@cloud-hms:repos
if [ $? -ne 0 ]; then
    echo failed to copy
    exit
fi

echo "$SCRIPT Running Python job"
ssh hadoop@cloud-hms << EOF
    cd repos

    python3 $exeName $dataset . $3
EOF


echo "$SCRIPT Download python results into $OUTPUT"
rsync -e "ssh -i $SSH_KEY" -r \
    hadoop@cloud-hms:repos/kmeans.stats \
    hadoop@cloud-hms:repos/start.csv \
    hadoop@cloud-hms:repos/kmeans.out.csv \
    $OUTPUT

if [ $? -ne 0 ]; then
    touch $OUTPUT/kmeans.stats
fi
