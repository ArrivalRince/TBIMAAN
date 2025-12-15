package com.example.tbimaan.model

object UserSession {
    var idUser: Int? = null
    var namaMasjid: String? = null
    var email: String? = null
    var alamat: String? = null

    fun clear() {
        idUser = null
        namaMasjid = null
        email = null
        alamat = null
    }
}

