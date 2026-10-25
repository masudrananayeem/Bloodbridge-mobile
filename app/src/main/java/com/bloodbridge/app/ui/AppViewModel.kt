package com.bloodbridge.app.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bloodbridge.app.data.ApiProvider
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

    init {
        viewModelScope.launch {
            user = repo.restore()
            booting = false
        }
    }

    fun clearError() { error = null }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) { error = "Enter email and password."; return }
        launchBusy { user = repo.login(email, password) }
    }

    fun register(
        fullName: String, email: String, password: String, confirm: String, phone: String,
        bloodGroup: String, gender: String, dateOfBirth: String, district: String, upazila: String, address: String,
    ) {
        val missing = listOf(fullName, email, password, phone, bloodGroup, gender, dateOfBirth, district, upazila, address)
            .any { it.isBlank() }
        when {
            missing -> error = "Please fill in all fields."
            password.length < 6 -> error = "Password must be at least 6 characters."
            password != confirm -> error = "Passwords do not match."
            !Regex("^01[3-9]\\d{8}$").matches(phone) -> error = "Enter a valid Bangladeshi phone number (01XXXXXXXXX)."
            !Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(dateOfBirth) -> error = "Date of birth must be YYYY-MM-DD."
            else -> launchBusy {
                user = repo.register(fullName, email, password, phone, bloodGroup, gender, dateOfBirth, district, upazila, address)
            }
        }
    }

    fun search(bloodGroup: String, district: String, nearMe: Boolean) {
        launchBusy {
            donors = repo.searchDonors(bloodGroup, district, nearMe)
            searched = true
        }
    }

    fun logout() {
        repo.logout()
        user = null
        donors = emptyList()
        searched = false
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
