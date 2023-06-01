import sys
import os
import random
import time
from multiprocessing import Process, Queue
import numpy as np
import math
from sklearn.cluster import KMeans
from sklearn.datasets import make_blobs
import matplotlib.pyplot as plt
from sklearn.neighbors import NearestNeighbors


def assign_labels_to_points(centroids, points):
    # Create a NearestNeighbors object
    nbrs = NearestNeighbors(n_neighbors=1, algorithm='auto').fit(centroids)
    # Find the nearest centroid for each point
    distances, indices = nbrs.kneighbors(points)

    # Assign labels to points based on the nearest centroid index
    labels = [indices[i][0] for i in range(len(points))]

    return labels


def plot_data(data, centroids, labels, outputfile):
    # Define the new color palette and symbol for centroids
    colors = ['#FF0000', '#00FF00', '#0000FF', '#FFFF00', '#FF00FF', '#00FFFF', '#000000']

    # Assign colors
    label_colors = [colors[label] for label in labels]

    plt.scatter(x=data[:, 0], y=data[:, 1], color=label_colors)
    plt.scatter(x=centroids[:, 0], y=centroids[:, 1], marker='+', c='black')
    plt.savefig(outputfile, dpi=300)
    plt.show()


if __name__ == '__main__':
    if len(sys.argv) < 3:
        print("Usage: python script.py <path/to/dataset.csv> <path/to/outdfolder/>")
        sys.exit(1)

    dataset = sys.argv[1]
    folder = sys.argv[2]

    # read the data
    data = np.loadtxt(dataset, delimiter=";", dtype=np.float64)

    # read the python centroids
    python_centroids = os.path.join(folder, "kmeans.out.csv")
    centroids = np.loadtxt(python_centroids, delimiter=";", dtype=np.float64)
    labels = assign_labels_to_points(centroids, data)

    plot_data(
        data, centroids, labels, 
        os.path.join(folder, "python.png")
    )

    # read the hadoop centroids
    hadoop_centroids = os.path.join(folder, "hadoop.out.csv")
    centroids = np.loadtxt(hadoop_centroids, delimiter=";", dtype=np.float64)
    labels = assign_labels_to_points(centroids, data)

    plot_data(
        data, centroids, labels, 
        os.path.join(folder, "hadoop.png")
    )
