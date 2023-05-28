# creating output folder
mkdir -p $1

# storing used conf
mkdir -p $1
echo "Dataset: $2" >> $1/.conf
echo "K: $3" >> $1/.conf

# running python
python3 compare/kmeans.py $2 $1 $3  

# running hadoop
./hadoop.sh $1/start.csv $2 $1
