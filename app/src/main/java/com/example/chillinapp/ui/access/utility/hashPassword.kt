package com.example.chillinapp.ui.access.utility

import at.favre.lib.crypto.bcrypt.BCrypt


/**
 * Function to hash a password using BCrypt algorithm.
 *
 * BCrypt algorithm automatically handles the creation of salt and the combination of salt and password.
 * It's designed to be slow and computationally intensive, to hinder attacks using rainbow tables or brute force.
 *
 * @param password The password string to be hashed.
 * @return The hashed password as a string.
 */
fun hashPassword(password: String): String {
    return BCrypt.withDefaults().hashToString(12, password.toCharArray())
}