# Import necessary modules and classes
import firebase_admin
from firebase_admin import db
from algorithms.keytranslator import KeyTranslator
# Firebase Admin SDK
from firebase_admin import credentials, firestore  # Sub-modules for Firebase Admin SDK
from algorithms.rule_based import RuleBasedAlgorithm  # Custom class for rule-based algorithm
from algorithms.range_based import BayesianAnalyzer  # Custom class for range-based algorithm
import time
# Define DataProcessor class
class DataProcessor:
    # Constructor
    def __init__(self, db_firestore):
        self.db_firestore = db_firestore  # Firestore database reference
        self.rule_based = RuleBasedAlgorithm()  # Rule-based algorithm object
        self.range_based = BayesianAnalyzer()# Range-based algorithm object
        self.key_translator=KeyTranslator()





# Method to retrieve recent raw data from Firestore
    def get_recent_raw_data(self, identical_data_count, previous_raw_data, MAX_IDENTICAL_DATA_REPETITIONS):
        recent_raw_data = {}  # Dictionary to store recent raw data for each account

        # Retrieve raw data from Firestore
        accounts = db.reference("account").get()

        # Check for identical data repetitions
        if recent_raw_data == previous_raw_data:
            identical_data_count += 1
            if identical_data_count >= MAX_IDENTICAL_DATA_REPETITIONS:
                print("No more new data")
                # Suspend execution before attempting to fetch new data
                time.sleep(60)  # Suspend execution for a minute
                identical_data_count = 0  # Reset repetitions count to zero
                return self.get_recent_raw_data()  # Recursively call the method to fetch new data
        else:
            identical_data_count = 0  # Reset repetitions count to zero

        previous_raw_data = recent_raw_data  # Update previous_raw_data for comparison in the next iteration

        # Translate keys in dictionary using KeyTranslator
        accounts_ref = self.key_translator.translate_keys_in_dictionary(accounts)

        # Iterate over accounts and their raw data
        for email, account_data in accounts_ref.items():
            raw_data_ref = account_data.get("RawData", {})  # If "RawData" doesn't exist, default to an empty dictionary

            recent_raw_data[email] = []  # Initialize list to store raw data for the account

            # Iterate over raw data documents and extract relevant fields
            for raw_data_id, raw_data_point in raw_data_ref.items():
                recent_raw_data[email].append({
                    "heartrateSensor": raw_data_point.get("heartRateSensor", 0),
                    "skinTemperatureSensor": raw_data_point.get("skinTemperatureSensor", 0),
                    "edaSensor": raw_data_point.get("edaSensor", 0),
                    "timestamp": raw_data_point.get("timestamp", 0),
                    "latitude": raw_data_point.get("latitude", 0),
                    "longitude": raw_data_point.get("longitude", 0)
                })

        print(recent_raw_data)
        return recent_raw_data  # Returns the recent raw data for all accounts

    # Method to create derived data from raw data
    def create_derived_data(self, accounts_dict, predata):
        results = {}  # Dictionary to store derived data for each account

        # Iterate over each account's raw data and previous data
        for (email1, account_data), (email2, pdata) in zip(accounts_dict.items(), predata.items()):
            raw_data = account_data["RawData"]  # Get raw data for the account

            # Extend initial data if it's less than 120 samples, otherwise update it
            if len(pdata["init_data"]) < 120:
                pdata["init_data"].extend(raw_data)
            else:
                pdata["init_data"] = pdata["init_data"][1:] + raw_data[:1]
                raw_data = raw_data[1:]
                data_for_range = raw_data + pdata["init_data"]  # Combine current and initial data
                posterior = self.range_based.calculate_posterior(data_for_range)  # Calculate posterior using range-based algorithm

                raw_for_score = pdata["next_data"] + account_data["RawData"]  # Combine previous and current raw data
                stress_scores, final_results = self.rule_based.rule_algorithm_general(raw_for_score)  # Calculate stress scores using rule-based algorithm

                # Determine minimum length of posterior and stress_scores lists
                min_length = min(len(posterior), len(stress_scores), len(raw_data))
                print("la lunghezza è", min)


                # Combine posterior and stress scores and add them to results
                for i in range(min_length):
                    post_data = posterior[i]
                    stress_data = stress_scores[i]
                    raw_data_single=raw_data[i]



                    result = {
                        "lower_bound": post_data.get("lower_bound", 0.0),
                        "upper_bound": post_data.get("upper_bound", 0.0),
                        "stress_score": stress_data.get("stress_score", 0.0) if stress_scores else 0.0,
                        "timestamp": raw_data_single.get("timestamp", 0.0),
                        "latitude": post_data.get("latitude", 0.0),
                        "longitude":  post_data.get("longitude", 0.0)
                    }
                    if email1 not in results:
                        results[email1] = []
                    results[email1].append(result)

                pdata["next_data"] = final_results  # Update next data for the account

        return results  # Return derived data for all accounts

    # Method to adapt recent raw data into a suitable format
    def adapt_recent_raw_data(self, raw_data):
        adapted_data = {}  # Dictionary to store adapted data for each account
        # Iterate over raw data and store it in adapted format
        for email, raw_data_list in raw_data.items():
            adapted_data[email] = {"RawData": raw_data_list}
        return adapted_data  # Return adapted data

    # Method to extract derived data specifically for Map
    def extract_derived_data_for_map(self, derived_data):
        extracted_data = []  # List to store extracted data
        # Iterate over derived data for all accounts
        for email_data in derived_data.values():
            for entry in email_data:
                # Extract relevant fields and append them to extracted data
                extracted_data.append({
                    "stress_score": entry["stress_score"],
                    "lat": entry.get("latitude", None),
                    "long": entry.get("longitude", None),
                    "timestamp": entry.get("timestamp", None)
                })
        print(extracted_data)
        return extracted_data  # Return extracted data

    # Method to add derived data to Firestore
    def add_data_to_firestore(self, data,data_prec):

        if not data:  # Check if data is empty
            print("No data to add to Firestore.")
            return

        #print(dat)
        len(data)
        print(data)


        for email, derived_data in data.items():
            email_doc_ref = self.db_firestore.collection("account").document(email)  # Reference to account document
            derived_data_collection_ref = email_doc_ref.collection("DerivedData")  # Reference to derived data collection

            # Iterate over derived data entries and add them to Firestore
            for entry in derived_data:
                # check if exists timestamp
                timestamp = entry["timestamp"]



                lower_bound = entry["lower_bound"]
                upper_bound = entry["upper_bound"]
                stress_score = entry["stress_score"]
                timestamp = entry["timestamp"]
                lower_bound = entry["lower_bound"]
                upper_bound = entry["upper_bound"]
                stress_score = entry["stress_score"]


                # Create document reference for each entry and set its data
                entry_doc_ref = derived_data_collection_ref.document(str(timestamp))
                entry_doc_ref.set({
                    "binterval": [lower_bound, upper_bound],
                     "stress_score": stress_score,
                    "timestamp": timestamp
                })
        print("Sending Data")

