package com.example.chillinapp.data.map

/**
 * Enum class representing different types of errors that can occur when dealing with stress data.
 *
 * This enum defines four types of errors:
 * - NOT_YET_IMPLEMENTED: This physiologicalError type is used when a method has not yet been implemented.
 * - NETWORK_ERROR: This physiologicalError type is used when a network physiologicalError occurs, such as when a network request fails.
 * - COMMUNICATION_PROBLEM: This physiologicalError type is used when there is a problem with communication, such as when data cannot be sent or received correctly.
 * - NO_ACCOUNT: This physiologicalError type is used when an operation requires an account but no account is available.
 */
enum class MapErrorType{
    NOT_YET_IMPLEMENTED,
    NETWORK_ERROR,
    COMMUNICATION_PROBLEM,
    NO_ACCOUNT,
    NO_DATA
}