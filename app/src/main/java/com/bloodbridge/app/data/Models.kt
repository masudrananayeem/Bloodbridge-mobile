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
    val dateOfBirth: String? = null,
    val savedDonors: List<String> = emptyList(),
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

@Serializable
data class BloodRequest(
    val id: String = "",
    val seekerUid: String = "",
    val seekerName: String = "",
    val seekerPhone: String = "",
    val bloodGroup: String = "",
    val units: Int = 1,
    val hospital: String = "",
    val district: String = "",
    val upazila: String = "",
    val urgency: String = "",
    val reason: String = "",
    val message: String = "",
    val neededByDate: String? = null,
    val status: String = "pending",
    val isEmergency: Boolean = false,
    val targetDonorUid: String? = null,
    val targetDonorName: String? = null,
    val acceptedDonorName: String? = null,
    val acceptedDonorPhone: String? = null,
    val acceptedDonorEmail: String? = null,
    val createdAt: String? = null,
    val distanceKm: Double? = null,
)

@Serializable
data class RequestsResponse(val success: Boolean = true, val requests: List<BloodRequest> = emptyList())

@Serializable
data class CreateRequestBody(
    val bloodGroup: String,
    val units: Int,
    val hospital: String,
    val district: String,
    val upazila: String,
    val urgency: String,
    val neededByDate: String,
    val message: String = "",
    val reason: String = "",
    val targetDonorUid: String? = null,
    val isEmergency: Boolean = false,
)

@Serializable
data class RespondBody(val action: String)

@Serializable
data class AvailabilityBody(val isAvailable: Boolean)

@Serializable
class EmptyBody

@Serializable
data class OkResponse(val success: Boolean = true)

@Serializable
data class RecordDonationResponse(val success: Boolean = true, val user: User = User())

@Serializable
data class AppNotification(
    val id: String = "",
    val type: String = "",
    val message: String = "",
    val relatedRequestId: String? = null,
    val isRead: Boolean = false,
    val createdAt: String? = null,
)

@Serializable
data class NotificationsResponse(
    val success: Boolean = true,
    val notifications: List<AppNotification> = emptyList(),
    val unreadCount: Int = 0,
)

@Serializable
data class Donation(
    val id: String = "",
    val hospital: String = "",
    val units: Int = 1,
    val district: String = "",
    val bloodGroup: String = "",
    val donationDate: String? = null,
)

@Serializable
data class HistoryResponse(val success: Boolean = true, val history: List<Donation> = emptyList())

@Serializable
data class UpdateProfileBody(
    val fullName: String? = null,
    val phone: String? = null,
    val bloodGroup: String? = null,
    val district: String? = null,
    val upazila: String? = null,
    val address: String? = null,
    val photoURL: String? = null,
    val dateOfBirth: String? = null,
)

@Serializable
data class UserResponse(val success: Boolean = true, val user: User = User())

@Serializable
data class Organization(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val description: String = "",
    val district: String = "",
    val upazila: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val website: String = "",
    val isVerified: Boolean = false,
)

@Serializable
data class OrganizationsResponse(val success: Boolean = true, val organizations: List<Organization> = emptyList())

@Serializable
data class GoogleBody(val firebaseIdToken: String)

@Serializable
data class SavedToggleResponse(val success: Boolean = true, val saved: Boolean = false, val savedDonors: List<String> = emptyList())

@Serializable
data class ModeBody(val mode: String)

@Serializable
data class ModeResponse(val success: Boolean = true, val activeMode: String = "donor")


object Constants {
    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    val genders = listOf("male", "female", "other")
    val urgencies = listOf("Low", "Medium", "High")
    val requestStatuses = listOf("pending", "accepted", "completed", "cancelled", "rejected")
    val orgTypes = listOf("Voluntary Group", "NGO", "Blood Bank", "Hospital")
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


@Serializable
data class FeedbackBody(val category: String, val message: String)

@Serializable
data class FeedbackItem(
    val id: String = "",
    val category: String = "General",
    val message: String = "",
    val status: String = "new",
    val createdAt: String? = null,
)

@Serializable
data class FeedbackResponse(val success: Boolean = true, val feedback: List<FeedbackItem> = emptyList())
