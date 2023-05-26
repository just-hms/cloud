import sys
import random
import time
import matplotlib.pyplot as plt
import numpy as np
import math
from sklearn.cluster import KMeans
from sklearn.datasets import make_blobs
import matplotlib.pyplot as plt
from sklearn.datasets import make_blobs
from numpy import savetxt




if __name__ == '__main__':
    
    if(len(sys.argv.size) != 2):
        print("Wrong number of arguments\n")
        sys.exit()

    path            = sys.argv[0]
    n_clusters      = sys.argv[1]



    kmeans = KMeans(n_clusters= n_clusters, random_state=0, n_init="auto").fit(generated_dataset)
    labels = kmeans.predict(generated_dataset)
    centroids  = kmeans.cluster_centers_


    filename_labels = path + "_labels.csv"
    savetxt(filename_labels, labels, delimiter=',')

    filename_centroids = path + "_centroids.csv"
    savetxt(filename_centroids, labels, delimiter=',')