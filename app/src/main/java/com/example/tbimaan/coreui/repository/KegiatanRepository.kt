package com.example.tbimaan.coreui.repository

import android.util.Log
import com.example.tbimaan.network.ApiClient
import com.example.tbimaan.network.KegiatanDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KegiatanRepository {

    private val TAG = "KegiatanRepository"

    // =========================
    // GET ALL
    // =========================
    fun getKegiatan(onResult: (List<KegiatanDto>?) -> Unit) {
        ApiClient.instance.getKegiatan()
            .enqueue(object : Callback<List<KegiatanDto>> {

                override fun onResponse(
                    call: Call<List<KegiatanDto>>,
                    response: Response<List<KegiatanDto>>
                ) {
                    if (response.isSuccessful) {
                        onResult(response.body())
                    } else {
                        Log.e(TAG, "getKegiatan error ${response.code()} : ${response.errorBody()?.string()}")
                        onResult(null)
                    }
                }

                override fun onFailure(call: Call<List<KegiatanDto>>, t: Throwable) {
                    Log.e(TAG, "getKegiatan failure: ${t.message}")
                    onResult(null)
                }
            })
    }

    // =========================
    // GET BY ID
    // =========================
    fun getKegiatanById(id: String, onResult: (KegiatanDto?) -> Unit) {
        ApiClient.instance.getKegiatanById(id)
            .enqueue(object : Callback<KegiatanDto> {

                override fun onResponse(
                    call: Call<KegiatanDto>,
                    response: Response<KegiatanDto>
                ) {
                    if (response.isSuccessful) {
                        onResult(response.body())
                    } else {
                        Log.e(TAG, "getKegiatanById error ${response.code()} : ${response.errorBody()?.string()}")
                        onResult(null)
                    }
                }

                override fun onFailure(call: Call<KegiatanDto>, t: Throwable) {
                    Log.e(TAG, "getKegiatanById failure: ${t.message}")
                    onResult(null)
                }
            })
    }

    // =========================
    // CREATE (MULTIPART) ⭐ FIX UTAMA
    // =========================
    fun createKegiatanMultipart(
        idUser: RequestBody,
        namaKegiatan: RequestBody,
        tanggalKegiatan: RequestBody,
        lokasi: RequestBody?,
        penanggungjawab: RequestBody?,
        deskripsi: RequestBody?,
        statusKegiatan: RequestBody,
        foto: MultipartBody.Part?,
        onResult: (Boolean, String) -> Unit
    ) {
        ApiClient.instance.createKegiatanMultipart(
            idUser = idUser,
            namaKegiatan = namaKegiatan,
            tanggalKegiatan = tanggalKegiatan,
            lokasi = lokasi,
            penanggungjawab = penanggungjawab,
            deskripsi = deskripsi,
            statusKegiatan = statusKegiatan,
            foto_kegiatan = foto
        ).enqueue(object : Callback<KegiatanDto> {

            override fun onResponse(
                call: Call<KegiatanDto>,
                response: Response<KegiatanDto>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "createKegiatanMultipart SUCCESS")
                    onResult(true, "Kegiatan berhasil disimpan")
                } else {
                    val err = response.errorBody()?.string()
                    Log.e(TAG, "createKegiatanMultipart ERROR ${response.code()} : $err")
                    onResult(false, err ?: "Gagal menyimpan kegiatan")
                }
            }

            override fun onFailure(call: Call<KegiatanDto>, t: Throwable) {
                Log.e(TAG, "createKegiatanMultipart FAILURE: ${t.message}", t)
                onResult(false, t.message ?: "Koneksi gagal")
            }
        })
    }

    // =========================
    // UPDATE (JSON – opsional)
    // =========================
    fun updateKegiatan(
        id: String,
        kegiatan: KegiatanDto,
        onResult: (Boolean, String) -> Unit
    ) {
        ApiClient.instance.updateKegiatan(id, kegiatan)
            .enqueue(object : Callback<KegiatanDto> {

                override fun onResponse(
                    call: Call<KegiatanDto>,
                    response: Response<KegiatanDto>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, "Kegiatan berhasil diperbarui")
                    } else {
                        val err = response.errorBody()?.string()
                        Log.e(TAG, "updateKegiatan error ${response.code()} : $err")
                        onResult(false, err ?: "Gagal memperbarui")
                    }
                }

                override fun onFailure(call: Call<KegiatanDto>, t: Throwable) {
                    Log.e(TAG, "updateKegiatan failure: ${t.message}")
                    onResult(false, t.message ?: "Koneksi gagal")
                }
            })
    }

    // =========================
    // DELETE
    // =========================
    fun deleteKegiatan(
        id: String,
        onResult: (Boolean, String) -> Unit
    ) {
        ApiClient.instance.deleteKegiatan(id)
            .enqueue(object : Callback<Void> {

                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, "Kegiatan berhasil dihapus")
                    } else {
                        val err = response.errorBody()?.string()
                        Log.e(TAG, "deleteKegiatan error ${response.code()} : $err")
                        onResult(false, err ?: "Gagal menghapus")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e(TAG, "deleteKegiatan failure: ${t.message}")
                    onResult(false, t.message ?: "Koneksi gagal")
                }
            })
    }
}