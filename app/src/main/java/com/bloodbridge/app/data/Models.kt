package com.bloodbridge.app.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val bloodGroup: String = "",
    val gender: String = "",
    val district: String = "",
    val upazila: String = "",
    val address: String = "",
    val photoURL: String = "",
    val role: String = "donor",
    val activeMode: String = "donor",
    val isAvailable: Boolean = true,
    val isVerified: Boolean = false,
    val profileComplete: Boolean = true,
    val lastDonationDate: String? = null,
)

@Serializable
data class AuthResponse(
    val success: Boolean = true,
    val token: String = "",
    val user: User = User(),
)

@Serializable
data class MeResponse(val success: Boolean = true, val user: User = User())

@Serializable
data class LoginBody(val firebaseIdToken: String)

@Serializable
data class RegisterBody(
    val idToken: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val bloodGroup: String,
    val gender: String,
    val dateOfBirth: String,
    val district: String,
    val upazila: String,
    val address: String,
)

@Serializable
data class Donor(
    val id: String = "",
    val fullName: String = "",
    val bloodGroup: String = "",
    val district: String = "",
    val upazila: String = "",
    val address: String = "",
    val photoURL: String = "",
    val isVerified: Boolean = false,
    val isAvailable: Boolean = true,
    val phone: String = "",
    val email: String = "",
    val age: Int? = null,
    val daysUntilEligible: Int = 0,
    val distanceKm: Double? = null,
)

@Serializable
data class DonorsResponse(val success: Boolean = true, val donors: List<Donor> = emptyList())

@Serializable
data class FieldError(val msg: String = "")

@Serializable
data class ErrorResponse(
    val success: Boolean = false,
    val message: String = "",
    val errors: List<FieldError> = emptyList(),
)

object Constants {
    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    val genders = listOf("male", "female", "other")
    val districts = listOf(
        "Bagerhat", "Bandarban", "Barguna", "Barishal", "Bhola", "Bogura",
        "Brahmanbaria", "Chandpur", "Chattogram", "Chuadanga", "Cox's Bazar",
        "Cumilla", "Dhaka", "Dinajpur", "Faridpur", "Feni", "Gaibandha",
        "Gazipur", "Gopalganj", "Habiganj", "Jamalpur", "Jashore", "Jhalokati",
        "Jhenaidah", "Joypurhat", "Khagrachhari", "Khulna", "Kishoreganj",
        "Kurigram", "Kushtia", "Lakshmipur", "Lalmonirhat", "Madaripur",
        "Magura", "Manikganj", "Meherpur", "Moulvibazar", "Munshiganj",
        "Mymensingh", "Naogaon", "Narail", "Narayanganj", "Narsingdi",
        "Natore", "Nawabganj", "Netrokona", "Nilphamari", "Noakhali",
        "Pabna", "Panchagarh", "Patuakhali", "Pirojpur", "Rajbari",
        "Rajshahi", "Rangamati", "Rangpur", "Satkhira", "Shariatpur",
        "Sherpur", "Sirajganj", "Sunamganj", "Sylhet", "Tangail", "Thakurgaon",
    )
}
