package com.example.tbimaan.coreui.repository

import android.util.Log
import com.example.tbimaan.model.KeuanganResponse
import com.example.tbimaan.network.ApiClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KeuanganRepository {


    fun getKeuangan(idUser: Int, onResult: (List<KeuanganResponse>?) -> Unit) {
        ApiClient.instance.getKeuangan(idUser).enqueue(object : Callback<List<KeuanganResponse>> {
            override fun onResponse(call: Call<List<KeuanganResponse>>, response: Response<List<KeuanganResponse>>) {
                if (response.isSuccessful) {
                    Log.d("KeuanganRepository", "getKeuangan success for user $idUser: ${response.body()?.size} items")
                    onResult(response.body())
                } else {
                    Log.e("KeuanganRepository", "getKeuangan error: ${response.code()}")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<List<KeuanganResponse>>, t: Throwable) {
                Log.e("KeuanganRepository", "getKeuangan failure: ${t.message}")
                onResult(null)
            }
        })
    }


    fun getKeuanganById(id: String, onResult: (KeuanganResponse?) -> Unit) {
        Log.d("KeuanganRepository", "Requesting getKeuanganById with id: $id")
        ApiClient.instance.getKeuanganById(id).enqueue(object : Callback<KeuanganResponse> {
            override fun onResponse(call: Call<KeuanganResponse>, response: Response<KeuanganResponse>) {
                if (response.isSuccessful) {
                    Log.d("KeuanganRepository", "getKeuanganById success: ${response.body()}")
                    onResult(response.body())
                } else {
                    Log.e("KeuanganRepository", "getKeuanganById error: ${response.code()} - ${response.message()}")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<KeuanganResponse>, t: Throwable) {
                Log.e("KeuanganRepository", "getKeuanganById failure: ${t.message}")
                onResult(null)
            }
        })
    }


    fun createKeuangan(
        idUser: RequestBody,
        keterangan: RequestBody,
        tipeTransaksi: RequestBody,
        tanggal: RequestBody,
        jumlah: RequestBody,
        bukti: MultipartBody.Part,
        onResult: (isSuccess: Boolean, message: String) -> Unit
    ) {
        ApiClient.instance.createKeuangan(idUser, keterangan, tipeTransaksi, tanggal, jumlah, bukti)
            .enqueue(object : Callback<KeuanganResponse> {
                override fun onResponse(call: Call<KeuanganResponse>, response: Response<KeuanganResponse>) {
                    if (response.isSuccessful) {
                        Log.d("KeuanganRepository", "createKeuangan success")
                        onResult(true, "Data berhasil disimpan")
                    } else {
                        Log.e("KeuanganRepository", "createKeuangan error: ${response.code()}")
                        onResult(false, "Gagal menyimpan data (Error: ${response.code()})")
                    }
                }

                override fun onFailure(call: Call<KeuanganResponse>, t: Throwable) {
                    Log.e("KeuanganRepository", "createKeuangan failure: ${t.message}")
                    onResult(false, "Koneksi ke server gagal: ${t.message}")
                }
            })
    }


    fun updateKeuangan(
        id: String,
        keterangan: RequestBody,
        tipeTransaksi: RequestBody,
        tanggal: RequestBody,
        jumlah: RequestBody,
        bukti: MultipartBody.Part?,
        onResult: (isSuccess: Boolean, message: String) -> Unit
    ) {
        Log.d("KeuanganRepository", "Updating keuangan with id: $id")
        ApiClient.instance.updateKeuangan(id, keterangan, tipeTransaksi, tanggal, jumlah, bukti)
            .enqueue(object : Callback<KeuanganResponse> {
                override fun onResponse(call: Call<KeuanganResponse>, response: Response<KeuanganResponse>) {
                    if (response.isSuccessful) {
                        Log.d("KeuanganRepository", "updateKeuangan success")
                        onResult(true, "Data berhasil diperbarui")
                    } else {
                        Log.e("KeuanganRepository", "updateKeuangan error: ${response.code()}")
                        onResult(false, "Gagal memperbarui data (Error: ${response.code()})")
                    }
                }

                override fun onFailure(call: Call<KeuanganResponse>, t: Throwable) {
                    Log.e("KeuanganRepository", "updateKeuangan failure: ${t.message}")
                    onResult(false, "Koneksi ke server gagal: ${t.message}")
                }
            })
    }


    fun deleteKeuangan(id: String, onResult: (isSuccess: Boolean, message: String) -> Unit) {
        Log.d("KeuanganRepository", "Deleting keuangan with id: $id")
        ApiClient.instance.deleteKeuangan(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("KeuanganRepository", "deleteKeuangan success")
                    onResult(true, "Data berhasil dihapus")
                } else {
                    Log.e("KeuanganRepository", "deleteKeuangan error: ${response.code()}")
                    onResult(false, "Gagal menghapus data (Error: ${response.code()})")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("KeuanganRepository", "deleteKeuangan failure: ${t.message}")
                onResult(false, "Koneksi ke server gagal: ${t.message}")
            }
        })
    }
}