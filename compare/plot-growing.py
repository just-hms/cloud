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
import seaborn as sns

def plot_data(sizes, data, outputfile, text, x_label, y_label):
    # Define the new color palette and symbol for centroids
    colors = ['#93B5C6', '#5E5768', '#F0CF65', '#DB93B0', '#DD6E42', '#588157', '#54428E']
    line_styles = ['-', '--', '-.', ':']

    sns.set(style='whitegrid', font_scale=1.2)

    for i, item in enumerate(data):
        plt.plot(sizes, item, 'o', color=colors[i], linestyle=line_styles[i], linewidth=2, markersize=8)
        plt.xscale('log')

        if text is True:
            label_padding = 0.3 
            for i in range(len(sizes)):
                plt.text(sizes[i], item[i] + label_padding, str(item[i]), ha='center', va='bottom', fontsize=10)

    # Set labels, grid, and plot aesthetics using Seaborn
    plt.xlabel(x_label, fontweight='bold')
    plt.ylabel(y_label, fontweight='bold')
    sns.despine()
    plt.grid(True, linestyle='--', alpha=0.5)
    plt.xticks(fontsize=10)
    plt.yticks(fontsize=10)

    # Save and show the plot
    plt.tight_layout()
    plt.savefig(outputfile, dpi=300)
    plt.show()


def get_stats(path, file):
    # Open the file in read mode

    filename = path + file

    with open(filename, "r") as file:
        # Read the lines
        lines = file.readlines()

    # Parse the execution time
    run_time = float(lines[0].split(": ")[-1].replace("ms", ""))
    # Parse the number of iterations
    iterations = int(lines[1].split(": ")[-1])
    return run_time, iterations







if __name__ == '__main__':
    kmeans_time = np.array([])
    hadoop_time = np.array([])
    hadoop_comb_time = np.array([])

    kmeans_iterations = []
    hadoop_iterations = []
    hadoop_comb_iterations = []

    for i in range(6):
        path = "./data/growing/growing-" + str(i + 1) + "/";

        run_time, iterations = get_stats(path, "kmeans.stats")
        kmeans_time = np.append(kmeans_time, run_time)
        kmeans_iterations.append(iterations)

        run_time, iterations = get_stats(path, "hadoop.nocomb.stats")
        hadoop_time = np.append(hadoop_time, run_time)
        hadoop_iterations.append(iterations)

        run_time, iterations = get_stats(path, "hadoop.stats")
        hadoop_comb_time = np.append(hadoop_comb_time, run_time)
        hadoop_comb_iterations.append(iterations)

    print(kmeans_time)
    print(hadoop_time)
    print(hadoop_comb_time)
    print(kmeans_iterations)
    print(hadoop_iterations)
    print(hadoop_comb_iterations)

    sizes = [100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000]

    times = []
    times.append(hadoop_time)
    times.append(hadoop_comb_time)

    percentage_time = (hadoop_comb_time - hadoop_time) / hadoop_time * 100
    percentage_array = [percentage_time]
    percentage_array = np.around(percentage_array, decimals=1)
    

    iterations = []
    iterations.append(hadoop_iterations)
    iterations.append(hadoop_comb_iterations)
    

    # read the python centroids
    folder = ".\data\growing"
    x_label = "Number of Samples"
    y_label_time = "Runtime"
    y_label_iterations ="Number of iterations"
    y_label_percentage = "Percentage runtime decrease"

    plot_data(
        sizes, times, 
        os.path.join(folder, "times_growing.png"),
        False, x_label, y_label_time
    )

    plot_data(
        sizes, percentage_array, 
        os.path.join(folder, "percentual_time.png"),
        True, x_label, y_label_percentage
    )

    # read the python centroids
    plot_data(
        sizes, iterations, 
        os.path.join(folder, "iterations_growing.png"),
        False, x_label, y_label_iterations
    )

    iterations.append(kmeans_iterations)
    times.append(kmeans_time)

    # read the python centroids
    folder = ".\data\growing"
    plot_data(
        sizes, times, 
        os.path.join(folder, "times_growing_all.png"),
        False, x_label, y_label_time
    )

    # read the python centroids
    plot_data(
        sizes, iterations, 
        os.path.join(folder, "iterations_growing_all.png"),
        False, x_label, y_label_iterations
    )
