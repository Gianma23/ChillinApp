# Import necessary modules and classes
#import firebase_admin  # Firebase Admin SDK
#from firebase_admin import credentials, firestore  # Sub-modules for Firebase Admin SDK
from rule_based import RuleBasedAlgorithm  # Custom class for rule-based algorithm
from range_based import BayesianAnalyzer  # Custom class for range-based algorithm

# Define DataProcessor class
class DataProcessor:
    # Constructor
    def __init__(self, db):
        self.db = db  # Firestore database reference
        self.rule_based = RuleBasedAlgorithm()  # Rule-based algorithm object
        self.range_based = BayesianAnalyzer()  # Range-based algorithm object

    # Method to retrieve recent raw data from Firestore
    def get_recent_raw_data(self):
        recent_raw_data = {}  # Dictionary to store recent raw data for each account

        # Query Firestore for recent raw data for each account
        accounts_ref = self.db.collection("account")
        accounts = accounts_ref.get()
        for account in accounts:
            email = account.id  # Get email of the account
            raw_data_ref = self.db.collection("account").document(email).collection("RawData")  # Reference to raw data collection
            query = raw_data_ref.order_by("timestamp", direction=firestore.Query.DESCENDING).limit(30)  # Query to retrieve latest 30 raw data entries
            raw_data = query.stream()  # Stream through the query results

            recent_raw_data[email] = []  # Initialize list to store raw data for the account
            # Iterate over raw data documents and extract relevant fields
            for doc in raw_data:
                raw_data_point = doc.to_dict()  # Convert raw data document to dictionary
                # Append relevant fields to recent raw data list
                recent_raw_data[email].append({
                    "heartrateSensor": raw_data_point.get("heartrateSensor", 0),
                    "skinTemperatureSensor": raw_data_point.get("skinTemperatureSensor", 0),
                    "edaSensor": raw_data_point.get("edaSensor", 0),
                    "timestamp": raw_data_point.get("timestamp", 0),
                    "latitude": raw_data_point.get("latitude", None),
                    "longitude": raw_data_point.get("longitude", None)
                })

        return recent_raw_data  # Return recent raw data for all accounts

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
                min_length = min(len(posterior), len(stress_scores))

                # Combine posterior and stress scores and add them to results
                for i in range(min_length):
                    post_data = posterior[i]
                    stress_data = stress_scores[i]

                    result = {
                        "lower_bound": post_data.get("lower_bound", 0),
                        "upper_bound": post_data.get("upper_bound", 0),
                        "stress_score": stress_data.get("stress_score", 0) if stress_scores else 0,
                        "timestamp": post_data.get("timestamp", 0)
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
        return extracted_data  # Return extracted data

    # Method to add derived data to Firestore
    def add_data_to_firestore(self, data):
        if not data:  # Check if data is empty
            print("No data to add to Firestore.")
            return

        for email, derived_data in data.items():
            email_doc_ref = self.db.collection("Accounts").document(email)  # Reference to account document
            derived_data_collection_ref = email_doc_ref.collection("DerivedData")  # Reference to derived data collection

            # Iterate over derived data entries and add them to Firestore
            for entry in derived_data:
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

