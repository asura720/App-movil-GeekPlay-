package com.example.geekplayproyecto.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // URLs para el Emulador Android (10.0.2.2 apunta a tu PC)
    private const val USER_SERVICE_URL = "http://10.0.2.2:8083/"
    private const val CONTENT_SERVICE_URL = "http://10.0.2.2:8081/"
    private const val INTERACTION_SERVICE_URL = "http://10.0.2.2:8082/"
    private const val MODERATION_SERVICE_URL = "http://10.0.2.2:8084/"

    // Cliente HTTP con logs para ver errores en el Logcat
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // 1. Servicio de Usuarios
    val userApi: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl(USER_SERVICE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }


    // ... (código anterior) ...

    // 2. Servicio de Contenido
    val contentApi: ContentApiService by lazy {
        Retrofit.Builder()
            .baseUrl(CONTENT_SERVICE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ContentApiService::class.java)
    }

    // 3. Servicio de Interacción
    val interactionApi: InteractionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(INTERACTION_SERVICE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InteractionApiService::class.java)
    }

    // 4. Servicio de Moderación
    val moderationApi: ModerationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(MODERATION_SERVICE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ModerationApiService::class.java)
    }

    // (Aquí agregaremos las otras APIs a medida que las necesitemos)
}