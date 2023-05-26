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
    values = [
        {
            'NAME'          : "first_dataset",
            'N_FEATURES'    : 5,
            'N_SAMPLES'     : 30,
            'BLOB_CENTERS'  : 3,
            'SD'            : 0.5
        },

        {
            'NAME'          : "second_dataset",
            'N_FEATURES'    : 5,
            'N_SAMPLES'     : 30,
            'BLOB_CENTERS'  : 3,
            'SD'            : 0.5
        }
    ]


    for i in range(len(values)):
        generated_dataset, generation_labels = make_blobs(
            n_samples = values[i]['N_SAMPLES'], 
            centers = values[i]['BLOB_CENTERS'], 
            cluster_std = values[i]['SD'], 
            n_features = values[i]['N_FEATURES'], 
            random_state = 0
        )

        filename = values[i]['NAME'] + ".csv"
        savetxt(filename, generated_dataset, delimiter=',')
        
        print(generated_dataset)