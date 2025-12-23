package com.example.tbimaan.network

import com.example.tbimaan.model.InventarisResponse
import com.example.tbimaan.model.KegiatanResponse
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
import retrofit2.http.Query

data class RegisterRequest(
    val nama_masjid: String,
    val email: String,
    val password: String,
    val alamat: String
)

data class RegisterResponse(val message: String)

data class LoginRequest(val email: String, val password: String)

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

// =========================
// ✅ FCM MODELS - Match Backend API
// =========================
data class FcmTokenRequest(
    val id_user: Int,
    val fcm_token: String
)

data class FcmTokenResponse(
    val message: String,
    val id_user: Int
)



interface ApiService {


    @POST("api/user/fcm-token")
    fun updateFcmToken(@Body request: FcmTokenRequest): Call<FcmTokenResponse>

    // Hapus FCM token (dipanggil saat logout)
    @DELETE("api/user/fcm-token/{id}")
    fun deleteFcmToken(@Path("id") userId: Int): Call<Map<String, String>>


    @POST("api/auth/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/auth/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>


    @GET("api/keuangan")
    fun getKeuangan(@Query("id_user") idUser: Int): Call<List<KeuanganResponse>>

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


    @GET("api/inventaris")
    fun getInventaris(@Query("id_user") idUser: Int): Call<List<InventarisResponse>>

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

    @DELETE("api/inventaris/{id}")
    fun deleteInventaris(@Path("id") id: String): Call<InventarisResponse>

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



    @GET("api/kegiatan")
    fun getKegiatan(
        @Query("id_user") idUser: Int
    ): Call<List<KegiatanResponse>>

    @GET("api/kegiatan/{id}")
    fun getKegiatanById(
        @Path("id") id: String
    ): Call<KegiatanResponse>

    @POST("api/kegiatan")
    fun createKegiatan(
        @Body kegiatan: KegiatanResponse
    ): Call<KegiatanResponse>

    @PUT("api/kegiatan/{id}")
    fun updateKegiatan(
        @Path("id") id: String,
        @Body kegiatan: KegiatanResponse
    ): Call<KegiatanResponse>

    @DELETE("api/kegiatan/{id}")
    fun deleteKegiatan(
        @Path("id") id: String
    ): Call<Void>


    @Multipart
    @POST("api/kegiatan")
    fun createKegiatanMultipart(
        @Part("id_user") idUser: RequestBody,
        @Part("nama_kegiatan") namaKegiatan: RequestBody,
        @Part("tanggal_kegiatan") tanggalKegiatan: RequestBody,
        @Part("lokasi") lokasi: RequestBody? = null,
        @Part("penanggungjawab") penanggungjawab: RequestBody? = null,
        @Part("deskripsi") deskripsi: RequestBody? = null,
        @Part("status_kegiatan") statusKegiatan: RequestBody? = null,
        @Part foto_kegiatan: MultipartBody.Part? = null
    ): Call<KegiatanResponse>


    @Multipart
    @PUT("api/kegiatan/{id}")
    fun updateKegiatanMultipart(
        @Path("id") id: String,
        @Part("id_user") idUser: RequestBody,
        @Part("nama_kegiatan") namaKegiatan: RequestBody,
        @Part("tanggal_kegiatan") tanggalKegiatan: RequestBody,
        @Part("lokasi") lokasi: RequestBody? = null,
        @Part("penanggungjawab") penanggungjawab: RequestBody? = null,
        @Part("deskripsi") deskripsi: RequestBody? = null,
        @Part("status_kegiatan") statusKegiatan: RequestBody? = null,
        @Part foto_kegiatan: MultipartBody.Part? = null
    ): Call<KegiatanResponse>
}