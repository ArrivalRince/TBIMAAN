package com.example.tbimaan.model

import com.google.gson.annotations.SerializedName

/**
 * Data class ini mencocokkan struktur JSON dari backend (snake_case)
 * dengan variabel Kotlin (camelCase) menggunakan @SerializedName.
 *
 * KELEBIHAN:
 * ✔ Tidak perlu mengubah backend
 * ✔ Aplikasi tetap rapi dengan camelCase
 * ✔ Retrofit membaca data tanpa error
 */

data class KeuanganResponse(

    @SerializedName("id_transaksi")
    val idTransaksi: Int?,

    @SerializedName("id_user")
    val idUser: Int?,

    @SerializedName("keterangan")
    val keterangan: String?,

    @SerializedName("tipe_transaksi")
    val tipeTransaksi: String?,

    @SerializedName("tanggal")
    val tanggal: String?,

    @SerializedName("jumlah")
    val jumlah: Int?,

    @SerializedName("bukti_transaksi")
    val buktiTransaksi: String?     // ini URL lengkap dari backend
)
