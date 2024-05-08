import firebase_admin
import schedule
import time
from firebase_admin import credentials
from firebase_admin import db
from firebase_admin import firestore

cred = credentials.Certificate("credentials.json")
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

    # retrieve current day YYYY-MM-DD
    current_day = time.strftime('%Y-%m-%d')

    # retrieve current hour from 0 to 23
    current_hour = int(time.strftime('%H'))

    if map_data is None:
        return
    
    # Push the data to Firestore
    for key, value in map_data.items():
        doc_ref = db_firestore.collection('Map').document(key)
        doc_ref.set({
            'lat': value['lat'],
            'long': value['long'],
            'days': {
                str(current_day): {
                    'hours': {
                        str(current_hour): value['stress_score']
                    }
                }
            }
        }, merge=True)

    # Clear the data from the Realtime Database
    map_ref.delete()


# Schedule the job to run every new hour
schedule.every().hour.at(':00').do(job)
#schedule.every(15).seconds.do(job)

# Keep the script running
while True:
    schedule.run_pending()
    time.sleep(1)