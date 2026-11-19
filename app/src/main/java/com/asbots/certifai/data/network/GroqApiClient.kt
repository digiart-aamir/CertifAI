package com.asbots.certifai.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

object GroqConfig {
    val API_KEY: String = System.getenv("GROQ_API_KEY") ?: ""
    const val BASE_URL = "https://api.groq.com/openai/v1/"
}

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqChatRequest(
    val model: String = "llama-3.3-70b-versatile",
    val messages: List<GroqMessage>,
    val temperature: Double = 0.7
)

data class GroqChoice(
    val message: GroqMessage
)

data class GroqChatResponse(
    val choices: List<GroqChoice>
)

interface GroqApiService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authHeader: String = "Bearer ${GroqConfig.API_KEY}",
        @Body request: GroqChatRequest
    ): Response<GroqChatResponse>
}

object GroqApiClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: GroqApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GroqConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqApiService::class.java)
    }
}
