class Hour:

    def __init__(self, hour, stress_score):
        self.hour = hour
        self.stress_score = stress_score

    def get_hour(self):
        return self.hour
    
    def get_stress_score(self):
        return self.stress_score
    
    def set_hour(self, hour):
        self.hour = hour

    def set_stress_score(self, stress_score):
        self.stress_score = stress_score