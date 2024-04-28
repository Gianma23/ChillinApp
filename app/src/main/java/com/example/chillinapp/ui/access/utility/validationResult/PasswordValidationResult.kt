package com.example.chillinapp.ui.access.utility.validationResult


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