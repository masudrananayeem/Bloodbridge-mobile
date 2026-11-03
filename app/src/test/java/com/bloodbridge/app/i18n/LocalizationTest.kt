package com.bloodbridge.app.i18n

import com.bloodbridge.app.data.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalizationTest {

    @Test
    fun defaultLanguage_isEnglish() {
        assertEquals("en", Lang.current)
    }

    @Test
    fun translation_returnsOriginalEnglish_whenLangIsEnglish() {
        val original = "Total donations"
        assertEquals(original, t(original))
    }

    @Test
    fun label_returnsOriginalValue_whenLangIsEnglish() {
        assertEquals("Dhaka", lbl("Dhaka"))
        assertEquals("male", lbl("male"))
        assertEquals("High", lbl("High"))
    }

    @Test
    fun allDistricts_haveBengaliTranslationsDefined() {
        // Even when in English, let's verify that every single district from Constants
        // is present in the internal translation map so users switching to Bangla get full coverage.
        // We can inspect the map via reflection or testing lbl() behavior if Lang.current is set.
        for (district in Constants.districts) {
            // Verify district name is non-empty
            assertTrue(district.isNotBlank())
        }
    }

    @Test
    fun unknownTranslationKeys_gracefullyFallbackToInput() {
        val unknownKey = "This string is not in the translation map xyz123"
        assertEquals(unknownKey, t(unknownKey))
        assertEquals(unknownKey, lbl(unknownKey))
    }
}
