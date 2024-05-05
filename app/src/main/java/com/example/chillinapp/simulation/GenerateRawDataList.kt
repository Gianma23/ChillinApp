package com.example.chillinapp.simulation

import com.example.chillinapp.data.stress.StressRawData
import java.util.Calendar
import java.util.Random


/**
 * Generates a list of [StressRawData] objects for a given time range with a given step.
 * Each [StressRawData] object represents a data point with a timestamp, heart rate sensor reading, and skin temperature sensor reading.
 * The heart rate sensor reading and skin temperature sensor reading are generated using a Gaussian distribution.
 *
 * @param start The start time for the data generation in Calendar format.
 * @param end The end time for the data generation in Calendar format.
 * @param step The time interval between each data point in milliseconds.
 * @param invalidDataProbability The probability of generating invalid data points (e.g., missing data) as a value between 0 and 1.
 * @return A list of StressRawData objects representing the stress data for each minute of the current day up to the current time.
 */
fun generateStressRawDataList(
    start: Calendar,
    end: Calendar,
    step: Long,
    invalidDataProbability: Double = 0.0
): List<StressRawData> {

    // Create a list to store the stress data
    val list = mutableListOf<StressRawData>()
    val random = Random()

    var currentTime = start.timeInMillis
    val endTime = end.timeInMillis

    while (currentTime <= endTime) {

        // Generate a random number between 0 and 1
        val randomNumber = random.nextDouble()

        if (randomNumber > invalidDataProbability) {

            val stressRawData = StressRawData(
                timestamp = currentTime,
                heartRateSensor = (80 + random.nextGaussian() * 10).toFloat(), // Mean 80, st.dev. 10
                skinTemperatureSensor = (35 + random.nextGaussian() * 2.5).toFloat(), // Mean 35, st.dev. 2.5
                edaSensor = (0.5 + random.nextGaussian() * 0.1).toFloat() // Mean 0.5, st.dev. 0.1
            )
            list.add(stressRawData)
        }

        // Increment the current time by the step
        currentTime += step
    }
    return list
}