# Import necessary libraries
import math  # Math functions
import pandas as pd  # Data manipulation library
import numpy as np  # Numerical computing library

# Constants
RULE1_THRESHOLD_LOW = 2
RULE1_THRESHOLD_HIGH = 5
RULE2_THRESHOLD_LOW = 3
RULE2_THRESHOLD_HIGH = 5
RULE3_THRESHOLD_LOW = 1
RULE3_THRESHOLD_HIGH = 5
RULE4_THRESHOLD_LOW = 8
RULE4_THRESHOLD_HIGH = 10
STRESS_SCORE_THRESHOLD = 0.75
TIME_DIFF_THRESHOLD_LOW = 0
TIME_DIFF_THRESHOLD_HIGH = 10000
WINDOW_SIZE = 30

# Define RuleBasedAlgorithm class
class RuleBasedAlgorithm:
    # Method implementing rule 1
    def rule1(self, samplings):
        current_sample = samplings[0]  # Get the first sample
        n = 0  # Initialize counter
        score = 0  # Initialize score
        # Iterate over remaining samples
        for val in samplings[1:]:
            # If the value exceeds the current sample, exit loop
            if val > current_sample:
                break
            n += 1  # Increment counter
        # Determine score based on the value of n
        if RULE1_THRESHOLD_LOW < n < RULE1_THRESHOLD_HIGH:
            score = 1
        elif RULE1_THRESHOLD_HIGH < n < RULE2_THRESHOLD_LOW:
            score = 0.5
        else:
            score = 0
        return score, n  # Return score and n

    # Method implementing rule 2
    def rule2(self, samplings_temperature):
        current_sample = samplings_temperature[0]  # Get the first sample
        n = 0  # Initialize counter
        score = 0  # Initialize score
        # Iterate over samples starting from the 4th one
        for val in samplings_temperature[3:]:
            # If the value exceeds the current sample, exit loop
            if val > current_sample:
                break
            n += 1  # Increment counter
        # Determine score based on the value of n
        if RULE2_THRESHOLD_LOW < n < RULE2_THRESHOLD_HIGH:
            score = 1
        elif RULE2_THRESHOLD_HIGH <= n <= RULE3_THRESHOLD_LOW:
            score = 0.5
        else:
            score = 0
        return score  # Return score

    # Method implementing ru
