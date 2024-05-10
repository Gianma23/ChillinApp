class Day:

    def __init__(self, day, hours):
        self.day = day
        self.hours = hours

    def get_day(self):
        return self.day
    
    def get_hours(self):
        return self.hours

    def set_day(self, day):
        self.day = day

    def set_hours(self, hours):
        self.hours = hours

    def add_hour(self, hour):
        self.hours.append(hour)

    def get_hour(self, hour):
        for record in self.hours:
            if record.get_hour() == hour:
                return record
        return None
    
    def is_hour(self, hour):
        for record in self.hours:
            if record.get_hour() == hour:
                return True
        return False
