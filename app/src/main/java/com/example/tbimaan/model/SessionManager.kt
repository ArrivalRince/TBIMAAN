package com.example.tbimaan.model

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "user_session_prefs"
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val KEY_ID_USER = "key_id_user"
        private const val KEY_NAMA_MASJID = "key_nama_masjid"
        private const val KEY_EMAIL = "key_email"
    }

    fun createLoginSession(
        idUser: Int,
        namaMasjid: String,
        email: String,
    ) {
        prefs.edit().apply {
            putBoolean(IS_LOGGED_IN, true)
            putInt(KEY_ID_USER, idUser)
            putString(KEY_NAMA_MASJID, namaMasjid)
            putString(KEY_EMAIL, email)
            apply()
        }
    }


    fun logoutUser() {
        prefs.edit().clear().apply()
    }


    val isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)


    val idUser: Int?
        get() = if (isLoggedIn)
            prefs.getInt(KEY_ID_USER, -1).takeIf { it != -1 }
        else null


    val namaMasjid: String?
        get() = prefs.getString(KEY_NAMA_MASJID, null)


    val email: String?
        get() = prefs.getString(KEY_EMAIL, null)
}