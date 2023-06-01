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
    def __init__(self, name : str, n_features : int, n_samples : int, blob_centers : int, sd : double, box : tuple[double , double]):
        self.name = name
        self.n_features = n_features
        self.n_samples = n_samples
        self.blob_centers = blob_centers
        self.sd = sd
        self.box = box
        
def generate_dataset(values : list[DatasetSpecs], folder_path : str):
    for value in values:
        generated_dataset, generation_labels = make_blobs(
            n_samples=value.n_samples,
            centers=value.blob_centers,
            cluster_std=value.sd,
            n_features=value.n_features,
            random_state=0,
            center_box=value.box
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
        # DatasetSpecs(
        #     name="example-non-overlapping.csv",
        #     n_features=2,
        #     n_samples=10_000,
        #     blob_centers=[[500, 500], [-500, -500], [-500, 500], [500, -500]],
        #     sd=100,
        #     box=[-1000, 1000]
        # ),
        # DatasetSpecs(
        #     name="example-overlapping.csv",
        #     n_features=2,
        #     n_samples=10_000,
        #     blob_centers=[[250, 250], [-250, -250], [-250, 250], [250, -250]],
        #     sd=500,
        #     box=[-1000, 1000]
        # )
        
    ]

    generate_dataset(values, folder_path)
