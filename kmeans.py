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


#INSTRUCTIONS
#command line:  py kmeans.py [in_path] [out_path] [number cluster]
#
# GENERATES 3 files:
# - [out_path]_centroids.csv    :   contains the centroids coordinates
# - [out_path]_labels.csv       :   contains the labels associated to the centroids
# - [out_path]_iterations.csv   :   contains the iterations
# - [out_path]_time.csv         :   contains the time taken to run kmeans

if __name__ == '__main__':
    
    if(len(sys.argv) != 4):
        print("Wrong number of arguments\n")
        sys.exit()

    in_path         = sys.argv[1]
    out_path        = sys.argv[2]
    n_clusters      = int(sys.argv[3])

    in_filename = in_path + ".csv"

    dataset = np.loadtxt(in_filename, delimiter=";", dtype=float)

    print(dataset)

    #runs Kmeans and gets time
    start_time = time.time()

    kmeans = KMeans(n_clusters= n_clusters, random_state=0, n_init="auto").fit(dataset)
    labels = kmeans.predict(dataset)

    end_time = time.time()
    elapsed_time = end_time - start_time

    #gets data
    centroids  = kmeans.cluster_centers_
    iterations = kmeans.n_iter_

    #saves on file

    filename_centroids = out_path + "_centroids.csv"
    savetxt(filename_centroids, centroids, delimiter=';')

    filename_labels = out_path + "_labels.csv"
    savetxt(filename_labels, labels, delimiter=';')

    filename_iterations = out_path + "_iterations.csv"
    savetxt(filename_iterations, iterations, delimiter=';')

    filename_time = out_path + "_time.csv"
    savetxt(filename_time, elapsed_time, delimiter=';')