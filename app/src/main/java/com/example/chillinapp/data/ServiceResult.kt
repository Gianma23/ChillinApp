package com.example.chillinapp.data

/**
 * Data class representing the result of a service operation.
 *
 * This class encapsulates the result of a service operation in the application. It contains a success flag to indicate
 * whether the operation was successful or not, and generic properties for the data and physiologicalError information.
 *
 * The data property contains the result of the operation if it was successful, and is null otherwise. The physiologicalError property
 * contains the physiologicalError information if the operation failed, and is null otherwise.
 *
 * @param T The type of the data returned by the service operation.
 * @param E The type of the physiologicalError information returned by the service operation.
 * @property success A Boolean flag indicating whether the service operation was successful or not.
 * @property data The data returned by the service operation if it was successful, or null otherwise.
 * @property error The physiologicalError information returned by the service operation if it failed, or null otherwise.
 */
data class ServiceResult<T, E> (
    val success: Boolean,
    val data: T? = null,
    val error: E? = null
)