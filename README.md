# Chillin App
The Chillin App is a mobile application that allows users to record their heart rate and gps data, allowing users to 
monitor their stress during their daily operations. The app is designed to be used in conjunction with the Chillin 
Wearable Device, which is a wearable device that measures heart rate and gps. The app allows users to record their data
and view it in real-time.
<hr>

## 1. Data sampling

### 1.1. Sensors
The wearable app uses 3 sensors:
- GSR(galvanic skin response)
- skin temperature
- heart rate. 

Each sensor has a callback that is called when its value change, sampling at a maximum rate of 1Hz. However, in this
manner there is no guarantee to have a constant sampling rate. Moreover, each sensor is independent of the others, 
so the sampling rate of each sensor can be different. If sensors values were to be sent like this, there would be useless 
data that would be sent to the handheld device, incrementing battery consumption. 

In order to have a constant sampling rate, a coroutine run each second to save sensors data. 

### 1.2 GPS Location
The user location is find using FusedLocationProviderClient. A callback is called each 30 seconds, updating two variables 
containing the longitude and latitude. Those variables are then read by the coroutine that saves the data, before sending 
to the handheld device.

## 2. Data Exchange Format
In order to reduce the complexity of the data exchange process, the data exchange format is defined. The data exchange 
format is a simple byte array that contains the following fields:
- **Timestamp**: The timestamp of the recorded data in milliseconds. [Long]
- **EDA Sensor Value**: The EDA sensor value in microsiemens. [Float]
- **Temperature Sensor Value**: The temperature sensor value in Celsius. [Float]
- **Heart Rate Sensor Value**: The heart rate sensor value in beats per minute. [Float]
- **GPS Sensor Value**: The GPS sensor value converted to a Double value (latitude and longitude). [Double]

The data exchange format is defined as follows:
```
| Timestamp (8 bytes) | EDA value (4 bytes) | Temperature value (4 bytes) | Heart Rate value (4 bytes) | Latitude (8 bytes) | Longitude (8 bytes) |
```
for a total of 36 bytes.
In order to perform fewer data exchange, it has been decided to send 30 seconds of data at a time (bulk data). 
This means that the data exchange format will be repeated 30 times, for a total of 1080 bytes.
