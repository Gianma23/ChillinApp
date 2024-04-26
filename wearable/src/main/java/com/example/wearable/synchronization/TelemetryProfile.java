package com.example.wearable.synchronization;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * This class represents the GATT profile of the Telemetry service.
 */
public class TelemetryProfile {
    private static final String TAG = TelemetryProfile.class.getSimpleName();

    public static UUID TELEMETRY_SERVICE = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");
    // Mandatory Sensor Data characteristic
    public static UUID SENSOR_DATA = UUID.fromString("25A590EF-074B-4408-9489-09186C01B7B6");
    // Mandatory Client Characteristic Config Descriptor
    public static UUID CLIENT_CONFIG = UUID.fromString("4DDE5B89-D2E7-4A19-8D9E-ECF221ED4802");

    /**
     * Create the Telemetry service
     * @return a BluetoothGattService
     */
    public static BluetoothGattService createTelemetryService(){
        BluetoothGattService service = new BluetoothGattService(TELEMETRY_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic sensorData = new BluetoothGattCharacteristic(SENSOR_DATA,
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);

        BluetoothGattDescriptor configDescriptor = new BluetoothGattDescriptor(CLIENT_CONFIG,
                //Read/write descriptor
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        sensorData.addDescriptor(configDescriptor);

        service.addCharacteristic(sensorData);

        return service;
    }

    public static byte[] getSensorData() {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES + Double.BYTES + Double.BYTES);

        // Add timestamp, EDA sensor value and ST sensor value using private helper methods
        buffer.put(getTimestamp());
        buffer.put(getEdaSensor());
        buffer.put(getStSensor());

        return buffer.array();
    }

    /**
     * Get the current timestamp
     * @return a byte array representing the current timestamp
     */
    private static byte[] getTimestamp(){
        long timestamp = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(timestamp);
        return buffer.array();
    }

    /**
     * Get the EDA sensor value
     * @return a byte array representing the EDA sensor value
     */
    private static byte[] getEdaSensor() {
        // TODO: implement logic here to communicate with @Sensor module
        double sensorValue = 5.12131;
        ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
        buffer.putDouble(sensorValue);
        return buffer.array();
    }

    /**
     * Get the ST sensor value
     * @return a byte array representing the ST sensor value
     */
    private static byte[] getStSensor(){
        // TODO: implement logic here to communicate with @Sensor module
        double sensorValue = 36.321;
        ByteBuffer buffer = ByteBuffer.allocate(Double.BYTES);
        buffer.putDouble(sensorValue);
        return buffer.array();
    }
}
