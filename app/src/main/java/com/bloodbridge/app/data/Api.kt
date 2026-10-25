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
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface BloodBridgeApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginBody): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body body: RegisterBody): AuthResponse

    @GET("auth/me")
    suspend fun me(): MeResponse

    @GET("users/search-donors")
    suspend fun searchDonors(
        @Query("bloodGroup") bloodGroup: String?,
        @Query("district") district: String?,
        @Query("nearMe") nearMe: Boolean?,
    ): DonorsResponse
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
