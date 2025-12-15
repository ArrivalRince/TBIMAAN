package com.example.tbimaan.model

import com.google.gson.annotations.SerializedName

data class KegiatanResponse(

    @SerializedName("id_kegiatan")
    val idKegiatan: Int?,

    @SerializedName("id_user")
    val idUser: Int?,

    @SerializedName("nama_kegiatan")
    val namaKegiatan: String?,

    @SerializedName("tanggal_kegiatan")
    val tanggalKegiatan: String?,

    @SerializedName("waktu_kegiatan")
    val waktuKegiatan: String?,

    @SerializedName("lokasi")
    val lokasi: String?,

    @SerializedName("penceramah")
    val penceramah: String?,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("status_kegiatan")
    val statusKegiatan: String?,

    @SerializedName("foto_kegiatan")
    val fotoKegiatan: String?
)
