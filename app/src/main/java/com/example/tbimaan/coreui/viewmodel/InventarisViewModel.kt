package com.example.tbimaan.coreui.viewmodel

import android.content.Context
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


data class InventarisEntry(
    val id: String,
    val namaBarang: String,
    val kondisi: String,
    val jumlah: String,
    val tanggal: String,
    val originalDate: Date?,
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


    fun loadInventaris(currentUserId: Int?, context: Context) {
        if (currentUserId == null) {
            _errorMessage.value = "Sesi pengguna tidak valid."
            return
        }
        if (_isLoading.value && _inventarisList.value.isEmpty()) return

        viewModelScope.launch {
            if (_inventarisList.value.isEmpty()) {
                _isLoading.value = true
            }
            _errorMessage.value = ""

            repository.getInventaris(currentUserId) { responseList ->
                try {
                    if (responseList != null) {
                        _inventarisList.value = responseList.mapNotNull { it.toInventarisEntry() }
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


    fun createInventaris(
        currentUserId: Int?,
        namaBarang: String,
        kondisi: String,
        jumlah: String,
        tanggal: String,
        fotoFile: File,
        context: Context,
        onResult: (Boolean, String) -> Unit
    ) {
        if (currentUserId == null) {
            onResult(false, "Sesi pengguna tidak valid")
            return
        }

        viewModelScope.launch {
            val idUserBody = currentUserId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val namaBody = namaBarang.toRequestBody("text/plain".toMediaTypeOrNull())
            val kondisiBody = kondisi.toRequestBody("text/plain".toMediaTypeOrNull())
            val jumlahBody = jumlah.toRequestBody("text/plain".toMediaTypeOrNull())
            val tanggalBody = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestFile = fotoFile.asRequestBody("image/*".toMediaTypeOrNull())
            val fotoPart = MultipartBody.Part.createFormData("foto_barang", fotoFile.name, requestFile)

            repository.createInventaris(idUserBody, namaBody, kondisiBody, jumlahBody, tanggalBody, fotoPart) { isSuccess, message ->
                if (isSuccess) loadInventaris(currentUserId, context)
                onResult(isSuccess, message)
            }
        }
    }


    fun updateInventaris(
        id: String,
        currentUserId: Int?,
        namaBarang: String,
        kondisi: String,
        jumlah: String,
        tanggal: String,
        fotoFile: File?,
        context: Context,
        onResult: (Boolean, String) -> Unit
    ) {
        if (currentUserId == null) {
            onResult(false, "Sesi pengguna tidak valid")
            return
        }
        viewModelScope.launch {
            val namaBody = namaBarang.toRequestBody("text/plain".toMediaTypeOrNull())
            val kondisiBody = kondisi.toRequestBody("text/plain".toMediaTypeOrNull())
            val jumlahBody = jumlah.toRequestBody("text/plain".toMediaTypeOrNull())
            val tanggalBody = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())
            var fotoPart: MultipartBody.Part? = null
            if (fotoFile != null) {
                val requestFile = fotoFile.asRequestBody("image/*".toMediaTypeOrNull())
                fotoPart = MultipartBody.Part.createFormData("foto_barang", fotoFile.name, requestFile)
            }

            repository.updateInventaris(id, namaBody, kondisiBody, jumlahBody, tanggalBody, fotoPart) { isSuccess, message ->
                if (isSuccess) loadInventaris(currentUserId, context) // <-- Kirim context
                onResult(isSuccess, message)
            }
        }
    }


    fun deleteInventaris(id: String, currentUserId: Int?, context: Context) {
        if (currentUserId == null) {
            _errorMessage.value = "Sesi pengguna tidak valid"
            return
        }
        val before = _inventarisList.value
        _inventarisList.value = before.filterNot { it.id == id }

        viewModelScope.launch {
            repository.deleteInventaris(id) { isSuccess, message ->
                if (isSuccess) {
                    loadInventaris(currentUserId, context) // <-- Kirim context
                } else {
                    _inventarisList.value = before
                    _errorMessage.value = message
                }
            }
        }
    }

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


    private fun InventarisResponse.toInventarisEntry(): InventarisEntry? {
        val idInv = this.idInventaris ?: return null


        val dateFormats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") },
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )

        var parsedDate: Date? = null
        for (format in dateFormats) {
            try {
                parsedDate = format.parse(this.tanggal ?: "")
                if (parsedDate != null) break //
            } catch (e: ParseException) {

            }
        }

        val tanggalUntukTampilan = if (parsedDate != null) {
            SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID")).format(parsedDate)
        } else {
            this.tanggal ?: "-"
        }

        val urlFoto = if (this.fotoBarang.isNullOrBlank()) ""
        else ApiClient.BASE_URL.removeSuffix("/") + if (this.fotoBarang.startsWith("/")) this.fotoBarang else "/${this.fotoBarang}"

        return InventarisEntry(
            id = idInv.toString(),
            namaBarang = this.namaBarang ?: "-",
            kondisi = this.kondisi ?: "-",
            jumlah = this.jumlah?.toString() ?: "0",
            tanggal = tanggalUntukTampilan,
            originalDate = parsedDate,
            urlFoto = urlFoto
        )
    }

}
