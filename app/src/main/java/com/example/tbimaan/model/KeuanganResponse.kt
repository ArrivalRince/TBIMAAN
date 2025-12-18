package com.example.tbimaan.model

import com.google.gson.annotations.SerializedName

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
    val buktiTransaksi: String?
)
