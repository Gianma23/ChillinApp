package com.example.chillinapp.simulation

import android.util.Log
import com.example.chillinapp.data.ServiceResult
import com.example.chillinapp.data.stress.StressDerivedData
import com.example.chillinapp.data.stress.StressErrorType
import java.util.Random


fun simulateDerivedDataService(
    start: Long,
    end: Long,
    invalidDataProbability: Double = 0.4
): ServiceResult<List<StressDerivedData>, StressErrorType> {
    val response: ServiceResult<List<StressDerivedData>, StressErrorType> =
        ServiceResult(
            success = true,
            data = generateDerivedDataList(start, end, invalidDataProbability),
            error = null
        )
    Log.d("SimulateDerivedDataService", "Simulated derived data service response: $response")
    return response
}

private fun generateDerivedDataList(
    start: Long,
    end: Long,
    invalidDataProbability: Double = 0.0
): List<StressDerivedData> {

    // Create a list to store the stress data
    val list = mutableListOf<StressDerivedData>()
    val random = Random()

    var currentTime = start
    while (currentTime <= end) {

        // Generate a random number between 0 and 1
        val randomNumber = random.nextDouble()

        if (randomNumber > invalidDataProbability) {

            val stressDerivedData = StressDerivedData(
                timestamp = currentTime,
                stressLevel = (0.4 + random.nextGaussian() * 0.4).toFloat().coerceIn(0.0f,1.0f), // Mean 0.4, st.dev. 0.4
                bInterval = arrayListOf(
                    (0.2 + random.nextGaussian() * 0.2).toFloat().coerceIn(0.0f,1.0f), // Mean 0.2, st.dev. 0.2
                    (0.6 + random.nextGaussian() * 0.4).toFloat().coerceIn(0.0f,1.0f) // Mean 0.5, st.dev. 0.1
                )
            )
            list.add(stressDerivedData)
        }

        // Increment the current time by 2 minute
        currentTime += 1000 * 60 * 2
    }
    return list
}