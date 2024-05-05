package com.example.chillinapp.data.stress

import kotlin.random.Random

data class StressRawData(
    val timestamp: Long = 0,
    val heartRateSensor: Float = 0.0F,
    val skinTemperatureSensor: Float = 0.0F,
    val edaSensor: Float= 0.0F
)
fun generateDummyRawDataList(): List<StressRawData> {
    val dummyRawDataList = mutableListOf<StressRawData>()

    for (i in 1..30) {
        val timestamp = System.currentTimeMillis() - (i * 60000L) // Creazione di timestamp decrescenti (ogni minuto)
        val heartRateSensor = Random.nextFloat() * 100 // Generazione di heartRateSensor casuale
        val skinTemperatureSensor = Random.nextFloat() * 10 + 30 // Generazione di skinTemperatureSensor casuale tra 30 e 40
        val edaSensor = Random.nextFloat() * 2 // Generazione di edaSensor casuale tra 0 e 2

        dummyRawDataList.add(StressRawData(timestamp, heartRateSensor, skinTemperatureSensor, edaSensor))
    }

    return dummyRawDataList
}

