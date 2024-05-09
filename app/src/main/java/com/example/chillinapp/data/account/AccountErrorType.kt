package com.example.chillinapp.data.account


/**
 * Enum class representing the types of errors that can occur when working with accounts.
 *
 * This enum defines the different types of errors that can occur when performing operations on accounts in the application.
 * Each enum constant represents a specific type of error.
 *
 * The types of errors include:
 * - EMAIL_IN_USE: The email provided for account creation is already in use.
 * - AUTHENTICATION_FAILED: The authentication process failed, likely due to incorrect credentials.
 * - ACCOUNT_NOT_FOUND: The account being accessed does not exist.
 * - NOT_YET_IMPLEMENTED: The operation being performed has not been implemented yet.
 * - INVALID_EMAIL: The email provided is not valid.
 * - INVALID_PASSWORD: The password provided is not valid.
 * - DATABASE_ERROR: An error occurred while interacting with the database.
 * - AUTHENTICATION_ERROR: An error occurred during the authentication process.
 */
enum class AccountErrorType{
    EMAIL_IN_USE,
    AUTHENTICATION_FAILED,
    ACCOUNT_NOT_FOUND,
    NOT_YET_IMPLEMENTED,
    INVALID_EMAIL,
    INVALID_PASSWORD,
    DATABASE_ERROR,
    AUTHENTICATION_ERROR
}

