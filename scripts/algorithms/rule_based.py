# Import necessary libraries
import math  # Math functions
import pandas as pd  # Data manipulation library
import numpy as np  # Numerical computing library

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
        if 2 < n < 5:
            score = 1
        elif 5 < n < 8:
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
        if 3 < n < 5:
            score = 1
        elif 5 <= n <= 6:
            score = 0.5
        else:
            score = 0
        return score  # Return score

    # Method implementing rule 3
    def rule3(self, samplings, time, n_pos):
        n_peak = samplings.index(max(samplings))  # Find index of peak
        g_peak = max(samplings)  # Find maximum value
        score = 0  # Initialize score
        time_diff = (time[n_peak] - time[n_pos])  # Calculate time difference
        # Determine score based on the value of time_diff
        if 1 <= time_diff <= 5:
            score = 1
        elif 5 < time_diff <= 15:
            score = 0.5
        else:
            score = 0
        return score, time_diff, g_peak  # Return score, time difference, and peak value

    # Method implementing rule 4
    def rule4(self, samplings, n_pos, time_diff, g_peak):
        g_pos = samplings[n_pos]  # Get value at n_pos
        m = (g_peak - g_pos) // time_diff  # Calculate slope
        rad = np.arctan(m)  # Calculate angle in radians
        angle = math.degrees(rad)  # Convert angle to degrees
        # Determine score based on the value of angle
        if angle >= 10:
            return 1
        elif 8 <= angle < 10:
            return 0.5
        else:
            return 0

    # Method implementing rule 5
    def rule5(self, data):
        new_data = []  # Initialize list to store new data
        # Iterate over the data
        for i in range(len(data)):
            stress_score = data[i]['stress_score']  # Get stress score
            timestamp = data[i]['timestamp']  # Get timestamp
            new_stress_score = stress_score  # Initialize new stress score
            # Check if stress score exceeds a threshold
            if stress_score > 0.75:
                # Iterate over subsequent data points
                for j in range(i + 1, len(data)):
                    time_diff = data[j]['timestamp'] - timestamp  # Calculate time difference
                    # If time difference is within a threshold, reset stress score
                    if 0 < time_diff <= 10000:
                        new_stress_score = 0
                        break
            # Append updated stress score and timestamp to new data
            new_data.append({'stress_score': new_stress_score, 'timestamp': timestamp})
        return new_data  # Return updated data

    # Method implementing the main rule-based algorithm
    def rule_based_algorithm_no5(self, samplings_gdr, samplings_temperature, time):
        # Define weights for different rules
        weight = {
            'rule1': 0.4,
            'rule2': 0.2,
            'rule3': 0.1,
            'rule4': 0.3
        }

        # Apply rule 1
        score1, n_pos = self.rule1(samplings_gdr[:len(samplings_gdr) // 2])
        # Apply rule 2
        score2 = self.rule2(samplings_temperature)
        # Apply rule 3
        score3, time_diff, g_peak = self.rule3(samplings_gdr, time, n_pos)
        # Apply rule 4
        score4 = self.rule4(samplings_gdr, n_pos, time_diff, g_peak)
        # Calculate final score based on weighted sum of individual scores
        final_score = weight["rule1"] * score1 + weight["rule2"] * score2 + weight["rule3"] * score3 + weight["rule4"] * score4
        return final_score  # Return final score

    # Method implementing the general rule-based algorithm
    def rule_algorithm_general(self, data):
        final_results = {}  # Initialize dictionary to store final results
        last_raw_data = []  # Initialize list to store last raw data
        # Iterate over the data
        for i in range(len(data) - 29):
            samples_gdr = [sample['edaSensor'] for sample in data[i:i + 30]]  # Extract EDA sensor data
            skin_temp = [sample['skinTemperatureSensor'] for sample in data[i:i + 30]]  # Extract skin temperature data
            time = [sample['timestamp'] for sample in data[i:i + 30]]  # Extract timestamps

            # Apply rule-based algorithm without rule 5
            score_sample = self.rule_based_algorithm_no5(samples_gdr, skin_temp, time)
            final_results[i] = {'stress_score': score_sample, 'timestamp': time[0]}  # Store stress score and timestamp

        # Apply rule 5 to final results
        final_results = self.rule5(final_results)

        # Extract last raw data
        for i in range(len(data) - 30, len(data)):
            last_raw_data.append({
                'edaSensor': data[i]['edaSensor'],
                'skinTemperatureSensor': data[i]['skinTemperatureSensor'],
                'lat': data[i]['latitude'],
                'long': data[i]['longitude'],
                'timestamp': data[i]['timestamp']
            })

        return final_results, last_raw_data  # Return final results and last raw data
