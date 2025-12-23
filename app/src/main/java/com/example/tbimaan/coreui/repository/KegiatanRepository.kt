package com.example.tbimaan.coreui.repository

import android.util.Log
import com.example.tbimaan.model.KegiatanResponse
import com.example.tbimaan.network.ApiClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KegiatanRepository {

    private val TAG = "KegiatanRepository"

    fun getKegiatan(
        idUser: Int,
        onResult: (List<KegiatanResponse>?) -> Unit
    ) {
        ApiClient.instance.getKegiatan(idUser)
            .enqueue(object : Callback<List<KegiatanResponse>> {

                override fun onResponse(
                    call: Call<List<KegiatanResponse>>,
                    response: Response<List<KegiatanResponse>>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "getKegiatan success for user $idUser")
                        onResult(response.body())
                    } else {
                        Log.e(
                            TAG,
                            "getKegiatan error ${response.code()} : ${response.errorBody()?.string()}"
                        )
                        onResult(null)
                    }
                }

                override fun onFailure(call: Call<List<KegiatanResponse>>, t: Throwable) {
                    Log.e(TAG, "getKegiatan failure: ${t.message}", t)
                    onResult(null)
                }
            })
    }

    fun getKegiatanById(
        id: String,
        onResult: (KegiatanResponse?) -> Unit
    ) {
        ApiClient.instance.getKegiatanById(id)
            .enqueue(object : Callback<KegiatanResponse> {

                override fun onResponse(
                    call: Call<KegiatanResponse>,
                    response: Response<KegiatanResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(response.body())
                    } else {
                        Log.e(
                            TAG,
                            "getKegiatanById error ${response.code()} : ${response.errorBody()?.string()}"
                        )
                        onResult(null)
                    }
                }

                override fun onFailure(call: Call<KegiatanResponse>, t: Throwable) {
                    Log.e(TAG, "getKegiatanById failure: ${t.message}", t)
                    onResult(null)
                }
            })
    }

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
        ).enqueue(object : Callback<KegiatanResponse> {

            override fun onResponse(
                call: Call<KegiatanResponse>,
                response: Response<KegiatanResponse>
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

            override fun onFailure(call: Call<KegiatanResponse>, t: Throwable) {
                Log.e(TAG, "createKegiatanMultipart FAILURE: ${t.message}", t)
                onResult(false, t.message ?: "Koneksi ke server gagal")
            }
        })
    }

    fun updateKegiatanMultipart(
        id: String,
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
        ApiClient.instance.updateKegiatanMultipart(
            id = id,
            idUser = idUser,
            namaKegiatan = namaKegiatan,
            tanggalKegiatan = tanggalKegiatan,
            lokasi = lokasi,
            penanggungjawab = penanggungjawab,
            deskripsi = deskripsi,
            statusKegiatan = statusKegiatan,
            foto_kegiatan = foto
        ).enqueue(object : Callback<KegiatanResponse> {

            override fun onResponse(
                call: Call<KegiatanResponse>,
                response: Response<KegiatanResponse>
            ) {
                if (response.isSuccessful) {
                    onResult(true, "Kegiatan berhasil diperbarui")
                } else {
                    onResult(false, response.errorBody()?.string() ?: "Gagal update kegiatan")
                }
            }

            override fun onFailure(call: Call<KegiatanResponse>, t: Throwable) {
                onResult(false, t.message ?: "Koneksi gagal")
            }
        })
    }


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
                        onResult(false, err ?: "Gagal menghapus kegiatan")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e(TAG, "deleteKegiatan failure: ${t.message}", t)
                    onResult(false, t.message ?: "Koneksi ke server gagal")
                }
            })
    }
}