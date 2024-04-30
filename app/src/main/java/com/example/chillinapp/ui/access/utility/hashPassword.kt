package com.example.chillinapp.ui.access.utility

import java.security.MessageDigest


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
    val bytes = password.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}