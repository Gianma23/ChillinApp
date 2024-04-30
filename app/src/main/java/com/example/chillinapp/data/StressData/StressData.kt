package com.example.chillinapp.data.StressData

import java.util.*

class StressRawData {
    val HR: Double = 0.0
    val GDR: Double = 0.0
    val  TEMP: Double = 0.0
    val timeStamp: Date = Date()


}
class StressDerivedData{
    val timeStamp: Date = Date()
    val BINTERVAL: Array<Float> = arrayOf(0.0f, 0.0f)
    val prediction: Double = 0.0
    val stressLevel: Float = 0.0f

}