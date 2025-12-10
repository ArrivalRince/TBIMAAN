package com.example.tbimaan.coreui.repository

import android.util.Log
import com.example.tbimaan.model.InventarisResponse
import com.example.tbimaan.network.ApiClient
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InventarisRepository {
    private val TAG = "InventarisRepository"

    fun getInventaris(onResult: (List<InventarisResponse>?) -> Unit) {
        ApiClient.instance.getInventaris().enqueue(object : Callback<List<InventarisResponse>> {
            override fun onResponse(call: Call<List<InventarisResponse>>, response: Response<List<InventarisResponse>>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    val err = response.errorBody()?.string()
                    Log.e(TAG, "getInventaris error: ${response.code()} - $err")
                    onResult(null)
                }
            }
            override fun onFailure(call: Call<List<InventarisResponse>>, t: Throwable) {
                Log.e(TAG, "getInventaris failure: ${t.message}")
                onResult(null)
            }
        })
    }

    fun getInventarisById(id: String, onResult: (InventarisResponse?) -> Unit) {
        ApiClient.instance.getInventarisById(id).enqueue(object : Callback<InventarisResponse> {
            override fun onResponse(call: Call<InventarisResponse>, response: Response<InventarisResponse>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    val err = response.errorBody()?.string()
                    Log.e(TAG, "getInventarisById error: ${response.code()} - $err")
                    onResult(null)
                }
            }
            override fun onFailure(call: Call<InventarisResponse>, t: Throwable) {
                Log.e(TAG, "getInventarisById failure: ${t.message}")
                onResult(null)
            }
        })
    }

    fun createInventaris(
        idUser: RequestBody, namaBarang: RequestBody, kondisi: RequestBody,
        jumlah: RequestBody, tanggal: RequestBody, fotoBarang: MultipartBody.Part,
        onResult: (isSuccess: Boolean, message: String) -> Unit
    ) {
        ApiClient.instance.createInventaris(idUser, namaBarang, kondisi, jumlah, tanggal, fotoBarang)
            .enqueue(object : Callback<InventarisResponse> {
                override fun onResponse(call: Call<InventarisResponse>, response: Response<InventarisResponse>) {
                    if (response.isSuccessful) {
                        onResult(true, "Data inventaris berhasil disimpan")
                    } else {
                        val err = response.errorBody()?.string()
                        Log.e(TAG, "createInventaris failed: ${response.code()} - $err")
                        onResult(false, "Gagal menyimpan (Error: ${response.code()})")
                    }
                }
                override fun onFailure(call: Call<InventarisResponse>, t: Throwable) {
                    Log.e(TAG, "createInventaris failure: ${t.message}")
                    onResult(false, "Koneksi gagal: ${t.message}")
                }
            })
    }

    fun updateInventaris(
        id: String, namaBarang: RequestBody, kondisi: RequestBody,
        jumlah: RequestBody, tanggal: RequestBody, fotoBarang: MultipartBody.Part?,
        onResult: (isSuccess: Boolean, message: String) -> Unit
    ) {
        ApiClient.instance.updateInventaris(id, namaBarang, kondisi, jumlah, tanggal, fotoBarang)
            .enqueue(object : Callback<InventarisResponse> {
                override fun onResponse(call: Call<InventarisResponse>, response: Response<InventarisResponse>) {
                    if (response.isSuccessful) {
                        onResult(true, "Data inventaris berhasil diperbarui")
                    } else {
                        val err = response.errorBody()?.string()
                        Log.e(TAG, "updateInventaris failed: ${response.code()} - $err")
                        onResult(false, "Gagal memperbarui (Error: ${response.code()})")
                    }
                }
                override fun onFailure(call: Call<InventarisResponse>, t: Throwable) {
                    Log.e(TAG, "updateInventaris failure: ${t.message}")
                    onResult(false, "Koneksi gagal: ${t.message}")
                }
            })
    }

    fun deleteInventaris(id: String, onResult: (isSuccess: Boolean, message: String) -> Unit) {
        ApiClient.instance.deleteInventaris(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onResult(true, "Data inventaris berhasil dihapus")
                } else {
                    val err = response.errorBody()?.string()
                    Log.e(TAG, "deleteInventaris failed: ${response.code()} - $err")
                    onResult(false, "Gagal menghapus (Error: ${response.code()})")
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "deleteInventaris failure: ${t.message}")
                onResult(false, "Koneksi gagal: ${t.message}")
            }
        })
    }
}
