package com.example.tbimaan.coreui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbimaan.coreui.repository.InventarisRepository
import com.example.tbimaan.coreui.screen.Inventaris.InventarisEntry
import com.example.tbimaan.model.InventarisResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat

import java.util.*

// Data class ini khusus untuk UI

class InventarisViewModel : ViewModel() {
    private val repository = InventarisRepository()
    private val TAG = "InventarisViewModel"

    private val _inventarisList = mutableStateOf<List<InventarisEntry>>(emptyList())
    val inventarisList: State<List<InventarisEntry>> = _inventarisList

    private val _selectedItem = mutableStateOf<InventarisEntry?>(null)
    val selectedItem: State<InventarisEntry?> = _selectedItem

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    fun loadInventaris() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            repository.getInventaris { responseList ->
                if (responseList != null) {
                    _inventarisList.value = responseList.mapNotNull { it.toInventarisEntry() }
                } else {
                    _errorMessage.value = "Gagal mengambil data inventaris."
                }
                _isLoading.value = false
            }
        }
    }

    fun getInventarisById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getInventarisById(id) { response ->
                _selectedItem.value = response?.toInventarisEntry()
                _isLoading.value = false
                if (response == null) {
                    _errorMessage.value = "Gagal memuat detail item."
                }
            }
        }
    }

    fun createInventaris(
        idUser: String, namaBarang: String, kondisi: String,
        jumlah: String, tanggal: String, fotoFile: File,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // pastikan format tanggal menjadi YYYY-MM-DD (ambil 10 char)
                val tanggalClean = if (tanggal.length >= 10) tanggal.substring(0, 10) else tanggal

                val idUserBody = idUser.toRequestBody("text/plain".toMediaTypeOrNull())
                val namaBarangBody = namaBarang.toRequestBody("text/plain".toMediaTypeOrNull())
                val kondisiBody = kondisi.toRequestBody("text/plain".toMediaTypeOrNull())
                val jumlahBody = jumlah.toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalBody = tanggalClean.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestFile = fotoFile.asRequestBody("image/*".toMediaTypeOrNull())
                val fotoPart = MultipartBody.Part.createFormData("foto_barang", fotoFile.name, requestFile)

                Log.d(TAG, "createInventaris: idUser=$idUser, nama=$namaBarang, tanggal=$tanggalClean, jumlah=$jumlah")

                repository.createInventaris(idUserBody, namaBarangBody, kondisiBody, jumlahBody, tanggalBody, fotoPart) { isSuccess, message ->
                    if (isSuccess) loadInventaris()
                    onResult(isSuccess, message)
                }
            } catch (e: Exception) {
                Log.e(TAG, "createInventaris exception: ${e.message}")
                onResult(false, "Terjadi error: ${e.message}")
            }
        }
    }

    fun updateInventaris(
        id: String, namaBarang: String, kondisi: String,
        jumlah: String, tanggal: String, fotoFile: File?,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val tanggalClean = if (tanggal.length >= 10) tanggal.substring(0, 10) else tanggal

                val namaBarangBody = namaBarang.toRequestBody("text/plain".toMediaTypeOrNull())
                val kondisiBody = kondisi.toRequestBody("text/plain".toMediaTypeOrNull())
                val jumlahBody = jumlah.toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalBody = tanggalClean.toRequestBody("text/plain".toMediaTypeOrNull())
                var fotoPart: MultipartBody.Part? = null
                if (fotoFile != null) {
                    val requestFile = fotoFile.asRequestBody("image/*".toMediaTypeOrNull())
                    fotoPart = MultipartBody.Part.createFormData("foto_barang", fotoFile.name, requestFile)
                }

                Log.d(TAG, "updateInventaris: id=$id, nama=$namaBarang, tanggal=$tanggalClean, jumlah=$jumlah")

                repository.updateInventaris(id, namaBarangBody, kondisiBody, jumlahBody, tanggalBody, fotoPart) { isSuccess, message ->
                    if (isSuccess) loadInventaris()
                    onResult(isSuccess, message)
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateInventaris exception: ${e.message}")
                onResult(false, "Terjadi error: ${e.message}")
            }
        }
    }

    fun deleteInventaris(id: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteInventaris(id) { isSuccess, message ->
                if (isSuccess) {
                    loadInventaris()
                } else {
                    _errorMessage.value = message
                    _isLoading.value = false
                }
                onResult(isSuccess, message)
            }
        }
    }

    fun clearSelectedItem() {
        _selectedItem.value = null
    }

    private fun InventarisResponse.toInventarisEntry(): InventarisEntry? {
        val outputDate = try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(this.tanggal ?: "")
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
            formatter.format(date!!)
        } catch (e: Exception) {
            this.tanggal ?: "Tanggal Invalid"
        }

        return InventarisEntry(
            id = this.idInventaris?.toString() ?: return null,
            namaBarang = this.namaBarang ?: "Tanpa Nama",
            kondisi = this.kondisi ?: "Tidak Diketahui",
            jumlah = this.jumlah?.toString() ?: "0",
            tanggal = outputDate,
            urlFoto = this.fotoBarang ?: ""
        )
    }
}