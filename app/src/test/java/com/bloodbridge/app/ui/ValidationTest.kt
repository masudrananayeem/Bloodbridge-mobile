package com.bloodbridge.app.ui

import com.bloodbridge.app.data.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationTest {

    private val bangladeshiPhoneRegex = Regex("^01[3-9]\\d{8}$")
    private val dobRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")

    @Test
    fun bangladeshiPhoneRegex_acceptsValidOperators() {
        val validNumbers = listOf(
            "01312345678", // Grameenphone
            "01412345678", // Banglalink
            "01512345678", // Teletalk
            "01612345678", // Airtel
            "01712345678", // Grameenphone
            "01812345678", // Robi
            "01912345678", // Banglalink
        )
        for (num in validNumbers) {
            assertTrue("Expected '$num' to be valid Bangladeshi phone", bangladeshiPhoneRegex.matches(num))
        }
    }

    @Test
    fun bangladeshiPhoneRegex_rejectsInvalidFormats() {
        val invalidNumbers = listOf(
            "01212345678",   // 012 operator is not valid
            "0171234567",    // 10 digits (too short)
            "017123456789",  // 12 digits (too long)
            "+8801712345678",// International prefix not handled by raw regex
            "0171234567a",   // Contains letters
            " 01712345678 ", // Un-trimmed
            "02912345678",   // Landline / non-mobile
            "",              // Blank
        )
        for (num in invalidNumbers) {
            assertFalse("Expected '$num' to be rejected", bangladeshiPhoneRegex.matches(num))
        }
    }

    @Test
    fun dobRegex_acceptsIsoDateOnly() {
        assertTrue(dobRegex.matches("1995-08-25"))
        assertTrue(dobRegex.matches("2000-01-01"))

        assertFalse(dobRegex.matches("25-08-1995"))
        assertFalse(dobRegex.matches("1995/08/25"))
        assertFalse(dobRegex.matches("1995-8-25"))
        assertFalse(dobRegex.matches("not-a-date"))
        assertFalse(dobRegex.matches(""))
    }

    @Test
    fun constants_bloodGroups_containsAllStandardGroups() {
        assertEquals(8, Constants.bloodGroups.size)
        val expected = setOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        assertEquals(expected, Constants.bloodGroups.toSet())
    }

    @Test
    fun constants_districts_containsAll64DistrictsOfBangladesh() {
        assertEquals("Bangladesh has exactly 64 administrative districts", 64, Constants.districts.size)
        // Verify key districts are present
        val keyDistricts = listOf("Dhaka", "Chattogram", "Rajshahi", "Khulna", "Sylhet", "Barishal", "Rangpur", "Mymensingh")
        for (d in keyDistricts) {
            assertTrue("Expected district '$d' in Constants.districts", Constants.districts.contains(d))
        }
        // Verify all names are unique and sorted
        assertEquals(Constants.districts.size, Constants.districts.toSet().size)
    }

    @Test
    fun constants_urgencies_containsExpectedLevels() {
        assertEquals(listOf("Low", "Medium", "High"), Constants.urgencies)
    }

    @Test
    fun constants_requestStatuses_containsAllWorkflowStatuses() {
        val expected = setOf("pending", "accepted", "completed", "cancelled", "rejected")
        assertEquals(expected, Constants.requestStatuses.toSet())
    }

    @Test
    fun constants_orgTypes_containsRecognizedTypes() {
        val expected = setOf("Voluntary Group", "NGO", "Blood Bank", "Hospital")
        assertEquals(expected, Constants.orgTypes.toSet())
    }
}
