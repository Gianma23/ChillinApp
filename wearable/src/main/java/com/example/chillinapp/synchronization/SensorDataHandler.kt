package com.example.chillinapp.synchronization

/**
 * Singleton class that handles the data collected from the sensor. The data is stored in a byte array
 */
object SensorDataHandler {
    private var bulkData: ByteArray
    private var samplingCount = 0
    private const val MAX_SAMPLING = 30
    private const val BYTES_PER_SAMPLE = 36
    private const val MAX_DATA_SIZE = MAX_SAMPLING * BYTES_PER_SAMPLE

    init {
        bulkData = ByteArray(MAX_DATA_SIZE)
    }

    fun pushData(data: ByteArray): Boolean {

        // if length of data is not equal to BYTES_PER_SAMPLE, return false
        if (data.size != BYTES_PER_SAMPLE) {
            return false
        }

        // If samples have reached the maximum, create a new bulk data and start over
        if (samplingCount == MAX_SAMPLING) {
            bulkData = ByteArray(MAX_DATA_SIZE)
            samplingCount = 0
        }
        if (samplingCount < MAX_SAMPLING) {
            System.arraycopy(data, 0, bulkData, samplingCount * BYTES_PER_SAMPLE, BYTES_PER_SAMPLE)
            samplingCount++
        }
        return samplingCount == MAX_SAMPLING
    }

    fun getData(): ByteArray {
        return bulkData
    }
}

