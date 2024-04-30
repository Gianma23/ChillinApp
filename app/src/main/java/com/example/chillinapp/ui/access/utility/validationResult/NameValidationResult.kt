package com.example.chillinapp.ui.access.utility.validationResult


/**
 * Enum class representing the validation results for the name input in the sign-in screen.
 * It contains the following possible results:
 * VALID: The name input is valid.
 * EMPTY: The name input is empty.
 * TOO_LONG: The name input is too long.
 * INVALID_CHARACTERS: The name input contains invalid characters.
 * IDLE: The name input is idle (no input or validation yet).
 */
enum class NameValidationResult {
    VALID,
    EMPTY,
    TOO_LONG,
    INVALID_CHARACTERS,
    IDLE
}