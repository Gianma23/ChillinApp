# define a main that runs the job and tries to call function from the Map class
from Map import Map
from datetime import datetime
import time

def main():
    """
    This is the main function that runs the job and calls functions from the Map class.

    It initializes a list of derived data, creates an instance of the Map class, and
    calls the `parse_derived_data()` method of the Map class to parse the derived data.

    Args:
        None

    Returns:
        None
    """
    derived_data = [
        {"lat": 1, "long": 1, "stress_score": 0.7},
        {"lat": 2, "long": 2, "stress_score": 0.8},
        {"lat": 1, "long": 1, "stress_score": 0.9},
        {"lat": 3, "long": 3, "stress_score": 0.6},
        {"lat": 2, "long": 2, "stress_score": 0.7},
        {"lat": 1, "long": 1, "stress_score": 0.8},
        {"lat": 3, "long": 3, "stress_score": 0.3},
        {"lat": 1, "long": 1, "stress_score": 0.7},
        {"lat": 4, "long": 1, "stress_score": 0.8},
        {"lat": 1, "long": 1, "stress_score": 0.9},
        {"lat": 3, "long": 3, "stress_score": 0.6},
        {"lat": 2, "long": 2, "stress_score": 0.7},
        {"lat": 1, "long": 1, "stress_score": 0.8},
        {"lat": 4, "long": 55, "stress_score": 0.3},
        {"lat": 1, "long": 1, "stress_score": 0.7},
        {"lat": 2, "long": 3, "stress_score": 0.8},
        {"lat": 1, "long": 1, "stress_score": 0.9},
        {"lat": 5, "long": 3, "stress_score": 0.6},
        {"lat": 2, "long": 2, "stress_score": 0.7},
        {"lat": 1, "long": 1, "stress_score": 0.8},
        {"lat": 3, "long": 3, "stress_score": 0.3},
    ]

    map = Map()
    map.parse_derived_data(derived_data)


if __name__ == '__main__':
    main()

