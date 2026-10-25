package com.bloodbridge.app.data

import java.time.Instant
import java.time.temporal.ChronoUnit

const val DONATION_COOLDOWN_DAYS = 120

/** Whole days remaining until the donor may donate again (0 = eligible). */
fun daysUntilEligible(lastDonationDate: String?): Int {
    if (lastDonationDate.isNullOrBlank()) return 0
    return try {
        val next = Instant.parse(lastDonationDate).plus(DONATION_COOLDOWN_DAYS.toLong(), ChronoUnit.DAYS)
        val diffMs = next.toEpochMilli() - System.currentTimeMillis()
        if (diffMs > 0) Math.ceil(diffMs / 86_400_000.0).toInt() else 0
    } catch (e: Exception) {
        0
    }
}

fun nextEligibleLabel(lastDonationDate: String?): String? {
    if (lastDonationDate.isNullOrBlank()) return null
    return try {
        Instant.parse(lastDonationDate).plus(DONATION_COOLDOWN_DAYS.toLong(), ChronoUnit.DAYS).toString().take(10)
    } catch (e: Exception) {
        null
    }
}

fun shortDate(iso: String?): String = iso?.take(10).orEmpty()
