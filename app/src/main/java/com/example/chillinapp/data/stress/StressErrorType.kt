package com.example.chillinapp.data.stress

/**
 * Enum class representing different types of errors that can occur when dealing with stress data.
 *
 * This enum defines four types of errors:
 * - NOT_YET_IMPLEMENTED: This error type is used when a method has not yet been implemented.
 * - NETWORK_ERROR: This error type is used when a network error occurs, such as when a network request fails.
 * - COMMUNICATION_PROBLEM: This error type is used when there is a problem with communication, such as when data cannot be sent or received correctly.
 * - NO_ACCOUNT: This error type is used when an operation requires an account but no account is available.
 */
enum class StressErrorType{
    NOT_YET_IMPLEMENTED,
    NETWORK_ERROR,
    COMMUNICATION_PROBLEM,
    NO_ACCOUNT,
    NO_DATA
}