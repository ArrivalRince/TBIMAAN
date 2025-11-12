package com.example.tbimaan.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(
    val nama_masjid: String,
    val email: String,
    val password: String,
    val alamat: String
)

data class RegisterResponse(val message: String)

// Data untuk Login
data class LoginRequest(
    val email: String,
    val password: String
)

data class UserData(
    val id_user: Int,
    val nama_masjid: String,
    val email: String,
    val alamat: String
)

data class LoginResponse(
    val message: String,
    val token: String?,
    val user: UserData?
)

// API Service
interface ApiService {
    @POST("api/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
}