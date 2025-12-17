package com.example.tbimaan.network

import com.example.tbimaan.model.InventarisResponse
import com.example.tbimaan.model.KeuanganResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

/* =====================================================
 * AUTH MODELS
 * ===================================================== */
data class RegisterRequest(
    val nama_masjid: String,
    val email: String,
    val password: String,
    val alamat: String
)

data class RegisterResponse(
    val message: String
)

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

/* =====================================================
 * KEGIATAN MODEL
 * ===================================================== */
data class KegiatanDto(
    val id_kegiatan: Int? = null,
    val id_user: Int,
    val nama_kegiatan: String,
    val tanggal_kegiatan: String,
    val waktu_kegiatan: String? = null,
    val lokasi: String? = null,
    val penceramah: String? = null,
    val deskripsi: String? = null,
    val status_kegiatan: String? = null,
    val foto_kegiatan: String? = null
)

/* =====================================================
 * API SERVICE
 * ===================================================== */
interface ApiService {

    /* =========================
     * AUTH
     * ========================= */
    @POST("api/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    /* =========================
     * KEUANGAN
     * ========================= */
    @GET("api/keuangan")
    fun getKeuangan(): Call<List<KeuanganResponse>>

    @GET("api/keuangan/{id}")
    fun getKeuanganById(@Path("id") id: String): Call<KeuanganResponse>

    @Multipart
    @POST("api/keuangan")
    fun createKeuangan(
        @Part("id_user") idUser: RequestBody,
        @Part("keterangan") keterangan: RequestBody,
        @Part("tipe_transaksi") tipeTransaksi: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("jumlah") jumlah: RequestBody,
        @Part bukti_transaksi: MultipartBody.Part
    ): Call<KeuanganResponse>

    @Multipart
    @PUT("api/keuangan/{id}")
    fun updateKeuangan(
        @Path("id") id: String,
        @Part("keterangan") keterangan: RequestBody,
        @Part("tipe_transaksi") tipeTransaksi: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("jumlah") jumlah: RequestBody,
        @Part bukti_transaksi: MultipartBody.Part? = null
    ): Call<KeuanganResponse>

    @DELETE("api/keuangan/{id}")
    fun deleteKeuangan(@Path("id") id: String): Call<Void>

    /* =========================
     * INVENTARIS
     * ========================= */
    @GET("api/inventaris")
    fun getInventaris(): Call<List<InventarisResponse>>

    @GET("api/inventaris/{id}")
    fun getInventarisById(@Path("id") id: String): Call<InventarisResponse>

    @Multipart
    @POST("api/inventaris")
    fun createInventaris(
        @Part("id_user") idUser: RequestBody,
        @Part("nama_barang") namaBarang: RequestBody,
        @Part("kondisi") kondisi: RequestBody,
        @Part("jumlah") jumlah: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part foto_barang: MultipartBody.Part
    ): Call<InventarisResponse>

    @Multipart
    @PUT("api/inventaris/{id}")
    fun updateInventaris(
        @Path("id") id: String,
        @Part("nama_barang") namaBarang: RequestBody,
        @Part("kondisi") kondisi: RequestBody,
        @Part("jumlah") jumlah: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part foto_barang: MultipartBody.Part? = null
    ): Call<InventarisResponse>

    @DELETE("api/inventaris/{id}")
    fun deleteInventaris(@Path("id") id: String): Call<Void>

    /* =========================
     * KEGIATAN (JSON)
     * ========================= */
    @GET("api/kegiatan")
    fun getKegiatan(): Call<List<KegiatanDto>>

    @GET("api/kegiatan/{id}")
    fun getKegiatanById(@Path("id") id: String): Call<KegiatanDto>

    @POST("api/kegiatan")
    fun createKegiatan(@Body kegiatan: KegiatanDto): Call<KegiatanDto>

    @PUT("api/kegiatan/{id}")
    fun updateKegiatan(
        @Path("id") id: String,
        @Body kegiatan: KegiatanDto
    ): Call<KegiatanDto>

    @DELETE("api/kegiatan/{id}")
    fun deleteKegiatan(@Path("id") id: String): Call<Void>

    /* =========================
     * KEGIATAN (MULTIPART)
     * ========================= */
    @Multipart
    @POST("api/kegiatan")
    fun createKegiatanMultipart(
        @Part("id_user") idUser: RequestBody,
        @Part("nama_kegiatan") namaKegiatan: RequestBody,
        @Part("tanggal_kegiatan") tanggalKegiatan: RequestBody,
        @Part("lokasi") lokasi: RequestBody? = null,
        @Part("penceramah") penceramah: RequestBody? = null,
        @Part("deskripsi") deskripsi: RequestBody? = null,
        @Part("status_kegiatan") statusKegiatan: RequestBody? = null,
        @Part foto_kegiatan: MultipartBody.Part? = null
    ): Call<KegiatanDto>

    // ========== ENDPOINT KEGIATAN (MULTIPART UPDATE) ==========
    @Multipart
    @PUT("api/kegiatan/{id}")
    fun updateKegiatanMultipart(
        @Path("id") id: String,

        @Part("id_user") idUser: RequestBody,
        @Part("nama_kegiatan") namaKegiatan: RequestBody,
        @Part("tanggal_kegiatan") tanggalKegiatan: RequestBody,
        @Part("lokasi") lokasi: RequestBody? = null,
        @Part("penceramah") penceramah: RequestBody? = null,
        @Part("deskripsi") deskripsi: RequestBody? = null,
        @Part("status_kegiatan") statusKegiatan: RequestBody? = null,

        @Part foto_kegiatan: MultipartBody.Part? = null
    ): Call<KegiatanDto>
}
