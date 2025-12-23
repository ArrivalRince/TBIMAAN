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

    // read
    fun getInventaris(idUser: Int, onResult: (List<InventarisResponse>?) -> Unit) {
        Log.d("InventarisRepository", "Requesting getInventaris for user $idUser")
        ApiClient.instance.getInventaris(idUser)
            .enqueue(object : Callback<List<InventarisResponse>> {
                override fun onResponse(
                    call: Call<List<InventarisResponse>>,
                    response: Response<List<InventarisResponse>>
                ) {
                    if (response.isSuccessful) {
                        Log.d(
                            "InventarisRepository",
                            "getInventaris success: ${response.body()?.size} items"
                        )
                        onResult(response.body())
                    } else {
                        Log.e(
                            "InventarisRepository",
                            "getInventaris error: ${response.code()}"
                        )
                        onResult(null)
                    }
                }

                override fun onFailure(call: Call<List<InventarisResponse>>, t: Throwable) {
                    Log.e(
                        "InventarisRepository",
                        "getInventaris failure: ${t.message}"
                    )
                    onResult(null)
                }
            })
    }

    //read id
    fun getInventarisById(id: String, onResult: (InventarisResponse?) -> Unit) {
        Log.d("InventarisRepository", "Requesting getInventarisById with id: $id")
        ApiClient.instance.getInventarisById(id)
            .enqueue(object : Callback<InventarisResponse> {
                override fun onResponse(
                    call: Call<InventarisResponse>,
                    response: Response<InventarisResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d(
                            "InventarisRepository",
                            "getInventarisById success"
                        )
                        onResult(response.body())
                    } else {
                        Log.e(
                            "InventarisRepository",
                            "getInventarisById error: ${response.code()}"
                        )
                        onResult(null)
                    }
                }

                override fun onFailure(call: Call<InventarisResponse>, t: Throwable) {
                    Log.e(
                        "InventarisRepository",
                        "getInventarisById failure: ${t.message}"
                    )
                    onResult(null)
                }
            })
    }

    // create
    fun createInventaris(
        idUser: RequestBody,
        namaBarang: RequestBody,
        kondisi: RequestBody,
        jumlah: RequestBody,
        tanggal: RequestBody,
        foto: MultipartBody.Part,
        onResult: (isSuccess: Boolean, message: String) -> Unit
    ) {
        Log.d("InventarisRepository", "Creating inventaris")
        ApiClient.instance.createInventaris(
            idUser,
            namaBarang,
            kondisi,
            jumlah,
            tanggal,
            foto
        ).enqueue(object : Callback<InventarisResponse> {
            override fun onResponse(
                call: Call<InventarisResponse>,
                response: Response<InventarisResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("InventarisRepository", "createInventaris success")
                    onResult(true, "Data berhasil disimpan")
                } else {
                    Log.e(
                        "InventarisRepository",
                        "createInventaris error: ${response.code()}"
                    )
                    onResult(
                        false,
                        "Gagal menyimpan data (Error: ${response.code()})"
                    )
                }
            }

            override fun onFailure(call: Call<InventarisResponse>, t: Throwable) {
                Log.e(
                    "InventarisRepository",
                    "createInventaris failure: ${t.message}"
                )
                onResult(false, "Koneksi ke server gagal: ${t.message}")
            }
        })
    }

    // update
    fun updateInventaris(
        id: String,
        namaBarang: RequestBody,
        kondisi: RequestBody,
        jumlah: RequestBody,
        tanggal: RequestBody,
        foto: MultipartBody.Part?,
        onResult: (isSuccess: Boolean, message: String) -> Unit
    ) {
        Log.d("InventarisRepository", "Updating inventaris with id: $id")
        ApiClient.instance.updateInventaris(
            id,
            namaBarang,
            kondisi,
            jumlah,
            tanggal,
            foto
        ).enqueue(object : Callback<InventarisResponse> {
            override fun onResponse(
                call: Call<InventarisResponse>,
                response: Response<InventarisResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("InventarisRepository", "updateInventaris success")
                    onResult(true, "Data berhasil diperbarui")
                } else {
                    Log.e(
                        "InventarisRepository",
                        "updateInventaris error: ${response.code()}"
                    )
                    onResult(
                        false,
                        "Gagal memperbarui data (Error: ${response.code()})"
                    )
                }
            }

            override fun onFailure(call: Call<InventarisResponse>, t: Throwable) {
                Log.e(
                    "InventarisRepository",
                    "updateInventaris failure: ${t.message}"
                )
                onResult(false, "Koneksi ke server gagal: ${t.message}")
            }
        })
    }

    fun deleteInventaris(id: String, onResult: (isSuccess: Boolean, message: String) -> Unit) {
        Log.d("InventarisRepository", "Deleting inventaris with id: $id")
        ApiClient.instance.deleteInventaris(id)
            .enqueue(object : Callback<InventarisResponse> {
                override fun onResponse(
                    call: Call<InventarisResponse>,
                    response: Response<InventarisResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("InventarisRepository", "deleteInventaris success")
                        onResult(true, "Data berhasil dihapus")
                    } else {
                        Log.e(
                            "InventarisRepository",
                            "deleteInventaris error: ${response.code()}"
                        )
                        onResult(false, "Gagal menghapus data (Error: ${response.code()})")
                    }
                }

                override fun onFailure(call: Call<InventarisResponse>, t: Throwable) {
                    Log.e(
                        "InventarisRepository",
                        "deleteInventaris failure: ${t.message}"
                    )
                    onResult(false, "Koneksi ke server gagal: ${t.message}")
                }
            })
    }
}
