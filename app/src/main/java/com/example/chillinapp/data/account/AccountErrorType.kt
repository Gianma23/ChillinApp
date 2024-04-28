package com.example.chillinapp.data.account

import com.example.chillinapp.Message

enum class AccountErrorType(override val message: String) : Message {
    EMAIL_IN_USE("Email is already in use"),
    AUTHENTICATION_FAILED("Authentication failed"),
    ACCOUNT_NOT_FOUND("Account not found"),
    NOT_YET_IMPLEMENTED("Service not yet implemented"),
    INVALID_EMAIL("Invalid email"),
    INVALID_PASSWORD("Invalid password"),
}