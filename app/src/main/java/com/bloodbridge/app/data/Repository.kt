package com.bloodbridge.app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.bloodbridge.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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

    suspend fun me(): User = api.me().user

    suspend fun createRequest(body: CreateRequestBody) = api.createRequest(body)
    suspend fun myRequests() = api.myRequests().requests
    suspend fun incoming() = api.incomingRequests().requests
    suspend fun nearby() = api.nearbyRequests().requests
    suspend fun cancelRequest(id: String) = api.cancelRequest(id)
    suspend fun respond(id: String, action: String) = api.respond(id, RespondBody(action))
    suspend fun setAvailability(value: Boolean) = api.setAvailability(AvailabilityBody(value))
    suspend fun recordDonation(): User = api.recordDonation(EmptyBody()).user
    suspend fun history() = api.donationHistory().history
    suspend fun notifications() = api.notifications()
    suspend fun markRead(id: String) = api.markRead(id)
    suspend fun markAllRead() = api.markAllRead()

    suspend fun updateProfile(body: UpdateProfileBody): User = api.updateProfile(body).user

    suspend fun organizations(district: String?, type: String?, search: String?) =
        api.organizations(
            district?.takeIf { it.isNotBlank() },
            type?.takeIf { it.isNotBlank() },
            search?.takeIf { it.isNotBlank() },
        ).organizations

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    /** Uploads a profile photo straight to Cloudinary (unsigned preset) and returns its https URL. */
    suspend fun uploadPhoto(bytes: ByteArray): String = withContext(Dispatchers.IO) {
        require(BuildConfig.CLOUDINARY_CLOUD_NAME.isNotBlank() && BuildConfig.CLOUDINARY_UPLOAD_PRESET.isNotBlank()) {
            "Cloudinary is not configured (CLOUDINARY_CLOUD_NAME / CLOUDINARY_UPLOAD_PRESET in gradle.properties)."
        }
        require(bytes.size <= 10 * 1024 * 1024) { "Image is too large (max 10 MB)." }
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("upload_preset", BuildConfig.CLOUDINARY_UPLOAD_PRESET)
            .addFormDataPart("file", "photo.jpg", bytes.toRequestBody("image/*".toMediaType()))
            .build()
        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/${BuildConfig.CLOUDINARY_CLOUD_NAME}/image/upload")
            .post(body)
            .build()
        OkHttpClient().newCall(request).execute().use { res ->
            val text = res.body?.string().orEmpty()
            if (!res.isSuccessful) throw IllegalStateException("Photo upload failed (${res.code}).")
            AppJson.parseToJsonElement(text).jsonObject["secure_url"]?.jsonPrimitive?.content
                ?: throw IllegalStateException("Photo upload failed.")
        }
    }

    /** Deletes the account on the backend (which also removes the Firebase user), then signs out locally. */
    suspend fun deleteAccount() {
        api.deleteMe()
        logout()
    }

    fun logout() {
        auth.signOut()
        session.token = null
    }
}
