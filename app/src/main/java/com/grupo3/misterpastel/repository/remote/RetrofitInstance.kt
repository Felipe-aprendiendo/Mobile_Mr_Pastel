package com.grupo3.misterpastel.repository.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Configuración Singleton de Retrofit.
 * Se conecta a la API REST creada en Oracle APEX.
 */
object RetrofitInstance {

    // ✔ Base URL correcta (termina con /api/)
    private const val BASE_URL =
        "https://g382daee58087c5-mrpastelreact.adb.sa-santiago-1.oraclecloudapps.com/ords/mr_pastel/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // ✔ Retrofit concatenará "productos/" desde ApiService
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(ApiService::class.java)
    }
}
