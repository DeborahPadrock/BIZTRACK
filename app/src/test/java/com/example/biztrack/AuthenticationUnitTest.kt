package com.example.biztrack

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthenticationUnitTest {

    @Test
    fun createAccount_validatesEmailRules() {
        fun validateEmail(email: String): String? {
            if (email.isBlank()) return "Please enter email"
            if (!isValidEmail(email)) return "Enter a correct email"
            return null
        }

        // Empty email
        assertEquals("Please enter email", validateEmail(""))
        assertEquals("Please enter email", validateEmail("   "))

        // Missing domain structure / missing extensions / malformed domain
        assertEquals("Enter a correct email", validateEmail("mwaithungula@gmail"))
        assertEquals("Enter a correct email", validateEmail("test@com"))
        assertEquals("Enter a correct email", validateEmail("thungulamwai@gm"))
        assertEquals("Enter a correct email", validateEmail("invalid-email"))
        assertEquals("Enter a correct email", validateEmail("mwaithungula@"))

        // Valid full email address
        assertEquals(null, validateEmail("mwaithungula@gmail.com"))
        assertEquals(null, validateEmail("thungulamwai@gm.com"))
        assertEquals(null, validateEmail("user@example.org"))
    }

    @Test
    fun createAccount_validatesEmptyFields() {
        fun isFormValid(email: String, username: String, pass: String, confirmPass: String): Boolean {
            return email.isNotBlank() && username.isNotBlank() && pass.isNotBlank() && confirmPass.isNotBlank()
        }

        assertFalse(isFormValid("", "username", "pass123", "pass123"))
        assertFalse(isFormValid("test@example.com", "", "pass123", "pass123"))
        assertFalse(isFormValid("test@example.com", "username", "", "pass123"))
        assertFalse(isFormValid("test@example.com", "username", "pass123", ""))
        assertTrue(isFormValid("test@example.com", "username", "pass123", "pass123"))
    }

    @Test
    fun createAccount_validatesPasswordMismatch() {
        fun validatePasswords(pass: String, confirmPass: String): String? {
            return if (pass != confirmPass) "Passwords do not match" else null
        }

        assertEquals("Passwords do not match", validatePasswords("password123", "password321"))
        assertEquals(null, validatePasswords("password123", "password123"))
    }

    @Test
    fun login_validatesEmptyFields() {
        fun isLoginValid(username: String, pass: String): Boolean {
            return username.isNotBlank() && pass.isNotBlank()
        }

        assertFalse(isLoginValid("", "password123"))
        assertFalse(isLoginValid("user123", ""))
        assertTrue(isLoginValid("user123", "password123"))
    }

    @Test
    fun authEmail_formatsUsernameToAppEmail() {
        fun formatAuthEmail(username: String): String {
            val trimmed = username.trim().lowercase()
            return if (trimmed.endsWith("@biztrack.app")) trimmed else "$trimmed@biztrack.app"
        }

        assertEquals("john@biztrack.app", formatAuthEmail("John"))
        assertEquals("mary@biztrack.app", formatAuthEmail("MARY@BIZTRACK.APP"))
        assertEquals("user123@biztrack.app", formatAuthEmail("  user123  "))
    }
}
