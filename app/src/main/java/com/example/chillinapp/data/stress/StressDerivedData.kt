package com.example.chillinapp.data.stress

data class StressDerivedData (
    val timestamp: Long = 0,
    val bInterval: Array<Float> = arrayOf(0.0f, 0.0f),
    val prediction: Double = 0.0,
    val stressLevel: Float = 0.0f
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StressDerivedData

        return bInterval.contentEquals(other.bInterval)
    }

    override fun hashCode(): Int {
        return bInterval.contentHashCode()
    }
}