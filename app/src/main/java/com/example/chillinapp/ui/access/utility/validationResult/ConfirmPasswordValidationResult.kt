package com.example.chillinapp.ui.access.utility.validationResult


/**
 * Enum class representing the validation results for the confirm password input in the sign-in screen.
 * It contains the following possible results:
 * VALID: The confirm password input is valid.
 * EMPTY: The confirm password input is empty.
 * NOT_MATCH: The confirm password input does not match the password input.
 * IDLE: The confirm password input is idle (no input or validation yet).
 */
enum class ConfirmPasswordValidationResult {
    VALID,
    EMPTY,
    NOT_MATCH,
    IDLE
}