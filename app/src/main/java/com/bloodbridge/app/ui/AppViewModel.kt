package com.bloodbridge.app.ui

import com.bloodbridge.app.i18n.t
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bloodbridge.app.data.ApiProvider
import com.bloodbridge.app.data.AppNotification
import com.bloodbridge.app.data.BloodRequest
import com.bloodbridge.app.data.CreateRequestBody
import com.bloodbridge.app.data.Donation
import com.bloodbridge.app.data.AnalyticsResponse
import com.bloodbridge.app.data.Organization
import com.bloodbridge.app.data.OrgBody
import com.bloodbridge.app.data.UpdateProfileBody
import android.content.Context
import android.net.Uri
import com.bloodbridge.app.data.AdminStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.bloodbridge.app.data.Donor
import com.bloodbridge.app.data.Repository
import com.bloodbridge.app.data.Session
import com.bloodbridge.app.data.User
import com.bloodbridge.app.data.friendlyMessage
import kotlinx.coroutines.launch

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repository(ApiProvider.create(Session(app)), Session(app))

    var user by mutableStateOf<User?>(null)
        private set
    var booting by mutableStateOf(true)
        private set
    var busy by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    var donors by mutableStateOf<List<Donor>>(emptyList())
        private set
    var searched by mutableStateOf(false)
        private set

    var info by mutableStateOf<String?>(null)
        private set
    fun clearInfo() { info = null }

    var myRequests by mutableStateOf<List<BloodRequest>>(emptyList())
        private set
    var incoming by mutableStateOf<List<BloodRequest>>(emptyList())
        private set
    var nearby by mutableStateOf<List<BloodRequest>>(emptyList())
        private set
    var notifications by mutableStateOf<List<AppNotification>>(emptyList())
        private set
    var unreadCount by mutableStateOf(0)
        private set
    var history by mutableStateOf<List<Donation>>(emptyList())
        private set

    var organizations by mutableStateOf<List<Organization>>(emptyList())
        private set

    var savedDonors by mutableStateOf<List<Donor>>(emptyList())
        private set
    var emailVerified by mutableStateOf(true)
        private set
    var analytics by mutableStateOf(AnalyticsResponse())
        private set

    // ---- admin ----
    var adminStats by mutableStateOf(AdminStats())
        private set
    var adminUsers by mutableStateOf<List<User>>(emptyList())
        private set
    var adminUsersPage by mutableStateOf(1)
        private set
    var adminUsersPages by mutableStateOf(1)
        private set
    var adminRequests by mutableStateOf<List<BloodRequest>>(emptyList())
        private set

    init {
        viewModelScope.launch {
            user = repo.restore()
            emailVerified = repo.isEmailVerified()
            booting = false
        }
    }

    fun clearError() { error = null }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) { error = t("Enter email and password."); return }
        info = null
        launchBusy { user = repo.login(email, password); emailVerified = repo.isEmailVerified() }
    }

    fun register(
        fullName: String, email: String, password: String, confirm: String, phone: String,
        bloodGroup: String, gender: String, dateOfBirth: String, district: String, upazila: String, address: String,
    ) {
        val missing = listOf(fullName, email, password, phone, bloodGroup, gender, dateOfBirth, district, upazila, address)
            .any { it.isBlank() }
        when {
            missing -> error = t("Please fill in all fields.")
            password.length < 6 -> error = t("Password must be at least 6 characters.")
            password != confirm -> error = t("Passwords do not match.")
            !Regex("^01[3-9]\\d{8}$").matches(phone) -> error = t("Enter a valid Bangladeshi phone number (01XXXXXXXXX).")
            !Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(dateOfBirth) -> error = t("Date of birth must be YYYY-MM-DD.")
            else -> launchBusy {
                user = repo.register(fullName, email, password, phone, bloodGroup, gender, dateOfBirth, district, upazila, address)
                emailVerified = repo.isEmailVerified()
            }
        }
    }

    fun search(bloodGroup: String, district: String, nearMe: Boolean) {
        launchBusy {
            donors = repo.searchDonors(bloodGroup, district, nearMe)
            searched = true
        }
    }

    fun loadRequests() {
        launchBusy {
            myRequests = repo.myRequests()
            incoming = repo.incoming()
            nearby = repo.nearby()
        }
    }

    fun createRequest(body: CreateRequestBody, onDone: () -> Unit) {
        launchBusy {
            repo.createRequest(body)
            info = if (body.isEmergency) t("Emergency request sent to the nearest donors.") else t("Request sent.")
            myRequests = repo.myRequests()
            onDone()
        }
    }

    fun cancelRequest(id: String) {
        launchBusy {
            repo.cancelRequest(id)
            myRequests = repo.myRequests()
            info = t("Request cancelled.")
        }
    }

    fun respond(id: String, action: String) {
        launchBusy {
            repo.respond(id, action)
            incoming = repo.incoming()
            nearby = repo.nearby()
            info = if (action == "accept") t("You accepted this request. The seeker can now see your contact.") else t("Request declined.")
        }
    }

    fun loadDonorDashboard() {
        launchBusy {
            user = repo.me()
            history = repo.history()
        }
    }

    fun setAvailability(value: Boolean) {
        launchBusy {
            repo.setAvailability(value)
            user = repo.me()
        }
    }

    fun recordDonation() {
        launchBusy {
            user = repo.recordDonation()
            history = repo.history()
            info = t("Donation recorded. Thank you!")
        }
    }

    fun loadNotifications() {
        launchBusy {
            val res = repo.notifications()
            notifications = res.notifications
            unreadCount = res.unreadCount
        }
    }

    fun markRead(id: String) {
        launchBusy {
            repo.markRead(id)
            val res = repo.notifications()
            notifications = res.notifications
            unreadCount = res.unreadCount
        }
    }

    fun markAllRead() {
        launchBusy {
            repo.markAllRead()
            val res = repo.notifications()
            notifications = res.notifications
            unreadCount = res.unreadCount
        }
    }

    fun updateProfile(body: UpdateProfileBody) {
        launchBusy {
            user = repo.updateProfile(body)
            info = t("Profile updated.")
        }
    }

    fun uploadPhoto(uri: Uri) {
        launchBusy {
            val bytes = withContext(Dispatchers.IO) {
                getApplication<Application>().contentResolver.openInputStream(uri)?.use { it.readBytes() }
            } ?: throw IllegalStateException(t("Could not read the selected image."))
            val url = repo.uploadPhoto(bytes)
            user = repo.updateProfile(UpdateProfileBody(photoURL = url))
            info = t("Profile photo updated.")
        }
    }

    fun loadOrganizations(district: String, type: String, search: String) {
        launchBusy { organizations = repo.organizations(district, type, search) }
    }

    fun resetPassword(email: String, onDone: () -> Unit) {
        if (email.isBlank()) { error = t("Enter your email address."); return }
        launchBusy {
            repo.sendPasswordReset(email)
            info = t("Password reset email sent. Check your inbox.")
            onDone()
        }
    }

    fun deleteAccount() {
        launchBusy {
            repo.deleteAccount()
            user = null
        }
    }

    fun loadSavedDonors() {
        launchBusy { savedDonors = repo.savedDonors() }
    }

    fun toggleSaved(donor: Donor) {
        viewModelScope.launch {
            try {
                val res = repo.toggleSaved(donor.id)
                user = user?.copy(savedDonors = res.savedDonors)
                if (!res.saved) savedDonors = savedDonors.filter { it.id != donor.id }
            } catch (e: Exception) {
                error = e.friendlyMessage()
            }
        }
    }

    fun setMode(mode: String) {
        if (user?.activeMode == mode) return
        launchBusy {
            repo.setMode(mode)
            user = user?.copy(activeMode = mode)
        }
    }

    fun checkEmailVerified() {
        launchBusy {
            emailVerified = repo.reloadEmailVerified()
            if (!emailVerified) info = t("Your email is not verified yet.")
        }
    }

    fun resendVerification() {
        launchBusy {
            repo.resendVerification()
            info = t("Verification email sent.")
        }
    }

    fun loadAnalytics() {
        launchBusy { analytics = repo.adminAnalytics() }
    }

    fun loadAdminOrgs() {
        launchBusy { organizations = repo.organizations(null, null, null) }
    }

    fun saveOrg(id: String?, body: OrgBody, onDone: () -> Unit) {
        launchBusy {
            repo.saveOrg(id, body)
            organizations = repo.organizations(null, null, null)
            info = t("Organization saved.")
            onDone()
        }
    }

    fun verifyOrg(id: String) {
        launchBusy {
            repo.verifyOrg(id)
            organizations = repo.organizations(null, null, null)
        }
    }

    fun deleteOrg(id: String) {
        launchBusy {
            repo.deleteOrg(id)
            organizations = repo.organizations(null, null, null)
            info = t("Organization deleted.")
        }
    }

    fun googleLogin(context: Context) {
        info = null
        launchBusy { user = repo.googleLogin(context); emailVerified = true }
    }

    fun completeProfile(phone: String, bloodGroup: String, district: String, upazila: String, address: String, dob: String) {
        when {
            listOf(phone, bloodGroup, district, upazila, address).any { it.isBlank() } -> error = t("Please fill in all fields.")
            !Regex("^01[3-9]\\d{8}$").matches(phone) -> error = t("Enter a valid Bangladeshi phone number (01XXXXXXXXX).")
            dob.isNotBlank() && !Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(dob) -> error = t("Date of birth must be YYYY-MM-DD.")
            else -> launchBusy {
                user = repo.updateProfile(
                    UpdateProfileBody(
                        phone = phone, bloodGroup = bloodGroup, district = district, upazila = upazila.trim(),
                        address = address.trim(), dateOfBirth = dob.ifBlank { null },
                    )
                )
            }
        }
    }

    fun loadAdminStats() {
        launchBusy { adminStats = repo.adminStats() }
    }

    fun loadAdminUsers(search: String, loadMore: Boolean = false) {
        launchBusy {
            val page = if (loadMore) adminUsersPage + 1 else 1
            val res = repo.adminUsers(search, page)
            adminUsers = if (loadMore) adminUsers + res.users else res.users
            adminUsersPage = res.page
            adminUsersPages = res.pages
        }
    }

    fun adminVerify(id: String, search: String) {
        launchBusy {
            repo.adminVerify(id)
            val res = repo.adminUsers(search, 1)
            adminUsers = res.users; adminUsersPage = res.page; adminUsersPages = res.pages
            info = t("Verification updated.")
        }
    }

    fun adminDeleteUser(id: String, search: String) {
        launchBusy {
            repo.adminDeleteUser(id)
            val res = repo.adminUsers(search, 1)
            adminUsers = res.users; adminUsersPage = res.page; adminUsersPages = res.pages
            info = t("User deleted.")
        }
    }

    fun loadAdminRequests(status: String) {
        launchBusy { adminRequests = repo.adminRequests(status) }
    }

    fun adminSetStatus(id: String, status: String, filter: String) {
        launchBusy {
            repo.adminSetStatus(id, status)
            adminRequests = repo.adminRequests(filter)
            info = t("Status updated.")
        }
    }

    fun logout() {
        repo.logout()
        user = null
        donors = emptyList()
        searched = false
        adminUsers = emptyList(); adminRequests = emptyList(); savedDonors = emptyList()
        myRequests = emptyList(); incoming = emptyList(); nearby = emptyList()
        notifications = emptyList(); unreadCount = 0; history = emptyList()
    }

    private fun launchBusy(block: suspend () -> Unit) {
        viewModelScope.launch {
            busy = true
            error = null
            try {
                block()
            } catch (e: Exception) {
                error = e.friendlyMessage()
            } finally {
                busy = false
            }
        }
    }
}
