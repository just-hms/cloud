import sys
import random
import time
from multiprocessing import Process, Queue
import matplotlib.pyplot as plt
import numpy as np
import math
from sklearn.cluster import KMeans
from sklearn.datasets import make_blobs
import matplotlib.pyplot as plt
from sklearn.datasets import make_blobs
from numpy import savetxt




if __name__ == '__main__':
    NAMES           = ["first_dataset"]
    N_FEATURES      = [5]
    N_SAMPLES       = [30]
    N_CLUSTER       = [5]
    BLOB_CENTERS    = [3]
    SD              = [0.5]


    values = [
        {
            'NAME'          : "first_dataset",
            'N_FEATURES'    : 5,
            'N_SAMPLES'     : 30,
            'N_CLUSTER'     : 5,
            'BLOB_CENTERS'  : 3,
            'SD'            : 0.5
        }
    ]


    for i, name in enumerate(NAMES):
        generated_dataset, generation_labels = make_blobs(
            n_samples = N_SAMPLES[i] , 
            centers = BLOB_CENTERS[i], 
            cluster_std = SD[i], 
            n_features = N_FEATURES[i], 
            random_state = 0
        )

        kmeans = KMeans(n_clusters=N_CLUSTER[i], random_state=0, n_init="auto").fit(generated_dataset)
        labels = kmeans.predict(generated_dataset)
        centroids  = kmeans.cluster_centers_

        filename = name + ".csv"
        savetxt(filename, generated_dataset, delimiter=',')

        filename_labels = "labels_" + name + ".csv"
        savetxt(filename_labels, labels, delimiter=',')

        filename_centroids = "centroids_" + name + ".csv"
        savetxt(filename_centroids, labels, delimiter=',')
        
        print(generated_dataset)