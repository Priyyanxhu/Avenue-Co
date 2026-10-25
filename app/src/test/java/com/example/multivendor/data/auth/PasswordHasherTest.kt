package com.example.multivendor.data.auth

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun hash_isDeterministic_forSameSaltAndPassword() {
        val salt = PasswordHasher.generateSalt()
        assertEquals(PasswordHasher.hash("secret123", salt), PasswordHasher.hash("secret123", salt))
    }

    @Test
    fun hash_differs_forDifferentSalts() {
        val a = PasswordHasher.hash("secret123", PasswordHasher.generateSalt())
        val b = PasswordHasher.hash("secret123", PasswordHasher.generateSalt())
        assertFalse(a == b)
    }

    @Test
    fun verify_acceptsCorrectPassword_rejectsWrongOne() {
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hash("myPassword", salt)
        assertTrue(PasswordHasher.verify("myPassword", salt, hash))
        assertFalse(PasswordHasher.verify("wrongPassword", salt, hash))
    }

    @Test
    fun hash_isHexSha256_notPlaintext() {
        val hash = PasswordHasher.hash("secret123", "somesalt")
        assertEquals(64, hash.length)
        assertTrue(hash.matches(Regex("[0-9a-f]+")))
        assertFalse(hash.contains("secret123"))
    }

    @Test
    fun generateSalt_producesUniqueValues() {
        assertFalse(PasswordHasher.generateSalt() == PasswordHasher.generateSalt())
    }
}
