package com.example.chillinapp.synchronization;

/**
 * Singleton class that handles the data collected from the sensor. The data is stored in a byte array
 */
public class SensorDataHandler {

    private static SensorDataHandler instance;
    private static final int MAX_SAMPLING = 30;
    private static final int BYTES_PER_SAMPLE = 20;
    private static final int MAX_DATA_SIZE = MAX_SAMPLING * BYTES_PER_SAMPLE; // 24 bytes per sample
    private byte[] bulkData;
    private int samplingCount = 0;

    private SensorDataHandler() {
        bulkData = new byte[MAX_DATA_SIZE];
    }

    public static SensorDataHandler getInstance() {
        if (instance == null) {
            instance = new SensorDataHandler();
        }
        return instance;
    }

    public boolean pushData(byte[] data) {
        // If samples have reached the maximum, create a new bulk data and start over
        if (samplingCount == MAX_SAMPLING){
            bulkData = new byte[MAX_DATA_SIZE];
            samplingCount = 0;
        }

        if (samplingCount < MAX_SAMPLING) {
            System.arraycopy(data, 0, bulkData, samplingCount * BYTES_PER_SAMPLE, BYTES_PER_SAMPLE);
            samplingCount++;
        }

        return samplingCount == MAX_SAMPLING;
    }

    public byte[] getBulkData() {
        return bulkData;
    }
}
