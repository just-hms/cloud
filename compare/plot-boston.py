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
import csv


def get_stats(path):
    # Open the file in read mode
    k_list = []
    iterations_list = []
    times_list = []

    with open(path, 'r') as file:
        reader = csv.reader(file, delimiter=';')
        next(reader)  # Skip the first row
        for row in reader:
            k_list.append(int(row[0].strip()))
            iterations_list.append(int(row[1].strip()))
            times_list.append(int(row[2].strip()))
    return k_list, iterations_list, times_list



def plot_data(x, y, outputfile, x_label, y_label, legend_labels):
    # Define the new color palette and symbol for centroids
    colors = ['#93B5C6', '#5E5768', '#F0CF65', '#DB93B0', '#DD6E42', '#588157', '#54428E']
    line_styles = ['-', '--', '-.', ':']

    sns.set(style='whitegrid', font_scale=1.2)

    for i, item in enumerate(y):
        plt.plot(x, item, 'o', color=colors[i], linestyle=line_styles[i], linewidth=2, markersize=8)

    # Set labels, grid, and plot aesthetics using Seaborn
    plt.xlabel(x_label, fontweight='bold')
    plt.ylabel(y_label, fontweight='bold')
    sns.despine()
    plt.grid(True, linestyle='--', alpha=0.5)
    plt.xticks(fontsize=10)
    plt.yticks(fontsize=10)

    
    if(len(y) > 1):
        plt.legend(legend_labels)

    # Save and show the plot
    plt.tight_layout()
    plt.savefig(outputfile, dpi=300)
    plt.show()


if __name__ == '__main__':
    k, iterations, times = get_stats("./data/boston/hadoop-nocomb.csv")
    k_comb, iterations_comb, times_comb = get_stats("./data/boston/hadoop-comb.csv")

    new_times = np.array(times_comb)
    old_times = np.array(times)
    percentage_time = (new_times - old_times) / old_times * 100
    
    new_iterations = np.array(iterations_comb)
    old_iterations = np.array(iterations)

    print(new_iterations)
    print(new_times)
    iteration_time_new = new_times / new_iterations
    iteration_time_old = old_times / old_iterations

    iteration_times = [iteration_time_old, iteration_time_new]
    iteration_time_percentage = (iteration_time_new - iteration_time_old) / iteration_time_old * 100

    time_list = [times, times_comb]
    plot_data(k, [percentage_time], "./data/boston/boston-percentage", "K", "Percentage", [])
    plot_data(k, [iteration_time_percentage], "./data/boston/boston-iteration-percentage", "K", "Percentage", [])

    iteration_time_comb = iteration_time_new.tolist()
    plot_data(k, [times_comb, iteration_time_comb], "./data/boston/boston-times", "K", "Time (ms)", ["Total runtime", "Time per iteration"])
    plot_data(k, iteration_times, "./data/boston/boston-iteration-times", "K", "Time (ms)", ["Without combiners", "With combiners"])
 