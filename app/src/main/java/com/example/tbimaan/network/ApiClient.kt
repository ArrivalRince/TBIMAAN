package com.example.tbimaan.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // ================== PERBAIKAN UTAMA DI SINI ==================
    // BASE_URL SEHARUSNYA HANYA BERISI SKEMA (http), ALAMAT IP, DAN PORT.
    // Pastikan diakhiri dengan garis miring '/'.
    // JANGAN sertakan path seperti "/api/" di sini.
    const val BASE_URL = "http://192.168.100.79:5000/"

    // =============================================================

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}
