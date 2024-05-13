# Import necessary modules and classes
import time  # Module for time-related functions
from algorithms.data_manipulation import DataProcessor
import firebase_admin# Custom module for data manipulation
from firebase_admin import db
from firebase_admin import credentials, firestore  # Sub-modules for Firebase Admin SDK
from map.models.Map import Map  # Custom module for mapping data
MAX_IDENTICAL_DATA_REPETITIONS = 5


identical_data_count = 0
previous_raw_data = None
def main_routine():
    # Initialize an empty dictionary to store previous data for each email
    predata = {}
    data_prec={}
    # Create an instance of the Map class
    map = Map()

    # Infinite loop to continuously process data
    while True:
        # Create a DataProcessor object with the Firestore client 'db'
        processor = DataProcessor(db_firestore)
        # Retrieve recent raw data from Firestore
        recent_raw_data = processor.get_recent_raw_data(identical_data_count,previous_raw_data,MAX_IDENTICAL_DATA_REPETITIONS)

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
        # Extract derived data specific for Map
        extracted_data = processor.extract_derived_data_for_map(derived_data)
        # Parse extracted data into the Map object
        map.parse_derived_data(extracted_data)


        # Add derived data to Firestore
        processor.add_data_to_firestore(derived_data,data_prec)

        # Wait for 40 seconds before the next iteration
        time.sleep(40)

# Initialize Firestore client
cred = credentials.Certificate("./utils/credentials.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://chillinapp-a5b5b-default-rtdb.europe-west1.firebasedatabase.app/'
    })
db_firestore = firestore.client()

# Invoke the main routine function to start processing data
main_routine()
