package com.example.multivendor.data.auth

/** Logged-in app user (never carries a password). */
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String,
    val createdAt: Long = System.currentTimeMillis()
)
