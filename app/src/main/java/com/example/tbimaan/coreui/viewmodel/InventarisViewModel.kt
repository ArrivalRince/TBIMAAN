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


data class InventarisEntry(
    val id: String,
    val namaBarang: String,
    val kondisi: String,
    val jumlah: String,
    val tanggal: String,
    val urlFoto: String // Properti ini akan berisi URL LENGKAP
)

class InventarisViewModel : ViewModel() {
    private val repository = InventarisRepository()
    private val TAG = "InventarisViewModel"

    // State untuk UI (tidak ada perubahan)
    private val _inventarisList = mutableStateOf<List<InventarisEntry>>(emptyList())
    val inventarisList: State<List<InventarisEntry>> = _inventarisList

    private val _selectedItem = mutableStateOf<InventarisEntry?>(null)
    val selectedItem: State<InventarisEntry?> = _selectedItem

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    // --- FUNGSI-FUNGSI UTAMA ---

    fun loadInventaris() {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            repository.getInventaris { responseList ->
                if (responseList != null) {
                    // Mengubah setiap item dari server menjadi format yang siap ditampilkan
                    _inventarisList.value = responseList.mapNotNull { it.toInventarisEntry() }
                    Log.d(TAG, "loadInventaris: Success, loaded ${_inventarisList.value.size} items.")
                } else {
                    _errorMessage.value = "Gagal mengambil data inventaris."
                    Log.e(TAG, "loadInventaris: Failed, responseList is null.")
                }
                _isLoading.value = false
            }
        }
    }

    // Fungsi get, create, update, delete lainnya (logikanya sudah benar, tidak perlu diubah)
    fun getInventarisById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getInventarisById(id) { response ->
                _selectedItem.value = response?.toInventarisEntry()
                _isLoading.value = false
                if (response == null) _errorMessage.value = "Gagal memuat detail item."
            }
        }
    }

    fun createInventaris(idUser: String, namaBarang: String, kondisi: String, jumlah: String, tanggal: String, fotoFile: File, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val tanggalClean = if (tanggal.length >= 10) tanggal.substring(0, 10) else tanggal
                val idUserBody = idUser.toRequestBody("text/plain".toMediaTypeOrNull())
                val namaBarangBody = namaBarang.toRequestBody("text/plain".toMediaTypeOrNull())
                val kondisiBody = kondisi.toRequestBody("text/plain".toMediaTypeOrNull())
                val jumlahBody = jumlah.toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalBody = tanggalClean.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestFile = fotoFile.asRequestBody("image/*".toMediaTypeOrNull())
                val fotoPart = MultipartBody.Part.createFormData("foto_barang", fotoFile.name, requestFile)

                repository.createInventaris(idUserBody, namaBarangBody, kondisiBody, jumlahBody, tanggalBody, fotoPart) { isSuccess, message ->
                    if (isSuccess) loadInventaris() // Muat ulang data jika berhasil
                    onResult(isSuccess, message)
                }
            } catch (e: Exception) {
                Log.e(TAG, "createInventaris exception: ${e.message}")
                onResult(false, "Terjadi error: ${e.message}")
            }
        }
    }

    fun updateInventaris(id: String, namaBarang: String, kondisi: String, jumlah: String, tanggal: String, fotoFile: File?, onResult: (Boolean, String) -> Unit) {
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

                repository.updateInventaris(id, namaBarangBody, kondisiBody, jumlahBody, tanggalBody, fotoPart) { isSuccess, message ->
                    if (isSuccess) loadInventaris() // Muat ulang data jika berhasil
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
                    loadInventaris() // Muat ulang data jika berhasil
                } else {
                    _errorMessage.value = message
                }
                _isLoading.value = false
                onResult(isSuccess, message)
            }
        }
    }

    fun clearSelectedItem() { _selectedItem.value = null }


    private fun InventarisResponse.toInventarisEntry(): InventarisEntry? {
        // Jika ID dari server null, jangan proses item ini (menjaga integritas data)
        if (this.idInventaris == null) {
            Log.w(TAG, "toInventarisEntry: Skipping item because idInventaris is null.")
            return null
        }

        // PERBAIKAN A: Parsing tanggal dengan benar dari format backend (UTC)
        val outputDate = try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC") // Penting: beri tahu parser bahwa tanggal sumber adalah UTC
            val date = parser.parse(this.tanggal ?: "")
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
            formatter.format(date!!)
        } catch (e: Exception) {
            // Fallback jika format tanggal tidak sesuai (misal: hanya "yyyy-MM-dd")
            this.tanggal?.substringBefore("T") ?: "Tanggal Invalid"
        }

        // PERBAIKAN B (PALING PENTING): Membuat URL gambar yang lengkap
        val fullUrlFoto = if (this.fotoBarang.isNullOrBlank()) {
            "" // Jika backend tidak mengirim nama file, URL-nya kosong
        } else {
            // Gabungkan alamat server (BASE_URL) dengan path gambar dari backend
            val baseUrl = ApiClient.BASE_URL.removeSuffix("/") // Hapus '/' di akhir jika ada, agar tidak jadi '...//uploads/...'
            val path = if (this.fotoBarang.startsWith("/")) this.fotoBarang else "/${this.fotoBarang}" // Pastikan path diawali '/'
            "$baseUrl$path" // Hasil: "http://192.168.1.39:5000/uploads/namafile.jpg"
        }

        Log.d(TAG, "toInventarisEntry: Nama: ${this.namaBarang}, Converted URL: $fullUrlFoto")

        // Kembalikan objek yang siap ditampilkan UI
        return InventarisEntry(
            id = this.idInventaris.toString(),
            namaBarang = this.namaBarang ?: "Tanpa Nama",
            kondisi = this.kondisi ?: "Tidak Diketahui",
            jumlah = this.jumlah?.toString() ?: "0",
            tanggal = outputDate,
            urlFoto = fullUrlFoto // <- Sekarang berisi URL yang lengkap dan benar
        )
    }
}
