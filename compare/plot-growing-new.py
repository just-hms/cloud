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
    K = []
    iterations = []
    times = []
    sizes = []

    with open(path, 'r') as file:
        lines = file.readlines()
    for line in lines[1:]:  # Skip the first row
        row = line.strip().split(';')
        k = int(row[0])
        iteration = int(row[1])
        time = float(row[2])
        dataset = row[3]
        size = int(dataset.split('/')[1].split('_')[1])
        
        K.append(k)
        iterations.append(iteration)
        times.append(time)
        sizes.append(size)
        
    return K, iterations, times, sizes



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
    plt.xscale('log')
    
    if(len(y) > 1):
        plt.legend(legend_labels)

    # Save and show the plot
    plt.tight_layout()
    plt.savefig(outputfile, dpi=300)
    plt.show()


if __name__ == '__main__':
    k_py, iterations_py, times_py, sizes_py = get_stats("./data/growing/python.csv")
    k_comb, iterations_comb, times_comb, sizes_comb = get_stats("./data/growing/hadoop-comb.csv")
    k_nocomb, iterations_nocomb, times_nocomb, sizes_nocomb = get_stats("./data/growing/hadoop-nocomb.csv")

    print(iterations_comb)
    print(iterations_nocomb)

    plot_data(sizes_py, 
              [times_py, times_comb], 
              "./data/growing/graph-times-python", 
              "Number of samples", "Time (ms)", 
              ["Python", "Hadoop with combiners"])
    
    
    plot_data(sizes_py, 
              [times_py], 
              "./data/growing/graph-times-only-python", 
              "Number of samples", "Time (ms)", [])

    
    times_nocomb_np = np.array(times_nocomb)
    times_comb_np = np.array(times_comb)
    
    percentage_times = (times_comb_np - times_nocomb_np) / times_nocomb_np * 100
    
    plot_data(sizes_py, 
        [percentage_times], 
        "./data/growing/graph-percentages", 
        "Number of samples", "Percentage", 
        [])
    

    times_py_np = np.array(times_py)
    percentage_times_py = (times_comb_np - times_py_np) / times_py_np * 100
    
    plot_data(sizes_py, 
        [percentage_times_py], 
        "./data/growing/graph-percentages-py", 
        "Number of samples", "Percentage", 
        [])
    
    plot_data(sizes_py, 
            [times_comb, times_nocomb], 
            "./data/growing/graph-times", 
            "Number of samples", "Time (ms)", 
            ["Hadoop with combiners", "Hadoop without combiners"])
    

    iterations_nocomb_np =  np.array(iterations_nocomb)
    iterations_comb_np =  np.array(iterations_comb)
    iteration_times_comb = times_comb_np / iterations_comb_np
    iteration_times_nocomb = times_nocomb_np / iterations_nocomb_np
    plot_data(sizes_py, 
            [iteration_times_comb, iteration_times_nocomb], 
            "./data/growing/graph-iteration-times", 
            "Number of samples", "Time (ms)", 
            ["Hadoop with combiners", "Hadoop without combiners"])
    
    iterations_py_np =  np.array(iterations_py)
    iteration_times_py = times_py_np / iterations_py_np

    plot_data(sizes_py, 
            [iteration_times_py, iteration_times_comb], 
            "./data/growing/graph-iteration-times-py", 
            "Number of samples", "Time (ms)", 
            ["Python", "Hadoop with combiners"])
    

    percentage_times_iterations_py = (iteration_times_comb - iterations_py_np) / iterations_py_np * 100
    plot_data(sizes_py, 
        [percentage_times_iterations_py], 
        "./data/growing/graph-percentages-iterations-py", 
        "Number of samples", "Percentage", 
        [])
    
 