package com.bloodbridge.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelsSerializationTest {

    @Test
    fun userSerialization_defaultsAndJsonRoundtrip() {
        val user = User(
            id = "usr_123",
            fullName = "Rahim Ahmed",
            email = "rahim@example.com",
            phone = "01712345678",
            bloodGroup = "O+",
            gender = "male",
            district = "Dhaka",
            upazila = "Dhanmondi",
            address = "Road 27",
            role = "donor",
            activeMode = "donor",
            isAvailable = true,
            isVerified = true,
            profileComplete = true,
            lastDonationDate = "2024-06-01T00:00:00Z",
            dateOfBirth = "1998-05-10",
            savedDonors = listOf("usr_456", "usr_789")
        )

        val json = AppJson.encodeToString(User.serializer(), user)
        val decoded = AppJson.decodeFromString(User.serializer(), json)

        assertEquals(user.id, decoded.id)
        assertEquals(user.fullName, decoded.fullName)
        assertEquals(user.email, decoded.email)
        assertEquals(user.phone, decoded.phone)
        assertEquals(user.bloodGroup, decoded.bloodGroup)
        assertEquals(user.gender, decoded.gender)
        assertEquals(user.district, decoded.district)
        assertEquals(user.upazila, decoded.upazila)
        assertEquals(user.address, decoded.address)
        assertEquals(user.role, decoded.role)
        assertEquals(user.activeMode, decoded.activeMode)
        assertTrue(decoded.isAvailable)
        assertTrue(decoded.isVerified)
        assertTrue(decoded.profileComplete)
        assertEquals("2024-06-01T00:00:00Z", decoded.lastDonationDate)
        assertEquals("1998-05-10", decoded.dateOfBirth)
        assertEquals(listOf("usr_456", "usr_789"), decoded.savedDonors)
    }

    @Test
    fun userDeserialization_withUnknownKeys_doesNotThrow() {
        val jsonWithExtras = """
            {
                "id": "u999",
                "fullName": "Karim Ullah",
                "email": "karim@example.com",
                "unknownField": 12345,
                "futureApiPayload": {"nested": true}
            }
        """.trimIndent()

        val user = AppJson.decodeFromString(User.serializer(), jsonWithExtras)
        assertEquals("u999", user.id)
        assertEquals("Karim Ullah", user.fullName)
        assertEquals("karim@example.com", user.email)
        // Defaults preserved
        assertEquals("donor", user.role)
        assertTrue(user.isAvailable)
    }

    @Test
    fun donorDeserialization_handlesOptionalAndComputedFields() {
        val donorJson = """
            {
                "id": "d1",
                "fullName": "Fatima Begum",
                "bloodGroup": "B+",
                "district": "Chattogram",
                "upazila": "Pahartali",
                "age": 28,
                "daysUntilEligible": 45,
                "distanceKm": 3.75
            }
        """.trimIndent()

        val donor = AppJson.decodeFromString(Donor.serializer(), donorJson)
        assertEquals("d1", donor.id)
        assertEquals("Fatima Begum", donor.fullName)
        assertEquals("B+", donor.bloodGroup)
        assertEquals("Chattogram", donor.district)
        assertEquals("Pahartali", donor.upazila)
        assertEquals(28, donor.age)
        assertEquals(45, donor.daysUntilEligible)
        assertEquals(3.75, donor.distanceKm ?: 0.0, 0.001)
    }

    @Test
    fun bloodRequestDeserialization_verifiesDefaultValues() {
        val requestJson = """
            {
                "id": "req_001",
                "bloodGroup": "A-",
                "hospital": "Dhaka Medical College Hospital",
                "district": "Dhaka",
                "urgency": "High",
                "neededByDate": "2024-12-01"
            }
        """.trimIndent()

        val request = AppJson.decodeFromString(BloodRequest.serializer(), requestJson)
        assertEquals("req_001", request.id)
        assertEquals("A-", request.bloodGroup)
        assertEquals(1, request.units)
        assertEquals("pending", request.status)
        assertFalse(request.isEmergency)
        assertNull(request.targetDonorUid)
    }

    @Test
    fun errorResponseDeserialization_parsesMessageAndFieldErrors() {
        val errorJson = """
            {
                "success": false,
                "message": "Validation failed",
                "errors": [
                    {"msg": "Phone is invalid"},
                    {"msg": "Blood group is required"}
                ]
            }
        """.trimIndent()

        val err = AppJson.decodeFromString(ErrorResponse.serializer(), errorJson)
        assertFalse(err.success)
        assertEquals("Validation failed", err.message)
        assertEquals(2, err.errors.size)
        assertEquals("Phone is invalid", err.errors[0].msg)
        assertEquals("Blood group is required", err.errors[1].msg)
    }

    @Test
    fun okResponse_defaultIsTrue() {
        val ok = AppJson.decodeFromString(OkResponse.serializer(), "{}")
        assertTrue(ok.success)
    }
}
