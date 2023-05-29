import sys
import os
import random
import time
from multiprocessing import Process, Queue
import numpy as np
import math
from sklearn.cluster import KMeans
from sklearn.datasets import make_blobs

class DatasetSpecs:
    def __init__(self, name : str, n_features : int, n_samples : int, blob_centers : int, sd : float):
        self.name = name
        self.n_features = n_features
        self.n_samples = n_samples
        self.blob_centers = blob_centers
        self.sd = sd
        
def generate_dataset(values : list[DatasetSpecs], folder_path : str):
    for value in values:
        generated_dataset, generation_labels = make_blobs(
            n_samples=value.n_samples,
            centers=value.blob_centers,
            cluster_std=value.sd,
            n_features=value.n_features,
            random_state=0
        )

        filename = os.path.join(folder_path, value.name)
        np.savetxt(filename, generated_dataset, delimiter=';')

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print("Usage: python script.py <folder_path>")
        sys.exit(1)

    folder_path = sys.argv[1]

    if not os.path.exists(folder_path):
        print("Folder does not exist.")
        sys.exit(1)

    values = [
        DatasetSpecs(
            name="first_dataset.csv",
            n_features=10,
            n_samples=1_000,
            blob_centers=7,
            sd=0.5
        ),
        DatasetSpecs(
            name="second_dataset.csv",
            n_features=5,
            n_samples=30,
            blob_centers=3,
            sd=0.5
        ),
        DatasetSpecs(
            name="big.csv",
            n_features=10,
            n_samples=5_000,
            blob_centers=18,
            sd=0.9
        ),      
    ]

    generate_dataset(values, folder_path)
