package com.example.tbimaan.network

import com.example.tbimaan.model.InventarisResponse
import com.example.tbimaan.model.KeuanganResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

/** ---------------------------
 *  AUTH MODELS
 *  -------------------------- */
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

/** ---------------------------
 *  KEGIATAN MODELS
 *  -------------------------- */
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

/**
 * Interface Retrofit yang mendefinisikan semua endpoint API.
 */
interface ApiService {

    // ========== ENDPOINT AUTENTIKASI ==========
    @POST("api/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    // ========== ENDPOINT KEUANGAN ==========
    @GET("api/keuangan")
    fun getKeuangan(): Call<List<KeuanganResponse>>

    @GET("api/keuangan/{id}")
    fun getKeuanganById(@Path("id") id: String): Call<KeuanganResponse>

    @Multipart
    @POST("api/keuangan")
    fun createKeuangan(
        @Part("id_user") id_user: RequestBody,
        @Part("keterangan") keterangan: RequestBody,
        @Part("tipe_transaksi") tipe_transaksi: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("jumlah") jumlah: RequestBody,
        @Part bukti_transaksi: MultipartBody.Part
    ): Call<KeuanganResponse>

    @Multipart
    @PUT("api/keuangan/{id}")
    fun updateKeuangan(
        @Path("id") id: String,
        @Part("keterangan") keterangan: RequestBody,
        @Part("tipe_transaksi") tipe_transaksi: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("jumlah") jumlah: RequestBody,
        @Part bukti_transaksi: MultipartBody.Part? = null
    ): Call<KeuanganResponse>

    @DELETE("api/keuangan/{id}")
    fun deleteKeuangan(@Path("id") id: String): Call<Void>

    // =================================================================
    // === PERBAIKAN ENDPOINT INVENTARIS: DISELARASKAN DENGAN EXPRESS ===
    // =================================================================

    // READ All Inventaris
    @GET("api/inventaris")
    fun getInventaris(): Call<List<InventarisResponse>>

    // READ Inventaris by ID
    @GET("api/inventaris/{id}")
    fun getInventarisById(@Path("id") id: String): Call<InventarisResponse>

    // CREATE Inventaris
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

    // DELETE Inventaris
    @DELETE("api/inventaris/{id}")
    fun deleteInventaris(@Path("id") id: String): Call<Void>

    // UPDATE Inventaris
    @Multipart
    @PUT("api/inventaris/{id}")
    fun updateInventaris(
        @Path("id") id: String,
        @Part("nama_barang") namaBarang: RequestBody,
        @Part("kondisi") kondisi: RequestBody,
        @Part("jumlah") jumlah: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part foto_barang: MultipartBody.Part?
    ): Call<InventarisResponse>

    // ========== ENDPOINT KEGIATAN (DITAMBAHKAN) ==========
    // Note: disesuaikan dengan base path 'api/kegiatan' agar konsisten dengan endpoint lain

    @GET("api/kegiatan")
    fun getKegiatan(): Call<List<KegiatanDto>>

    @GET("api/kegiatan/{id}")
    fun getKegiatanById(@Path("id") id: String): Call<KegiatanDto>

    // Jika backend menerima JSON biasa (tanpa file), gunakan @Body
    @POST("api/kegiatan")
    fun createKegiatan(@Body kegiatan: KegiatanDto): Call<KegiatanDto>

    @PUT("api/kegiatan/{id}")
    fun updateKegiatan(
        @Path("id") id: String,
        @Body kegiatan: KegiatanDto
    ): Call<KegiatanDto>

    @DELETE("api/kegiatan/{id}")
    fun deleteKegiatan(@Path("id") id: String): Call<Void>

    // -----------------------
    // Jika backend untuk kegiatan mendukung upload foto (multipart),
    // kamu bisa menambahkan overload multipart seperti ini (opsional):
    //
    // @Multipart
    // @POST("api/kegiatan")
    // fun createKegiatanMultipart(
    //     @Part("id_user") idUser: RequestBody,
    //     @Part("nama_kegiatan") namaKegiatan: RequestBody,
    //     @Part("tanggal_kegiatan") tanggal: RequestBody,
    //     @Part("waktu_kegiatan") waktu: RequestBody?,
    //     @Part("lokasi") lokasi: RequestBody?,
    //     @Part("penceramah") penceramah: RequestBody?,
    //     @Part("deskripsi") deskripsi: RequestBody?,
    //     @Part("status_kegiatan") status: RequestBody?,
    //     @Part foto_kegiatan: MultipartBody.Part?
    // ): Call<KegiatanDto>
    //
    // Sama untuk update jika ingin upload file.
}
