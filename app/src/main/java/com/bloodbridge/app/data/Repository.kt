package com.bloodbridge.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException

/** Turns any failure into a short, human-readable message. */
fun Throwable.friendlyMessage(): String {
    if (this is HttpException) {
        val body = runCatching { response()?.errorBody()?.string() }.getOrNull()
        val parsed = body?.let { runCatching { AppJson.decodeFromString<ErrorResponse>(it) }.getOrNull() }
        val msg = parsed?.message?.takeIf { it.isNotBlank() } ?: parsed?.errors?.firstOrNull()?.msg
        if (!msg.isNullOrBlank()) return msg
        return "Server error (${code()})"
    }
    val m = message.orEmpty()
    return when {
        m.contains("password is invalid", true) || m.contains("INVALID_LOGIN_CREDENTIALS", true) ||
            m.contains("supplied auth credential", true) -> "Email or password is incorrect."
        m.contains("no user record", true) -> "No account found with this email."
        m.contains("already in use", true) -> "This email is already registered."
        m.contains("network", true) || m.contains("Unable to resolve host", true) -> "No internet connection."
        m.isBlank() -> "Something went wrong. Please try again."
        else -> m
    }
}

class Repository(private val api: BloodBridgeApi, private val session: Session) {
    private val auth get() = FirebaseAuth.getInstance()

    val hasSession: Boolean get() = session.token != null && auth.currentUser != null

    suspend fun restore(): User? {
        if (!hasSession) return null
        return try {
            api.me().user
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) logout()
            null
        }
    }

    suspend fun login(email: String, password: String): User {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
        val idToken = auth.currentUser!!.getIdToken(true).await().token!!
        val res = api.login(LoginBody(idToken))
        session.token = res.token
        return res.user
    }

    suspend fun register(
        fullName: String, email: String, password: String, phone: String, bloodGroup: String,
        gender: String, dateOfBirth: String, district: String, upazila: String, address: String,
    ): User {
        val cred = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val fbUser = cred.user!!
        fbUser.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(fullName).build()).await()
        runCatching { fbUser.sendEmailVerification().await() }
        val idToken = fbUser.getIdToken(true).await().token!!
        val res = api.register(
            RegisterBody(idToken, fullName, email.trim(), phone, bloodGroup, gender, dateOfBirth, district, upazila, address)
        )
        session.token = res.token
        return res.user
    }

    suspend fun searchDonors(bloodGroup: String?, district: String?, nearMe: Boolean): List<Donor> =
        api.searchDonors(
            bloodGroup?.takeIf { it.isNotBlank() },
            district?.takeIf { it.isNotBlank() },
            if (nearMe) true else null,
        ).donors

    fun logout() {
        auth.signOut()
        session.token = null
    }
}
