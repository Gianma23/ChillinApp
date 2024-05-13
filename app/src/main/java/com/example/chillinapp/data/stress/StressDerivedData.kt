package com.example.chillinapp.data.stress

/**
 * Data class representing derived stress data.
 *
 * This class holds data derived from raw stress data, including a timestamp, an array of two floats representing the B interval, a prediction value, and a stress level.
 * The equals and hashCode methods have been overridden to provide custom equality checks and hash code generation based on the B interval array.
 *
 * @property timestamp The timestamp of the data, represented as a long. Defaults to 0.
 * @property bInterval An array of two floats representing the B interval. Defaults to an array of two 0.0f values.
 * @property prediction A double representing a prediction value. Defaults to 0.0.
 * @property stressLevel A float representing the stress level. Defaults to 0.0f.
 */
data class StressDerivedData(
    val timestamp: Long = 0,
    val bInterval: ArrayList<Float>,
    val stressLevel: Float = 0.0f
) {

}

/**
     * Checks if this StressDerivedData is equal to another object.
     *
     * @param other The object to compare to this StressDerivedData.
     * @return A boolean indicating whether this StressDerivedData is equal to the other object.
     */
