package com.example.chillinapp.ui.access.utility.validationResult


/**
 * Enum class representing the validation results for the password input in the sign-in screen.
 * It contains the following possible results:
 * VALID: The password input is valid.
 * EMPTY: The password input is empty.
 * TOO_SHORT: The password input is too short.
 * TOO_LONG: The password input is too long.
 * NO_UPPERCASE: The password input does not contain an uppercase letter.
 * NO_LOWERCASE: The password input does not contain a lowercase letter.
 * NO_DIGITS: The password input does not contain a digit.
 * NO_SPECIAL_CHAR: The password input does not contain a special character.
 * IDLE: The password input is idle (no input or validation yet).
 */
enum class PasswordValidationResult {
    VALID,
    EMPTY,
    TOO_SHORT,
    TOO_LONG,
    NO_UPPERCASE,
    NO_LOWERCASE,
    NO_DIGITS,
    NO_SPECIAL_CHAR,
    IDLE
}