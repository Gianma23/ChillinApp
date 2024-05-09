import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

cred = credentials.Certificate("../utils/credentials.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://chillinapp-a5b5b-default-rtdb.europe-west1.firebasedatabase.app/'
    })

class Coordinate: 
    """
    This class is used to store the coordinates and their stress score.
    """

    def __init__(self, lat, long, stress_score, timestamp):
        """
        Initialize a Coordinate object.

        Args:
            lat (float): The latitude coordinate.
            long (float): The longitude coordinate.
            stress_score (float): The stress score associated with the coordinate.
            timestamp (int): The timestamp of the coordinate.

        Returns:
            None
        """
        self.lat = lat
        self.long = long
        self.stress_score = stress_score
        self.timestamp = timestamp

    def get_stress_score(self):
        """
        Get the stress score of the coordinate.

        Returns:
            float: The stress score of the coordinate.
        """
        return self.stress_score

    def set_stress_score(self, stress_score):
        """
        Set the stress score of the coordinate.

        Args:
            stress_score (float): The new stress score.

        Returns:
            None
        """
        self.stress_score = stress_score

    def get_lat(self):
        """
        Get the latitude coordinate.

        Returns:
            float: The latitude coordinate.
        """
        return self.lat

    def get_long(self):
        """
        Get the longitude coordinate.

        Returns:
            float: The longitude coordinate.
        """
        return self.long
    
    def get_timestamp(self):
        """
        Get the timestamp of the coordinate.

        Returns:
            int: The timestamp of the coordinate.
        """
        return self.timestamp
    
    def set_timestamp(self, timestamp):
        """
        Set the timestamp of the coordinate.

        Args:
            timestamp (int): The new timestamp.

        Returns:
            None
        """
        self.timestamp = timestamp

    def __str__(self):
        """
        Return a string representation of the coordinate.

        Returns:
            str: The string representation of the coordinate.
        """
        return 'Coordinate: lat = ' + str(self.lat) + ' long = ' + str(self.long) + ' stress_score = ' + str(self.stress_score)

    def __repr__(self):
        """
        Return a string representation of the coordinate.

        Returns:
            str: The string representation of the coordinate.
        """
        return 'Coordinate: lat = ' + str(self.lat) + ' long = ' + str(self.long) + ' stress_score = ' + str(self.stress_score)


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
            coord_ref = db.reference(f'Map/{coord.get_lat()},{coord.get_long()}')
            if coord_ref.get() is None:
                # If the coordinate does not exist in the database, create a new one
                coord_ref.set({
                    'lat': coord.get_lat(),
                    'long': coord.get_long(),
                    'stress_score': coord.get_stress_score()
                })
            else:
                # If the coordinate exists in the database, sum the existing score with the new one
                coord_ref.update({
                    'stress_score': coord_ref.get()['stress_score'] + coord.get_stress_score()
                })

    def parse_derived_data(self, data):
        """
        Parse derived data and update the map of coordinates.

        Args:
            data (list): A list of dictionaries containing coordinate data.

        Returns:
            None
        """
        for coord in data:
            coord = Coordinate(coord['lat'], coord['long'], coord['stress_score'], coord['timestamp'])
            
            found = False
            for i in range(len(self.map)):
                if self.map[i].get_lat() == coord.get_lat() and self.map[i].get_long() == coord.get_long():
                    if coord.get_stress_score() > 0.6:
                        self.map[i].set_stress_score(self.map[i].get_stress_score() + 1)
                    found = True
                    break

            if not found:
                if coord.get_stress_score() > 0.6:
                    coord.set_stress_score(1)
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