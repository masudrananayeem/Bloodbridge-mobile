package com.bloodbridge.app.data

import org.junit.Assert.assertEquals
import org.junit.Test

class FriendlyMessageTest {

    @Test
    fun friendlyMessage_invalidCredentialsPatterns() {
        val ex1 = Exception("The password is invalid or the user does not have a password.")
        assertEquals("Email or password is incorrect.", ex1.friendlyMessage())

        val ex2 = Exception("INVALID_LOGIN_CREDENTIALS")
        assertEquals("Email or password is incorrect.", ex2.friendlyMessage())

        val ex3 = Exception("The supplied auth credential is malformed")
        assertEquals("Email or password is incorrect.", ex3.friendlyMessage())
    }

    @Test
    fun friendlyMessage_noUserRecord() {
        val ex = Exception("There is no user record corresponding to this identifier.")
        assertEquals("No account found with this email.", ex.friendlyMessage())
    }

    @Test
    fun friendlyMessage_emailAlreadyInUse() {
        val ex = Exception("The email address is already in use by another account.")
        assertEquals("This email is already registered.", ex.friendlyMessage())
    }

    @Test
    fun friendlyMessage_cancelledSignIn() {
        val ex = Exception("User cancel the sign-in prompt.")
        assertEquals("Sign-in was cancelled.", ex.friendlyMessage())
    }

    @Test
    fun friendlyMessage_networkErrors() {
        val ex1 = Exception("A network error occurred.")
        assertEquals("No internet connection.", ex1.friendlyMessage())

        val ex2 = Exception("java.net.UnknownHostException: Unable to resolve host 'example.com'")
        assertEquals("No internet connection.", ex2.friendlyMessage())
    }

    @Test
    fun friendlyMessage_blankOrGenericError() {
        val ex1 = Exception("")
        assertEquals("Something went wrong. Please try again.", ex1.friendlyMessage())

        val ex2 = Exception("   ")
        assertEquals("Something went wrong. Please try again.", ex2.friendlyMessage())
    }

    @Test
    fun friendlyMessage_unrecognizedError_passesMessageThrough() {
        val customMsg = "Database connection timed out in region us-east-1"
        val ex = Exception(customMsg)
        assertEquals(customMsg, ex.friendlyMessage())
    }
}
