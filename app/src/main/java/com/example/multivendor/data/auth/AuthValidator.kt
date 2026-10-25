package com.example.multivendor.data.auth

/** Pure input validation for login / registration. Returns an error message or null when valid. */
object AuthValidator {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    const val MIN_PASSWORD_LENGTH = 6

    fun validateName(name: String): String? = when {
        name.isBlank() -> "Please enter your name."
        name.trim().length < 2 -> "Name must be at least 2 characters."
        else -> null
    }

    fun validateEmail(email: String): String? = when {
        email.isBlank() -> "Please enter your email."
        !EMAIL_REGEX.matches(email.trim()) -> "Please enter a valid email address."
        else -> null
    }

    fun validatePhone(phone: String): String? {
        val digits = phone.filter { it.isDigit() }
        return when {
            phone.isBlank() -> "Please enter your phone number."
            digits.length < 10 -> "Phone number must have at least 10 digits."
            else -> null
        }
    }

    fun validatePassword(password: String): String? = when {
        password.isEmpty() -> "Please enter a password."
        password.length < MIN_PASSWORD_LENGTH -> "Password must be at least $MIN_PASSWORD_LENGTH characters."
        else -> null
    }

    fun validateConfirmPassword(password: String, confirm: String): String? = when {
        confirm.isEmpty() -> "Please confirm your password."
        password != confirm -> "Passwords do not match."
        else -> null
    }

    /** Returns the first registration error, or null when everything is valid. */
    fun validateRegistration(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): String? =
        validateName(name)
            ?: validateEmail(email)
            ?: validatePhone(phone)
            ?: validatePassword(password)
            ?: validateConfirmPassword(password, confirmPassword)

    /** Returns the first login error, or null when everything is valid. */
    fun validateLogin(email: String, password: String): String? =
        validateEmail(email) ?: validatePassword(password)

    fun normalizeEmail(email: String): String = email.trim().lowercase()
}
