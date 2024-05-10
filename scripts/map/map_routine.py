import firebase_admin
import schedule
import time
from firebase_admin import credentials
from firebase_admin import db
from firebase_admin import firestore

cred = credentials.Certificate("../utils/credentials.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://chillinapp-a5b5b-default-rtdb.europe-west1.firebasedatabase.app/'
    })

db_firestore = firestore.client()

def job():
    """
    This function is used to run the job that pushes the data from the Realtime Database to Firestore.

    Returns:
        None
    """
    print('Running job')

    # Retrieve the Map collection from the Realtime Database
    map_ref = db.reference('Map')
    map_data = map_ref.get()

    if map_data is None:
        return
    
    # Push the data to Firestore
    for key, value in map_data.items():
        print(key, value)
        doc_ref = db_firestore.collection('Map').document(key)
        
        # Retrieve the existing document
        existing_doc = doc_ref.get()
        if existing_doc.exists:
            # If the document exists, add the new stress_score to the existing one
            existing_data = existing_doc.to_dict()
            for day, day_data in value['days'].items():
                for hour, hour_data in day_data['hours'].items():
                    existing_score = existing_data['days'].get(day, {}).get('hours', {}).get(hour, {}).get('stress_score', 0)
                    new_score = hour_data['stress_score']
                    hour_data['stress_score'] = existing_score + new_score

        doc_ref.set({
            'lat': value['lat'],
            'long': value['long'],
            'days': value['days']
        }, merge=True)

    # Clear the data from the Realtime Database
    map_ref.delete()


# Schedule the job to run every new hour
schedule.every().hour.at(':00').do(job)
# schedule.every(5).seconds.do(job)

# Keep the script running
while True:
    schedule.run_pending()
    time.sleep(1)