import sys
import os
import random
import time
from multiprocessing import Process, Queue
import numpy as np
import math
from sklearn.cluster import KMeans
from sklearn.datasets import make_blobs

def generate_random_points(num_points, num_features):
    min_value = -750
    max_value = 750
    
    # Generate random points
    random_points = np.random.uniform(min_value, max_value, size=(num_points, num_features))
    
    return random_points

class DatasetSpecs:
    def __init__(self, name : str, n_features : int, n_samples : int, blob_centers : int, sd : float, box : tuple[float , float]):
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
        # ),
        # DatasetSpecs(
        #     name="dataset_100_10_5.csv",
        #     n_features=10,
        #     n_samples=100,
        #     blob_centers=[
        #         [250, 250, 250, 250, 250, 250, 250, 250, 250, 250], 
        #         [250, 250, 250, 250, 250, -250, -250, -250, -250, -250],
        #         [-250, -250, -250, -250, -250, 250, 250, 250, 250, 250],
        #         [-250, 250, -250, 250, -250, 250, -250, 250, -250, 250],
        #         [-250, -250, -250, -250, -250, -250, -250, -250, -250, -250]
        #     ],
        #     sd=100,
        #     box=[-10, 10]
        # ),
        # DatasetSpecs(
        #     name="dataset_1000_10_5.csv",
        #     n_features=10,
        #     n_samples=1_000,
        #     blob_centers=[
        #         [250, 250, 250, 250, 250, 250, 250, 250, 250, 250], 
        #         [250, 250, 250, 250, 250, -250, -250, -250, -250, -250],
        #         [-250, -250, -250, -250, -250, 250, 250, 250, 250, 250],
        #         [-250, 250, -250, 250, -250, 250, -250, 250, -250, 250],
        #         [-250, -250, -250, -250, -250, -250, -250, -250, -250, -250]
        #     ],
        #     sd=250,
        #     box=[-1000, 1000]
        # ),
        # DatasetSpecs(
        #     name="dataset_10000_10_5.csv",
        #     n_features=10,
        #     n_samples=10_000,
        #     blob_centers=[
        #         [250, 250, 250, 250, 250, 250, 250, 250, 250, 250], 
        #         [250, 250, 250, 250, 250, -250, -250, -250, -250, -250],
        #         [-250, -250, -250, -250, -250, 250, 250, 250, 250, 250],
        #         [-250, 250, -250, 250, -250, 250, -250, 250, -250, 250],
        #         [-250, -250, -250, -250, -250, -250, -250, -250, -250, -250]
        #     ],
        #     sd=250,
        #     box=[-1000, 1000]
        # ),
        # DatasetSpecs(
        #     name="dataset_100000_10_5.csv",
        #     n_features=10,
        #     n_samples=100_000,
        #     blob_centers=[
        #         [250, 250, 250, 250, 250, 250, 250, 250, 250, 250], 
        #         [250, 250, 250, 250, 250, -250, -250, -250, -250, -250],
        #         [-250, -250, -250, -250, -250, 250, 250, 250, 250, 250],
        #         [-250, 250, -250, 250, -250, 250, -250, 250, -250, 250],
        #         [-250, -250, -250, -250, -250, -250, -250, -250, -250, -250]
        #     ],
        #     sd=100,
        #     box=[-1000, 1000]
        # ),
        # DatasetSpecs(
        #     name="dataset_1000000_10_5.csv",
        #     n_features=10,
        #     n_samples=1_000_000,
        #     blob_centers=[
        #         [250, 250, 250, 250, 250, 250, 250, 250, 250, 250], 
        #         [250, 250, 250, 250, 250, -250, -250, -250, -250, -250],
        #         [-250, -250, -250, -250, -250, 250, 250, 250, 250, 250],
        #         [-250, 250, -250, 250, -250, 250, -250, 250, -250, 250],
        #         [-250, -250, -250, -250, -250, -250, -250, -250, -250, -250]
        #     ],
        #     sd=250,
        #     box=[-1000, 1000]
        # ),
        # DatasetSpecs(
        #     name="dataset_10000000_10_5.csv",
        #     n_features=10,
        #     n_samples=10_000_000,
        #     blob_centers=[
        #         [250, 250, 250, 250, 250, 250, 250, 250, 250, 250], 
        #         [250, 250, 250, 250, 250, -250, -250, -250, -250, -250],
        #         [-250, -250, -250, -250, -250, 250, 250, 250, 250, 250],
        #         [-250, 250, -250, 250, -250, 250, -250, 250, -250, 250],
        #         [-250, -250, -250, -250, -250, -250, -250, -250, -250, -250]
        #     ],
        #     sd=250,
        #     box=[-1000, 1000]
        # ),
        DatasetSpecs(
            name="dataset_100_000_10_10.csv",
            n_features=10,
            n_samples=500_000,
            blob_centers=[
                  [250, 250, 250, 250, 250, 250, 250, 250, 250, 250],
                  [-250, -250, -250, -250, -250, -250, -250, -250, -250, -250],
                  [250, 250, 250, 250, 250, -250, -250, -250, -250, -250],
                  [-250, -250, -250, -250, -250, 250, 250, 250, 250, 250],
                  [-250, 250, -250, 250, -250, 250, -250, 250, -250, 250],
                  [250, -250, 250, -250, 250, -250, 250, -250, 250, -250],
                  [-250, -250, -250, -250, -250, -250, -250, 250, 250, 250],
                  [250, 250, 250, -250, -250, -250, -250, -250, -250, -250],
                  [250, -250, 250, -250, 250, 250, -250, 250, -250, 250],
                  [-250, 250, -250, 250, -250, -250, 250, -250, 250, -250]
               ],
            sd=100,
            box=[-1000, 1000]
        ),
    ]

    generate_dataset(values, folder_path)
