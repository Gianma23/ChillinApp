class Coordinate: 
    """
    This class is used to store the coordinates and their stress score.
    """

    def __init__(self, lat, long):
        """
        Initialize a Coordinate object.

        Args:
            lat (float): The latitude coordinate.
            long (float): The longitude coordinate.

        Returns:
            None
        """
        self.lat = lat
        self.long = long
        self.days = []

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

    def get_days(self):
        """
        Get the days of the coordinate.

        Returns:
            list: The days of the coordinate.
        """
        return self.days
    
    def set_days(self, days):
        """
        Set the days of the coordinate.

        Args:
            days (list): The new days.

        Returns:
            None
        """
        self.days = days

    def add_day(self, day):
        """
        Add a day to the coordinate.

        Args:
            day (Day): The new day.

        Returns:
            None
        """
        self.days.append(day)

    def get_day(self, day):
        """
        Get the day of the coordinate.

        Args:
            day (int): The day to get.

        Returns:
            Day: The day of the coordinate.
        """
        for d in self.days:
            if d.get_day() == day:
                return d
        return None
    
    def is_day(self, day):
        """
        Check if the day exists in the coordinate.

        Args:
            day (int): The day to check.

        Returns:
            bool: True if the day exists, False otherwise.
        """
        for d in self.days:
            if d.get_day() == day:
                return True
        return False