package com.example.chillinapp.ui.access.utility.validationResult


/**
 * Enum class representing the validation results for the email input in the sign-in screen.
 * It contains the following possible results:
 * VALID: The email input is valid.
 * TOO_LONG: The email input is too long.
 * EMPTY: The email input is empty.
 * DO_NOT_EXIST: The email input does not exist.
 * ALREADY_EXISTS: The email input already exists.
 * INVALID_FORMAT: The email input is in an invalid format.
 * IDLE: The email input is idle (no input or validation yet).
 */
enum class EmailValidationResult {
    VALID,
    TOO_LONG,
    EMPTY,
    DO_NOT_EXIST,
    ALREADY_EXISTS,
    INVALID_FORMAT,
    IDLE
}