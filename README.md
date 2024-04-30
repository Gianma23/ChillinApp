# Chillin App
The Chillin App is a mobile application that allows users to record their Electrodermal Activity (EDA) and skin temperature data, allowing users to monitor their stress during their daily operations. The app is designed to be used in conjunction with the Chillin Wearable Device, which is a wearable device that measures EDA and skin temperature. The app allows users to record their data and view it in real-time.
<hr>

## 1. Data Exchange Format
In order to reduce the complexity of the data exchange process, the data exchange format is defined. The data exchange format is a simple byte array that contains the following fields:
- **Timestamp**: The timestamp of the recorded data in milliseconds.
- **EDA Sensor Value**: The EDA sensor value in microsiemens.
- **Skin Temperature Sensor Value**: The skin temperature sensor value in degrees Celsius.

The data exchange format is defined as follows:
```
| Timestamp (8 bytes) | EDA Sensor Value (8 bytes) | Skin Temperature Sensor Value (8 bytes) |
```
for a total of 24 bytes.
In order to perform fewer data exchange, it has been decided to send 30 seconds of data at a time (bulk data). This means that the data exchange format will be repeated 30 times, for a total of 720 bytes.
