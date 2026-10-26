package com.bloodbridge.app.data

import android.content.Context
import com.bloodbridge.app.BuildConfig
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface BloodBridgeApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginBody): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body body: RegisterBody): AuthResponse

    @POST("auth/google")
    suspend fun google(@Body body: GoogleBody): AuthResponse

    @GET("auth/me")
    suspend fun me(): MeResponse

    @GET("users/search-donors")
    suspend fun searchDonors(
        @Query("bloodGroup") bloodGroup: String?,
        @Query("district") district: String?,
        @Query("upazila") upazila: String?,
        @Query("nearMe") nearMe: Boolean?,
    ): DonorsResponse

    @POST("requests")
    suspend fun createRequest(@Body body: CreateRequestBody): OkResponse

    @GET("requests/my-requests")
    suspend fun myRequests(): RequestsResponse

    @GET("requests/incoming")
    suspend fun incomingRequests(): RequestsResponse

    @GET("requests/nearby")
    suspend fun nearbyRequests(): RequestsResponse

    @PATCH("requests/{id}/cancel")
    suspend fun cancelRequest(@Path("id") id: String): OkResponse

    @PATCH("requests/{id}/respond")
    suspend fun respond(@Path("id") id: String, @Body body: RespondBody): OkResponse

    @PATCH("users/availability")
    suspend fun setAvailability(@Body body: AvailabilityBody): OkResponse

    @POST("users/record-donation")
    suspend fun recordDonation(@Body body: EmptyBody): RecordDonationResponse

    @GET("users/donation-history")
    suspend fun donationHistory(): HistoryResponse

    @PUT("users/profile")
    suspend fun updateProfile(@Body body: UpdateProfileBody): UserResponse

    @DELETE("users/me")
    suspend fun deleteMe(): OkResponse

    @GET("organizations")
    suspend fun organizations(
        @Query("district") district: String?,
        @Query("type") type: String?,
        @Query("search") search: String?,
    ): OrganizationsResponse

    @GET("users/saved-donors")
    suspend fun savedDonors(): DonorsResponse

    @PATCH("users/saved-donors/{id}")
    suspend fun toggleSaved(@Path("id") id: String): SavedToggleResponse

    @PATCH("users/mode")
    suspend fun setMode(@Body body: ModeBody): ModeResponse

    @POST("feedback")
    suspend fun sendFeedback(@Body body: FeedbackBody): OkResponse

    @GET("feedback/my")
    suspend fun myFeedback(): FeedbackListResponse

    @GET("notifications")
    suspend fun notifications(): NotificationsResponse

    @PATCH("notifications/{id}/read")
    suspend fun markRead(@Path("id") id: String): OkResponse

    @PATCH("notifications/read-all")
    suspend fun markAllRead(): OkResponse
}

/** Keeps the backend JWT on the device (SharedPreferences). */
class Session(context: Context) {
    private val prefs = context.getSharedPreferences("bloodbridge", Context.MODE_PRIVATE)
    var token: String?
        get() = prefs.getString("token", null)
        set(value) { prefs.edit().apply { if (value == null) remove("token") else putString("token", value) }.apply() }
}

val AppJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    explicitNulls = false
}

object ApiProvider {
    fun create(session: Session): BloodBridgeApi {
        val authInterceptor = Interceptor { chain ->
            val builder = chain.request().newBuilder()
            session.token?.let { builder.addHeader("Authorization", "Bearer $it") }
            chain.proceed(builder.build())
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(AppJson.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(BloodBridgeApi::class.java)
    }
}
