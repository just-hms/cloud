# creating output folder
# $1 output folder
# $2 dataset
# $3 K used for kmeans

mkdir -p $1

# storing used conf
echo "Dataset: $2" >> $1/.conf
echo "K: $3" >> $1/.conf

# running python
./python.sh $1 $2 $3  

# running hadoop
./hadoop.sh $1/start.csv $2 $1
