package com.example.multivendor.data.auth

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

class AuthValidatorTest {

    @Test
    fun registration_validInput_passes() {
        assertNull(
            AuthValidator.validateRegistration(
                name = "Priya Sharma",
                email = "priya@example.com",
                phone = "+91 98765 43210",
                password = "secret123",
                confirmPassword = "secret123"
            )
        )
    }

    @Test
    fun registration_blankName_fails() {
        val error = AuthValidator.validateRegistration(
            name = "  ",
            email = "priya@example.com",
            phone = "9876543210",
            password = "secret123",
            confirmPassword = "secret123"
        )
        assertTrue(error != null)
    }

    @Test
    fun registration_badEmail_fails() {
        assertTrue(
            AuthValidator.validateRegistration(
                name = "Priya",
                email = "not-an-email",
                phone = "9876543210",
                password = "secret123",
                confirmPassword = "secret123"
            ) != null
        )
    }

    @Test
    fun registration_shortPhone_fails() {
        assertTrue(
            AuthValidator.validateRegistration(
                name = "Priya",
                email = "priya@example.com",
                phone = "12345",
                password = "secret123",
                confirmPassword = "secret123"
            ) != null
        )
    }

    @Test
    fun registration_shortPassword_fails() {
        assertTrue(
            AuthValidator.validateRegistration(
                name = "Priya",
                email = "priya@example.com",
                phone = "9876543210",
                password = "123",
                confirmPassword = "123"
            ) != null
        )
    }

    @Test
    fun registration_mismatchedConfirm_fails() {
        assertEquals(
            "Passwords do not match.",
            AuthValidator.validateRegistration(
                name = "Priya",
                email = "priya@example.com",
                phone = "9876543210",
                password = "secret123",
                confirmPassword = "different"
            )
        )
    }

    @Test
    fun login_validInput_passes() {
        assertNull(AuthValidator.validateLogin("user@example.com", "secret123"))
    }

    @Test
    fun login_badEmail_fails() {
        assertTrue(AuthValidator.validateLogin("bad-email", "secret123") != null)
    }

    @Test
    fun normalizeEmail_trimsAndLowercases() {
        assertEquals("user@example.com", AuthValidator.normalizeEmail("  User@Example.COM "))
    }
}
