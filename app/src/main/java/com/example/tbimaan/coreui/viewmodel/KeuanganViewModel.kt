package com.example.tbimaan.coreui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbimaan.coreui.repository.KeuanganRepository
import com.example.tbimaan.coreui.screen.Keuangan.PemasukanEntry
import com.example.tbimaan.model.KeuanganResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class KeuanganViewModel : ViewModel() {

    private val repository = KeuanganRepository()
    private val TAG = "KeuanganViewModel"

    private val _pemasukanList = mutableStateOf<List<PemasukanEntry>>(emptyList())
    val pemasukanList: State<List<PemasukanEntry>> = _pemasukanList

    private val _pengeluaranList = mutableStateOf<List<PemasukanEntry>>(emptyList())
    val pengeluaranList: State<List<PemasukanEntry>> = _pengeluaranList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    private val _selectedItem = mutableStateOf<PemasukanEntry?>(null)
    val selectedItem: State<PemasukanEntry?> = _selectedItem

    /**
     * Memuat SEMUA data keuangan untuk pengguna yang sedang login.
     * PERBAIKAN: Sekarang menerima `currentUserId` sebagai parameter dari UI.
     */
    fun loadData(currentUserId: Int?) {
        // 1. Validasi ID Pengguna
        if (currentUserId == null) {
            _errorMessage.value = "Sesi pengguna tidak valid. Silakan login kembali."
            Log.e(TAG, "loadData: Gagal karena idUser null.")
            _isLoading.value = false
            return
        }

        if (_isLoading.value && _pemasukanList.value.isEmpty() && _pengeluaranList.value.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            Log.d(TAG, "loadData: Memulai ambil data untuk User ID: $currentUserId")

            // 2. Teruskan ID yang sudah valid ke repository
            repository.getKeuangan(currentUserId) { responseList ->
                try {
                    if (responseList != null) {
                        val entries = responseList.mapNotNull { it.toPemasukanEntry() }
                        _pemasukanList.value = entries.filter { it.tipeTransaksi.equals("pemasukan", ignoreCase = true) }
                        _pengeluaranList.value = entries.filter { it.tipeTransaksi.equals("pengeluaran", ignoreCase = true) }
                    } else {
                        _errorMessage.value = "Gagal mengambil data dari server."
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * Membuat data keuangan baru.
     * PERBAIKAN: Sekarang menerima `currentUserId` sebagai parameter dari UI.
     */
    fun createKeuangan(
        currentUserId: Int?,
        keterangan: String,
        tipeTransaksi: String,
        tanggal: String,
        jumlah: String,
        buktiFile: File,
        onResult: (isSuccess: Boolean, message: String) -> Unit
    ) {
        // 1. Validasi ID Pengguna
        if (currentUserId == null) {
            onResult(false, "Sesi pengguna tidak valid, tidak bisa menyimpan data.")
            Log.e(TAG, "createKeuangan: Gagal karena idUser null.")
            return
        }

        viewModelScope.launch {
            try {
                // 2. Gunakan ID yang sudah valid untuk membuat RequestBody
                val idUserBody = currentUserId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val keteranganBody = keterangan.toRequestBody("text/plain".toMediaTypeOrNull())
                val tipeTransaksiBody = tipeTransaksi.lowercase().toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalBody = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())
                val jumlahBody = jumlah.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestFile = buktiFile.asRequestBody("image/*".toMediaTypeOrNull())
                val buktiPart = MultipartBody.Part.createFormData("bukti_transaksi", buktiFile.name, requestFile)

                repository.createKeuangan(idUserBody, keteranganBody, tipeTransaksiBody, tanggalBody, jumlahBody, buktiPart) { isSuccess, message ->
                    if (isSuccess) loadData(currentUserId) // Muat ulang data untuk user ini jika berhasil
                    onResult(isSuccess, message)
                }
            } catch (e: Exception) {
                onResult(false, "Terjadi kesalahan: ${e.message}")
            }
        }
    }

    /**
     * Memperbarui data keuangan yang sudah ada.
     * PERBAIKAN: Sekarang menerima `currentUserId` sebagai parameter dari UI untuk validasi.
     */
    fun updateKeuangan(
        id: String,
        currentUserId: Int?,
        keterangan: String,
        tipeTransaksi: String,
        tanggal: String,
        jumlah: String,
        buktiFile: File?,
        onResult: (isSuccess: Boolean, message: String) -> Unit
    ) {
        // 1. Validasi ID Pengguna
        if (currentUserId == null) {
            onResult(false, "Sesi pengguna tidak valid, tidak bisa memperbarui data.")
            Log.e(TAG, "updateKeuangan: Gagal karena idUser null.")
            return
        }

        viewModelScope.launch {
            try {
                Log.d(TAG, "updateKeuangan: User $currentUserId is updating item $id")

                val keteranganBody = keterangan.toRequestBody("text/plain".toMediaTypeOrNull())
                val tipeTransaksiBody = tipeTransaksi.lowercase().toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalBody = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())
                val jumlahBody = jumlah.toRequestBody("text/plain".toMediaTypeOrNull())
                var buktiPart: MultipartBody.Part? = null
                if (buktiFile != null) {
                    val requestFile = buktiFile.asRequestBody("image/*".toMediaTypeOrNull())
                    buktiPart = MultipartBody.Part.createFormData("bukti_transaksi", buktiFile.name, requestFile)
                }

                repository.updateKeuangan(id, keteranganBody, tipeTransaksiBody, tanggalBody, jumlahBody, buktiPart) { isSuccess, message ->
                    if (isSuccess) loadData(currentUserId)
                    onResult(isSuccess, message)
                }
            } catch (e: Exception) {
                onResult(false, "Terjadi kesalahan: ${e.message}")
            }
        }
    }

    /**
     * Menghapus data keuangan.
     * PERBAIKAN: Memuat ulang data dengan `idUser` yang benar setelah berhasil.
     */
    fun deleteKeuangan(id: String, currentUserId: Int?) {
        if (currentUserId == null) {
            _errorMessage.value = "Sesi pengguna tidak valid, tidak bisa menghapus data."
            return
        }
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            repository.deleteKeuangan(id) { isSuccess, message ->
                if (isSuccess) {
                    loadData(currentUserId)
                } else {
                    _errorMessage.value = message
                    _isLoading.value = false
                }
            }
        }
    }

    fun getKeuanganById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getKeuanganById(id) { response ->
                if (response != null) {
                    _selectedItem.value = response.toPemasukanEntry()
                } else {
                    _errorMessage.value = "Gagal memuat detail data untuk ID: $id."
                }
                _isLoading.value = false
            }
        }
    }

    fun clearSelectedItem() {
        _selectedItem.value = null
    }

    private fun KeuanganResponse.toPemasukanEntry(): PemasukanEntry? {
        val id = this.idTransaksi ?: return null
        return PemasukanEntry(
            id = id.toString(),
            keterangan = this.keterangan ?: "Tidak ada keterangan",
            jumlah = (this.jumlah ?: 0).toDouble(),
            tanggal = try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val date = parser.parse(this.tanggal ?: "")
                val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
                formatter.format(date!!)
            } catch (e: Exception) {
                this.tanggal?.substringBefore("T") ?: "Tanggal Invalid"
            },
            tipeTransaksi = this.tipeTransaksi ?: "lainnya",
            urlBukti = this.buktiTransaksi ?: ""
        )
    }
}
