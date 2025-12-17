package com.example.tbimaan.model

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private var prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "user_session_prefs"
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val KEY_ID_USER = "key_id_user"
        private const val KEY_NAMA_MASJID = "key_nama_masjid"
        private const val KEY_EMAIL = "key_email"
        private const val KEY_ALAMAT = "key_alamat"
    }

    // =======================================================================
    // ===                 PERBAIKAN UTAMA DAN FINAL ADA DI SINI           ===
    // =======================================================================
    /**
     * Menyimpan data sesi login.
     * PERBAIKAN: Parameter `alamat` sekarang boleh null (String?).
     */
    fun createLoginSession(
        idUser: Int,
        namaMasjid: String,
        email: String,
        alamat: String? // <-- UBAH MENJADI NULLABLE
    ) {
        val editor = prefs.edit()
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.putInt(KEY_ID_USER, idUser)
        editor.putString(KEY_NAMA_MASJID, namaMasjid)
        editor.putString(KEY_EMAIL, email)
        // Jika alamat null, simpan string kosong agar tidak error
        editor.putString(KEY_ALAMAT, alamat ?: "")
        editor.apply()
    }
    // =======================================================================

    /**
     * Menghapus semua data sesi. Dipanggil saat logout.
     */
    fun logoutUser() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    /**
     * Mengecek apakah pengguna sudah login atau belum.
     */
    val isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)

    /**
     * Mengambil ID pengguna yang tersimpan.
     */
    val idUser: Int?
        get() = if (isLoggedIn) prefs.getInt(KEY_ID_USER, -1).takeIf { it != -1 } else null

    /**
     * Mengambil nama masjid yang tersimpan.
     */
    val namaMasjid: String?
        get() = prefs.getString(KEY_NAMA_MASJID, null)

    /**
     * Mengambil alamat yang tersimpan.
     */
    val alamat: String?
        get() = prefs.getString(KEY_ALAMAT, null)

    // Anda bisa menambahkan getter lain jika perlu
}
