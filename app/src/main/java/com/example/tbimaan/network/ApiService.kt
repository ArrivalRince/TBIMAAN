package com.example.tbimaan.network

import com.example.tbimaan.model.KeuanganResponse
import com.example.tbimaan.model.InventarisResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

// --- Data class untuk Autentikasi ---
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

    // CREATE Inventaris (PERBAIKAN FINAL)
    // Backend: router.post('/') -> /api/inventaris
    @Multipart
    @POST("api/inventaris") // PERBAIKAN: Dihapus "/create" agar sama seperti endpoint keuangan
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

    // UPDATE Inventaris (PERBAIKAN FINAL)
    // Backend: router.put('/:id') -> /api/inventaris/{id}
    @Multipart
    @PUT("api/inventaris/{id}") // PERBAIKAN: Dihapus "/update" agar sesuai rute backend
    fun updateInventaris(
        @Path("id") id: String,
        @Part("nama_barang") namaBarang: RequestBody,
        @Part("kondisi") kondisi: RequestBody,
        @Part("jumlah") jumlah: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part foto_barang: MultipartBody.Part?
    ): Call<InventarisResponse>
}