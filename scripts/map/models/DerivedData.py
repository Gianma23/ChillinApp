class DerivedData:
    
        def __init__(self, lat, long, stress_score, timestamp):
            self.lat = lat
            self.long = long
            self.stress_score = stress_score
            self.timestamp = timestamp
    
        def get_lat(self):
            return self.lat
        
        def get_long(self):
            return self.long
        
        def get_stress_score(self):
            return self.stress_score
        
        def get_timestamp(self):
            return self.timestamp
    
        def set_lat(self, lat):
            self.lat = lat
    
        def set_long(self, long):
            self.long = long
    
        def set_stress_score(self, stress_score):
            self.stress_score = stress_score
    
        def set_timestamp(self, timestamp):
            self.timestamp = timestamp