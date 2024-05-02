package com.example.chillinapp.data.stress

data class StressRawData(
    val timestamp: Long = 0,
    val heartrateSensor: Double = 0.0,
    val skinTemperatureSensor: Double = 0.0
)

data class StressDerivedData (
    val timestamp: Long = 0,
    val BINTERVAL: Array<Float> = arrayOf(0.0f, 0.0f),
    val prediction: Double = 0.0,
    val stressLevel: Float = 0.0f
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StressDerivedData

        if (timestamp != other.timestamp) return false
        if (!BINTERVAL.contentEquals(other.BINTERVAL)) return false
        if (prediction != other.prediction) return false
        if (stressLevel != other.stressLevel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + BINTERVAL.contentHashCode()
        result = 31 * result + prediction.hashCode()
        result = 31 * result + stressLevel.hashCode()
        return result
    }
}