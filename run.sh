# $1 outputfolder
# $2 dataset
# $3 K

mkdir -p $1
python3 compare/kmeans.py $2 $1 $3  
./hadoop.sh $1/start.csv $2 $1
