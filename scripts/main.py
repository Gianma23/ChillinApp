# Import necessary modules and classes
import time  # Module for time-related functions
from data_manipulation import DataProcessor  # Custom module for data manipulation
#import firebase_admin  # Module for Firebase Admin SDK
#from firebase_admin import credentials, firestore  # Sub-modules for Firebase Admin SDK
#from Map import Map  # Custom module for mapping data
import random
import time

def generate_simulated_data(num_accounts=5, num_samples=30):
    simulated_data = {}

    for i in range(num_accounts):
        email = f"account{i+1}@example.com"
        raw_data = []

        # Generate simulated raw data for each account
        for j in range(num_samples):
            heart_rate = random.randint(60, 100)
            skin_temp = random.uniform(32.0, 37.0)
            eda = random.uniform(0.1, 5.0)
            timestamp = int(time.time()) - (num_samples - j) * 10  # Timestamp in the past
            latitude = random.uniform(-90.0, 90.0)
            longitude = random.uniform(-180.0, 180.0)

            raw_data.append({
                "heartrateSensor": heart_rate,
                "skinTemperatureSensor": skin_temp,
                "edaSensor": eda,
                "timestamp": timestamp,
                "latitude": latitude,
                "longitude": longitude
            })

        simulated_data[email] = raw_data

    return simulated_data

# Define the main routine function
def main_routine():
    # Initialize an empty dictionary to store previous data for each email
    predata = {}
    # Create an instance of the Map class
   # map = Map()
    
    # Infinite loop to continuously process data
    while True:
        db=0
        # Create a DataProcessor object with the Firestore client 'db'
        processor = DataProcessor(db)
        # Retrieve recent raw data from Firestore
        #recent_raw_data = processor.get_recent_raw_data()
        recent_raw_data=generate_simulated_data()
        
        # Iterate through the recent raw data
        for email, account_data in recent_raw_data.items():
            # If email not in predata, initialize data structures
            if email not in predata:
                init_data = []  # Initialize list for initial data
                next_data = []  # Initialize list for subsequent data
                # Create a dictionary to store initial and subsequent data
                data = {
                    "init_data": init_data,
                    "next_data": next_data
                }
                # Store data dictionary for the email in predata
                predata[email] = data

        # Process raw data to derive meaningful information
        derived_data = processor.create_derived_data(processor.adapt_recent_raw_data(recent_raw_data), predata)
        # Extract derived data specific for "Daniel"
        #extracted_data = processor.extract_derived_data_for_map(derived_data)
        print("i derived data", derived_data)
        # Parse extracted data into the Map object
        #map.parse_derived_data(extracted_data)

        # Add derived data to Firestore
        #processor.add_data_to_firestore(derived_data)

        # Wait for 30 seconds before the next iteration
        time.sleep(3)

# Initialize Firestore client
#db = firestore.client()

# Invoke the main routine function to start processing data
main_routine()
