package com.example.tbimaan.model

data class RegisterRequest(
    val nama_masjid: String,
    val email: String,
    val password: String,
    val alamat: String
)
