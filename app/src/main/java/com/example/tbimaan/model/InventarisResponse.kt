package com.example.tbimaan.model

import com.google.gson.annotations.SerializedName

data class InventarisResponse(
    @SerializedName("id_inventaris")
    val idInventaris: Int?,

    @SerializedName("id_user")
    val idUser: Int?,

    @SerializedName("nama_barang")
    val namaBarang: String?,

    @SerializedName("kondisi")
    val kondisi: String?,

    @SerializedName("foto_barang")
    val fotoBarang: String?,

    @SerializedName("tanggal")
    val tanggal: String?,

    @SerializedName("jumlah")
    val jumlah: Int?
)
