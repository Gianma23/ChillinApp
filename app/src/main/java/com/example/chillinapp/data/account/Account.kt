package com.example.chillinapp.data.account


/**
 * Data class representing an Account in the application.
 *
 * This class encapsulates the data related to an account in the application. It contains properties for the name,
 * email, and password of the account.
 *
 * The name, email, and password properties are all nullable, meaning that they can be null. This is because they are
 * initialized with null by default, and can be set to a non-null value later.
 *
 * @property name The name of the account. It is nullable and is initialized with null by default.
 * @property email The email of the account. It is nullable and is initialized with null by default.
 * @property password The password of the account. It is nullable and is initialized with null by default.
 */
data class Account (
    val name: String? = null,
    val email: String? = null,
    val password: String? = null
)