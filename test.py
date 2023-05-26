import sys
import random
import time
import matplotlib.pyplot as plt
import numpy as np
import math
from sklearn.cluster import KMeans
from sklearn.datasets import make_blobs
import matplotlib.pyplot as plt
from sklearn.datasets import make_blobs
from numpy import savetxt
import argparse
import argparse

import argparse

# Custom action to parse list from command line
class ListAction(argparse.Action):
    def __call__(self, parser, namespace, values, option_string=None):
        setattr(namespace, self.dest, values.split(','))



if __name__ == '__main__':

    # Create an ArgumentParser object
    parser = argparse.ArgumentParser(description='My Command Line Program')

    # Add the list argument
    parser.add_argument('-l', '--mylist', help='List of values', action=ListAction)

    # Add arguments
    parser.add_argument('-f', '--file', help='Input file path')
    parser.add_argument('-n', '--name', help='Your name')

    # Parse the arguments
    args = parser.parse_args()

    # Access the parsed list argument
    my_list = args.mylist

    # Print the list
    print('List:', my_list)

    # Parse the arguments
    args = parser.parse_args()

    # Access the parsed arguments
    file_path = args.file
    name = args.name

    # Print the values
    print('File:', file_path)
    print('Name:', name)

    