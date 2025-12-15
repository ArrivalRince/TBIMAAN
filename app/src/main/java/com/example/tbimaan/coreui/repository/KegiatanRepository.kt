package com.example.tbimaan.coreui.repository

import android.util.Log
import com.example.tbimaan.network.ApiClient
import com.example.tbimaan.network.KegiatanDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KegiatanRepository {
    private val TAG = "KegiatanRepository"

    fun getKegiatan(onResult: (List<KegiatanDto>?) -> Unit) {
        ApiClient.instance.getKegiatan().enqueue(object : Callback<List<KegiatanDto>> {
            override fun onResponse(call: Call<List<KegiatanDto>>, response: Response<List<KegiatanDto>>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    val err = response.errorBody()?.string()
                    Log.e(TAG, "getKegiatan error: ${response.code()} - $err")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<List<KegiatanDto>>, t: Throwable) {
                Log.e(TAG, "getKegiatan failure: ${t.message}")
                onResult(null)
            }
        })
    }

    fun getKegiatanById(id: String, onResult: (KegiatanDto?) -> Unit) {
        ApiClient.instance.getKegiatanById(id).enqueue(object : Callback<KegiatanDto> {
            override fun onResponse(call: Call<KegiatanDto>, response: Response<KegiatanDto>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    val err = response.errorBody()?.string()
                    Log.e(TAG, "getKegiatanById error: ${response.code()} - $err")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<KegiatanDto>, t: Throwable) {
                Log.e(TAG, "getKegiatanById failure: ${t.message}")
                onResult(null)
            }
        })
    }

    fun createKegiatan(kegiatan: KegiatanDto, onResult: (isSuccess: Boolean, message: String) -> Unit) {
        ApiClient.instance.createKegiatan(kegiatan).enqueue(object : Callback<KegiatanDto> {
            override fun onResponse(call: Call<KegiatanDto>, response: Response<KegiatanDto>) {
                if (response.isSuccessful) {
                    onResult(true, "Kegiatan berhasil disimpan")
                } else {
                    val err = response.errorBody()?.string()
                    Log.e(TAG, "createKegiatan failed: ${response.code()} - $err")
                    onResult(false, "Gagal menyimpan (Error: ${response.code()})")
                }
            }

            override fun onFailure(call: Call<KegiatanDto>, t: Throwable) {
                Log.e(TAG, "createKegiatan failure: ${t.message}")
                onResult(false, "Koneksi gagal: ${t.message}")
            }
        })
    }

    fun updateKegiatan(id: String, kegiatan: KegiatanDto, onResult: (isSuccess: Boolean, message: String) -> Unit) {
        ApiClient.instance.updateKegiatan(id, kegiatan).enqueue(object : Callback<KegiatanDto> {
            override fun onResponse(call: Call<KegiatanDto>, response: Response<KegiatanDto>) {
                if (response.isSuccessful) {
                    onResult(true, "Kegiatan berhasil diperbarui")
                } else {
                    val err = response.errorBody()?.string()
                    Log.e(TAG, "updateKegiatan failed: ${response.code()} - $err")
                    onResult(false, "Gagal memperbarui (Error: ${response.code()})")
                }
            }

            override fun onFailure(call: Call<KegiatanDto>, t: Throwable) {
                Log.e(TAG, "updateKegiatan failure: ${t.message}")
                onResult(false, "Koneksi gagal: ${t.message}")
            }
        })
    }

    fun deleteKegiatan(id: String, onResult: (isSuccess: Boolean, message: String) -> Unit) {
        ApiClient.instance.deleteKegiatan(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onResult(true, "Kegiatan berhasil dihapus")
                } else {
                    val err = response.errorBody()?.string()
                    Log.e(TAG, "deleteKegiatan failed: ${response.code()} - $err")
                    onResult(false, "Gagal menghapus (Error: ${response.code()})")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "deleteKegiatan failure: ${t.message}")
                onResult(false, "Koneksi gagal: ${t.message}")
            }
        })
    }
}
