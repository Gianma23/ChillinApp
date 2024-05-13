import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import datetime
from models.DerivedData import DerivedData
from models.Hour import Hour
from models.Day import Day
from models.Coordinate import Coordinate

cred = credentials.Certificate("../utils/credentials.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://chillinapp-a5b5b-default-rtdb.europe-west1.firebasedatabase.app/'
    })

class Map:
    """
    This class is used to store the map of coordinates.
    """

    def __init__(self):
        """
        Initialize a Map object.

        Returns:
            None
        """
        self.map = []

    def update_hotspots(self):
        """
        Update the stress scores of coordinates in the real-time Firebase database.

        Returns:
            None
        """
        for coord in self.map:
            # substitute dots with _ in the lat and long values
            latitude_key = str(coord.get_lat()).replace('.', '_')
            longitude_key = str(coord.get_long()).replace('.', '_')
            
            coord_ref = db.reference(f'Map/{latitude_key},{longitude_key}')
            if coord_ref.get() is None:
                coord_ref.set({
                    'lat': coord.get_lat(),
                    'long': coord.get_long(),
                })
                # If the coordinate does not exist in the database, create a new one
                for day in coord.get_days():
                    for hour in day.get_hours():
                        coord_ref.child('days').child(day.get_day()).child('hours').child(hour.get_hour()).set({
                            'hour': hour.get_hour(),
                            'stress_score': hour.get_stress_score(),
                        })
            else:
                # If the coordinate exists in the database, sum the existing score with the new one for each day and hour
                for day in coord.get_days():
                    for hour in day.get_hours():
                        if coord_ref.child('days').child(day.get_day()).child('hours').child(hour.get_hour()).get() is None:
                            coord_ref.child('days').child(day.get_day()).child('hours').child(hour.get_hour()).set({
                                'hour': hour.get_hour(),
                                'stress_score': hour.get_stress_score(),
                            })
                        else:
                            coord_ref.child('days').child(day.get_day()).child('hours').child(hour.get_hour()).update({
                                'stress_score': coord_ref.child('days').child(day.get_day()).child('hours').child(hour.get_hour()).get()['stress_score'] + hour.get_stress_score(),
                            })
        
        self.map = []
                

    def parse_derived_data(self, data):
        """
        Parse derived data and update the map of coordinates.

        Args:
            data (list): A list of dictionaries containing coordinate data.

        Returns:
            None
        """
        
        for derived_data in data:
            derived_data = DerivedData(derived_data['lat'], derived_data['long'], derived_data['stress_score'], derived_data['timestamp'])

            # get the YYYY-MM-DD part of the timestamp, which is expressed in milliseconds
            day = derived_data.get_timestamp() / 1000
            day = datetime.datetime.fromtimestamp(day)
            day = day.strftime('%Y-%m-%d')

            # get the HH part of the timestamp, which is expressed in milliseconds
            hour = derived_data.get_timestamp() / 1000
            hour = datetime.datetime.fromtimestamp(hour)
            hour = hour.strftime('%H')
            
            found = False
            for i in range(len(self.map)):
                if self.map[i].get_lat() == derived_data.get_lat() and self.map[i].get_long() == derived_data.get_long():
                    if derived_data.get_stress_score() > 0.6:

                        if not self.map[i].is_day(day):
                            self.map[i].add_day(Day(day, []))

                        day = self.map[i].get_day(day)

                        if not day.is_hour(hour):
                            day.add_hour(Hour(hour, 0))
                        
                        hour = day.get_hour(hour)

                        hour.set_stress_score(hour.get_stress_score() + 1)

                    found = True
                    break

            if not found:
                if derived_data.get_stress_score() > 0.6:
                    coord = Coordinate(derived_data.get_lat(), derived_data.get_long())
                    coord.add_day(Day(day, []))
                    day = coord.get_day(day)
                    day.add_hour(Hour(hour, 1))
                    self.map.append(coord)
        
        self.update_hotspots()

    def print_map(self):
        """
        Print the map of coordinates.

        Returns:
            None
        """
        for i in range(self.x):
            for j in range(self.y):
                print(self.map[i][j].get_value(), end=' ')
            print()