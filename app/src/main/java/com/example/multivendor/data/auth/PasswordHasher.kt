package com.example.multivendor.data.auth

import java.security.MessageDigest
import java.security.SecureRandom

/** SHA-256 password hashing with a per-user random salt. Pure JVM — unit-testable. */
object PasswordHasher {
    private val random = SecureRandom()

    fun generateSalt(): String {
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return bytes.toHex()
    }

    fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val combined = "$salt:$password".toByteArray(Charsets.UTF_8)
        return digest.digest(combined).toHex()
    }

    fun verify(password: String, salt: String, expectedHash: String): Boolean =
        hash(password, salt) == expectedHash

    private fun ByteArray.toHex(): String =
        joinToString("") { "%02x".format(it) }
}
