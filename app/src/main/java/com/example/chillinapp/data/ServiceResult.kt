package com.example.chillinapp.data

data class ServiceResult<T, E> (
    val success: Boolean,
    val data: T? = null,
    val error: E? = null
)