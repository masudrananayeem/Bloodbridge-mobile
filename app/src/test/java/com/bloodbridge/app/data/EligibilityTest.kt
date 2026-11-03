package com.bloodbridge.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class EligibilityTest {

    @Test
    fun donationCooldownConstant_isExactly120Days() {
        assertEquals("Business rule requires 120 days cooldown", 120, DONATION_COOLDOWN_DAYS)
    }

    @Test
    fun daysUntilEligible_nullOrBlank_returnsZero() {
        assertEquals(0, daysUntilEligible(null))
        assertEquals(0, daysUntilEligible(""))
        assertEquals(0, daysUntilEligible("   "))
    }

    @Test
    fun daysUntilEligible_invalidDateString_returnsZeroSafely() {
        assertEquals(0, daysUntilEligible("not-an-iso-date"))
        assertEquals(0, daysUntilEligible("2024/01/01"))
        assertEquals(0, daysUntilEligible("invalid"))
    }

    @Test
    fun daysUntilEligible_donationPast120Days_returnsZero() {
        val pastDate = Instant.now().minus(125, ChronoUnit.DAYS).toString()
        assertEquals(0, daysUntilEligible(pastDate))
    }

    @Test
    fun daysUntilEligible_donationToday_returns120Days() {
        val today = Instant.now().toString()
        val days = daysUntilEligible(today)
        assertTrue("Days left should be 120 immediately after donation, was $days", days in 119..120)
    }

    @Test
    fun daysUntilEligible_donation60DaysAgo_returns60Days() {
        val sixtyDaysAgo = Instant.now().minus(60, ChronoUnit.DAYS).toString()
        val days = daysUntilEligible(sixtyDaysAgo)
        assertTrue("Days left should be approximately 60, was $days", days in 59..61)
    }

    @Test
    fun nextEligibleLabel_nullOrBlank_returnsNull() {
        assertNull(nextEligibleLabel(null))
        assertNull(nextEligibleLabel(""))
        assertNull(nextEligibleLabel("   "))
    }

    @Test
    fun nextEligibleLabel_invalidFormat_returnsNull() {
        assertNull(nextEligibleLabel("invalid-date-format"))
    }

    @Test
    fun nextEligibleLabel_validIsoDate_returnsExactDatePlus120Days() {
        // 2024 is a leap year: Jan (30 remaining) + Feb (29) + Mar (31) + Apr (30) = 120 days -> 2024-04-30
        val label2024 = nextEligibleLabel("2024-01-01T00:00:00Z")
        assertEquals("2024-04-30", label2024)

        // Non-leap year 2023: Jan 1 + 120 days -> 2023-05-01
        val label2023 = nextEligibleLabel("2023-01-01T00:00:00Z")
        assertEquals("2023-05-01", label2023)
    }

    @Test
    fun shortDate_returnsFirst10CharactersOrEmpty() {
        assertEquals("", shortDate(null))
        assertEquals("", shortDate(""))
        assertEquals("2024-08-20", shortDate("2024-08-20T14:32:00Z"))
        assertEquals("2024-08-20", shortDate("2024-08-20"))
        assertEquals("short", shortDate("short"))
    }

    @Test
    fun ageFromDob_nullOrBlank_returnsNull() {
        assertNull(ageFromDob(null))
        assertNull(ageFromDob(""))
        assertNull(ageFromDob("   "))
        assertNull(ageFromDob("invalid-date"))
    }

    @Test
    fun ageFromDob_validDates_returnsCalculatedAge() {
        val now = LocalDate.now()
        val twentyYearsAgo = now.minusYears(20).toString()
        assertEquals(20, ageFromDob(twentyYearsAgo))

        val newborn = now.toString()
        assertEquals(0, ageFromDob(newborn))
    }

    @Test
    fun ageFromDob_boundaryConditions_returnsExpected() {
        val now = LocalDate.now()
        // Future date (negative age) must return null
        val future = now.plusYears(1).toString()
        assertNull(ageFromDob(future))

        // Age over 120 must return null
        val ancient = now.minusYears(125).toString()
        assertNull(ageFromDob(ancient))

        // Age 120 boundary
        val exactly120 = now.minusYears(120).toString()
        assertEquals(120, ageFromDob(exactly120))
    }
}
