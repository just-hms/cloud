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




if __name__ == '__main__':

    file = "example-overlapping"
    path = "./dataset/"
    path_centroids = "./hadoop.out.csv"

    filename = path + file + ".csv"


    # read the data
    data = np.loadtxt(filename, delimiter=";", dtype=float)
    print(data)

    # read the centroids
    centroids = np.loadtxt(path_centroids, delimiter=";", dtype=float)
    print(centroids)

    labels = assign_labels_to_points(centroids, data)

    #assign colors
    colors = ['r', 'g', 'b', 'y', 'm', 'c', 'k']
    label_colors = [colors[label] for label in labels]

    plt.scatter(x = data[:, 0], y = data[:, 1], color = label_colors)
    plt.scatter(x = centroids[:, 0], y = centroids[:, 1])
    plt.savefig('./images/' + file + 'hadoop.png', dpi=300)
    plt.show()

    plt.scatter(x = centroids[:, 0], y = centroids[:, 1])
    plt.savefig('./images/' + file + 'centroids.png', dpi=300)
    plt.show()