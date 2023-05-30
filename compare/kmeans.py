import sys
import os
import numpy as np
from datetime import datetime
from sklearn.cluster import KMeans

def get_random_points(data, K):
    random_indices = np.random.choice(len(data), K, replace=False)
    return data[random_indices]

if __name__ == '__main__':
    if len(sys.argv) != 4:
        print("Wrong number of arguments\n")
        sys.exit()

    # get the inputs
    dataset = sys.argv[1]
    out_folder = sys.argv[2]
    K = int(sys.argv[3])

    # read the data
    data = np.loadtxt(dataset, delimiter=";", dtype=float)

    # Save start data
    start_centroids = get_random_points(data, K)
    start_filename = os.path.join(out_folder, "start.csv")
    np.savetxt(start_filename, start_centroids, delimiter=';')
    
    # Runs Kmeans and measures execution time
    # -------------
    start_time = datetime.now()

    kmeans = KMeans(
        n_clusters=K, random_state=0, 
        init=start_centroids, tol=0.0001, n_init=1, max_iter=30
    ).fit(data)
    
    elapsed_time = datetime.now() - start_time
    
    # get the output
    centroids = kmeans.cluster_centers_
    iterations = kmeans.n_iter_
    # -------------


    # Save the output centroids
    centroids_filename = os.path.join(out_folder, "kmeans.out.csv")
    np.savetxt(centroids_filename, centroids, delimiter=';')

    # Save the statistics
    stats_filename = os.path.join(out_folder, "kmeans.stats")
    with open(stats_filename, "w", encoding="utf-8") as out:
        out.write("Execution Time: " + str(elapsed_time.microseconds/1000) + "ms\n")
        out.write("Number of Iterations: " + str(iterations) + "\n")
