package com.example.tbimaan.coreui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbimaan.coreui.repository.InventarisRepository
import com.example.tbimaan.model.InventarisResponse
import com.example.tbimaan.network.ApiClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// ===== MODEL UI =====
data class InventarisEntry(
    val id: String,
    val namaBarang: String,
    val kondisi: String,
    val jumlah: String,
    val tanggal: String,
    val urlFoto: String
)

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

    // ================= LOAD DATA =================
    fun loadInventaris(currentUserId: Int?) {
        if (currentUserId == null) {
            _errorMessage.value = "Sesi pengguna tidak valid. Silakan login ulang."
            Log.e(TAG, "loadInventaris: idUser null")
            return
        }

        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""

            Log.d(TAG, "loadInventaris: User ID $currentUserId")

            repository.getInventaris(currentUserId) { responseList ->
                try {
                    if (responseList != null) {
                        _inventarisList.value =
                            responseList.mapNotNull { it.toInventarisEntry() }
                    } else {
                        _errorMessage.value = "Gagal mengambil data inventaris"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    // ================= CREATE =================
    fun createInventaris(
        currentUserId: Int?,
        namaBarang: String,
        kondisi: String,
        jumlah: String,
        tanggal: String,
        fotoFile: File,
        onResult: (Boolean, String) -> Unit
    ) {
        if (currentUserId == null) {
            onResult(false, "Sesi pengguna tidak valid")
            return
        }

        viewModelScope.launch {
            try {
                val idUserBody =
                    currentUserId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val namaBody = namaBarang.toRequestBody("text/plain".toMediaTypeOrNull())
                val kondisiBody = kondisi.toRequestBody("text/plain".toMediaTypeOrNull())
                val jumlahBody = jumlah.toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalBody = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())

                val requestFile =
                    fotoFile.asRequestBody("image/*".toMediaTypeOrNull())
                val fotoPart = MultipartBody.Part.createFormData(
                    "foto_barang",
                    fotoFile.name,
                    requestFile
                )

                repository.createInventaris(
                    idUserBody,
                    namaBody,
                    kondisiBody,
                    jumlahBody,
                    tanggalBody,
                    fotoPart
                ) { isSuccess, message ->
                    if (isSuccess) loadInventaris(currentUserId)
                    onResult(isSuccess, message)
                }
            } catch (e: Exception) {
                onResult(false, "Terjadi kesalahan: ${e.message}")
            }
        }
    }

    // ================= UPDATE =================
    fun updateInventaris(
        id: String,
        currentUserId: Int?,
        namaBarang: String,
        kondisi: String,
        jumlah: String,
        tanggal: String,
        fotoFile: File?,
        onResult: (Boolean, String) -> Unit
    ) {
        if (currentUserId == null) {
            onResult(false, "Sesi pengguna tidak valid")
            return
        }

        viewModelScope.launch {
            try {
                val namaBody = namaBarang.toRequestBody("text/plain".toMediaTypeOrNull())
                val kondisiBody = kondisi.toRequestBody("text/plain".toMediaTypeOrNull())
                val jumlahBody = jumlah.toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalBody = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())

                var fotoPart: MultipartBody.Part? = null
                if (fotoFile != null) {
                    val requestFile =
                        fotoFile.asRequestBody("image/*".toMediaTypeOrNull())
                    fotoPart = MultipartBody.Part.createFormData(
                        "foto_barang",
                        fotoFile.name,
                        requestFile
                    )
                }

                repository.updateInventaris(
                    id,
                    namaBody,
                    kondisiBody,
                    jumlahBody,
                    tanggalBody,
                    fotoPart
                ) { isSuccess, message ->
                    if (isSuccess) loadInventaris(currentUserId)
                    onResult(isSuccess, message)
                }
            } catch (e: Exception) {
                onResult(false, "Terjadi kesalahan: ${e.message}")
            }
        }
    }

    // ================= DELETE =================
    fun deleteInventaris(id: String, currentUserId: Int?) {
        if (currentUserId == null) {
            _errorMessage.value = "Sesi pengguna tidak valid"
            return
        }

        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteInventaris(id) { isSuccess, message ->
                if (isSuccess) {
                    loadInventaris(currentUserId)
                } else {
                    _errorMessage.value = message
                    _isLoading.value = false
                }
            }
        }
    }

    // ================= GET BY ID =================
    fun getInventarisById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getInventarisById(id) { response ->
                _selectedItem.value = response?.toInventarisEntry()
                if (response == null) {
                    _errorMessage.value = "Gagal memuat detail inventaris"
                }
                _isLoading.value = false
            }
        }
    }

    fun clearSelectedItem() {
        _selectedItem.value = null
    }

    // ================= MAPPER =================
    private fun InventarisResponse.toInventarisEntry(): InventarisEntry? {
        val idInv = this.idInventaris ?: return null

        val tanggalFormatted = try {
            val parser =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(this.tanggal ?: "")
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
            formatter.format(date!!)
        } catch (e: Exception) {
            this.tanggal?.substringBefore("T") ?: "-"
        }

        val urlFoto = if (this.fotoBarang.isNullOrBlank()) {
            ""
        } else {
            val baseUrl = ApiClient.BASE_URL.removeSuffix("/")
            val path =
                if (this.fotoBarang.startsWith("/")) this.fotoBarang else "/${this.fotoBarang}"
            "$baseUrl$path"
        }

        return InventarisEntry(
            id = idInv.toString(),
            namaBarang = this.namaBarang ?: "-",
            kondisi = this.kondisi ?: "-",
            jumlah = this.jumlah?.toString() ?: "0",
            tanggal = tanggalFormatted,
            urlFoto = urlFoto
        )
    }
}
