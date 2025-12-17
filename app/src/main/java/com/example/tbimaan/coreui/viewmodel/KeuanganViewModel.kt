package com.example.tbimaan.coreui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbimaan.model.UserSession // <-- TAMBAHKAN BARIS INI
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


    // --- FUNGSI-FUNGSI CRUD ---

    fun loadData() {
        // 1. Ambil ID pengguna yang sedang login dari UserSession.
        val currentUserIdAny = UserSession.idUser // Tipe masih 'Any?'

        // 2. Validasi dan Konversi Tipe (Casting)
        val currentUserId = currentUserIdAny as? Int // <-- Lakukan konversi ke Int? (nullable Int)

        if (currentUserId == null) {
            _errorMessage.value = "Sesi pengguna tidak ditemukan. Silakan login kembali."
            Log.e(TAG, "loadData: User ID is null or not an Integer. Cannot fetch data.")
            _isLoading.value = false // Pastikan loading berhenti
            return
        }
        // Jika sedang loading, jangan panggil lagi
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            Log.d(TAG, "loadData: Starting to fetch data for user ID: $currentUserId")
// 3. Teruskan ID pengguna ke repository.
            repository.getKeuangan(currentUserId) { responseList ->
                try {
                    if (responseList != null) {
                        Log.d(TAG, "loadData: Received ${responseList.size} items for user ID $currentUserId")

                        val entries = responseList.mapNotNull { response ->
                            try {
                                response.toPemasukanEntry()
                            } catch (e: Exception) {
                                Log.e(TAG, "loadData: Error converting item ${response.idTransaksi}: ${e.message}")
                                null
                            }
                        }
                        _pemasukanList.value = entries.filter { it.tipeTransaksi.equals("pemasukan", ignoreCase = true) }
                        _pengeluaranList.value = entries.filter { it.tipeTransaksi.equals("pengeluaran", ignoreCase = true) }

                        Log.d(TAG, "loadData: Pemasukan=${_pemasukanList.value.size}, Pengeluaran=${_pengeluaranList.value.size}")
                    } else {
                        _errorMessage.value = "Gagal mengambil data dari server."
                        Log.e(TAG, "loadData: Response is NULL for user ID $currentUserId")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "loadData: Exception in callback - ${e.message}")
                    _errorMessage.value = "Error: ${e.message}"
                } finally {
                    // Pastikan loading selalu berhenti
                    _isLoading.value = false
                }
            }
        }
    }
    // ================== PERBAIKAN KRUSIAL: LOGGING DEBUG LENGKAP ==================
    /**
     * Mengambil SATU data keuangan dari server berdasarkan ID-nya.
     * Ini dipanggil saat user tekan tombol Edit di ReadKeuanganScreen
     */
    fun getKeuanganById(id: String) {
        Log.d(TAG, "getKeuanganById: Called with ID=$id")

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""

            try {
                // Memanggil fungsi repository yang benar untuk mengambil data dari jaringan
                repository.getKeuanganById(id) { response ->
                    try {
                        Log.d(TAG, "getKeuanganById: Callback received")
                        Log.d(TAG, "getKeuanganById: Response = $response")

                        if (response != null) {
                            Log.d(TAG, "getKeuanganById: Response is NOT null, converting to PemasukanEntry")
                            // Konversi data dari server ke format yang bisa dibaca UI
                            val entry = response.toPemasukanEntry()
                            _selectedItem.value = entry
                            Log.d(TAG, "getKeuanganById: Successfully set selectedItem = $entry")
                        } else {
                            Log.e(TAG, "getKeuanganById: Response is NULL for ID=$id")
                            _errorMessage.value = "Gagal memuat detail data untuk ID: $id."
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "getKeuanganById: Exception in callback - ${e.message}", e)
                        _errorMessage.value = "Error converting data: ${e.message}"
                    } finally {
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "getKeuanganById: Exception launching coroutine - ${e.message}", e)
                _errorMessage.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    // ==================================================================================

    // =======================================================================
// ===           PERBAIKAN UTAMA DAN FINAL ADA DI FUNGSI INI           ===
// =======================================================================
    fun createKeuangan(
        // idUser dihapus dari parameter, karena akan kita ambil dari UserSession
        keterangan: String,
        tipeTransaksi: String,
        tanggal: String,
        jumlah: String,
        buktiFile: File,
        onResult: (isSuccess: Boolean, message: String) -> Unit
    ) {
        // 1. Ambil ID Pengguna dari UserSession
        val currentUserId = UserSession.idUser as? Int
        if (currentUserId == null) {
            onResult(false, "Sesi pengguna tidak valid, tidak bisa menyimpan data.")
            Log.e(TAG, "createKeuangan: Gagal karena idUser null.")
            return
        }

        viewModelScope.launch {
            try {
                Log.d(TAG, "createKeuangan: Starting with keterangan=$keterangan for user ID=$currentUserId")

                // 2. Gunakan currentUserId yang sudah valid (sebagai String)
                val idUserBody = currentUserId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val keteranganBody = keterangan.toRequestBody("text/plain".toMediaTypeOrNull())
                val tipeTransaksiBody = tipeTransaksi.lowercase().toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalBody = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())
                val jumlahBody = jumlah.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestFile = buktiFile.asRequestBody("image/*".toMediaTypeOrNull())
                val buktiPart = MultipartBody.Part.createFormData("bukti_transaksi", buktiFile.name, requestFile)

                repository.createKeuangan(idUserBody, keteranganBody, tipeTransaksiBody, tanggalBody, jumlahBody, buktiPart) { isSuccess, message ->
                    Log.d(TAG, "createKeuangan: Result - isSuccess=$isSuccess, message=$message")
                    if (isSuccess) loadData() // Muat ulang data jika berhasil
                    onResult(isSuccess, message)
                }
            } catch (e: Exception) {
                Log.e(TAG, "createKeuangan: Exception - ${e.message}", e)
                onResult(false, "Terjadi kesalahan: ${e.message}")
            }
        }
    }
// =======================================================================

    fun updateKeuangan(
        id: String,
        keterangan: String,
        tipeTransaksi: String,
        tanggal: String,
        jumlah: String,
        buktiFile: File?,
        onResult: (isSuccess: Boolean, message: String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "updateKeuangan: Starting with id=$id, keterangan=$keterangan, hasFile=${buktiFile != null}")

                val keteranganBody = keterangan.toRequestBody("text/plain".toMediaTypeOrNull())
                val tipeTransaksiBody = tipeTransaksi.lowercase().toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalBody = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())
                val jumlahBody = jumlah.toRequestBody("text/plain".toMediaTypeOrNull())

                var buktiPart: MultipartBody.Part? = null
                if (buktiFile != null) {
                    val requestFile = buktiFile.asRequestBody("image/*".toMediaTypeOrNull())
                    buktiPart = MultipartBody.Part.createFormData("bukti_transaksi", buktiFile.name, requestFile)
                    Log.d(TAG, "updateKeuangan: File attached - ${buktiFile.name}")
                } else {
                    Log.d(TAG, "updateKeuangan: No new file, using existing bukti_transaksi")
                }

                repository.updateKeuangan(
                    id = id,
                    keterangan = keteranganBody,
                    tipeTransaksi = tipeTransaksiBody,
                    tanggal = tanggalBody,
                    jumlah = jumlahBody,
                    bukti = buktiPart,
                    onResult = { isSuccess, message ->
                        Log.d(TAG, "updateKeuangan: Result - isSuccess=$isSuccess, message=$message")
                        if (isSuccess) loadData()
                        onResult(isSuccess, message)
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "updateKeuangan: Exception - ${e.message}", e)
                onResult(false, "Terjadi kesalahan: ${e.message}")
            }
        }
    }

    fun deleteKeuangan(id: String) {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            Log.d(TAG, "deleteKeuangan: Starting delete with id=$id")

            repository.deleteKeuangan(id) { isSuccess, message ->
                Log.d(TAG, "deleteKeuangan: Result - isSuccess=$isSuccess, message=$message")
                if (isSuccess) {
                    loadData()
                } else {
                    _errorMessage.value = message
                    _isLoading.value = false
                }
            }
        }
    }

    fun clearSelectedItem() {
        Log.d(TAG, "clearSelectedItem: Clearing selectedItem")
        _selectedItem.value = null
    }

    /**
     * Fungsi helper untuk mengubah data dari server (KeuanganResponse)
     * menjadi data yang siap ditampilkan di UI (PemasukanEntry).
     *
     * ================== PERBAIKAN: HANDLING NULL VALUE LEBIH AMAN ==================
     */
    private fun KeuanganResponse.toPemasukanEntry(): PemasukanEntry {
        Log.d(TAG, "toPemasukanEntry: Converting KeuanganResponse - idTransaksi=${this.idTransaksi}, keterangan=${this.keterangan}")

        val outputDate = try {
            val tanggalStr = this.tanggal ?: ""
            if (tanggalStr.isEmpty()) {
                "Tanggal tidak tersedia"
            } else {
                // Coba parsing dari format "yyyy-MM-dd"
                val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = parser.parse(tanggalStr)
                // Format ulang ke "dd MMMM yyyy"
                val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
                formatter.format(date!!)
            }
        } catch (e: Exception) {
            Log.w(TAG, "toPemasukanEntry: Failed to parse date '${this.tanggal}' - ${e.message}")
            this.tanggal ?: "Tanggal tidak valid"
        }

        return PemasukanEntry(
            id = this.idTransaksi?.toString() ?: "0",
            keterangan = this.keterangan ?: "Tanpa Keterangan",
            jumlah = (this.jumlah?.toString()?.toDoubleOrNull() ?: 0.0),
            tanggal = outputDate,
            namaBukti = if (!this.buktiTransaksi.isNullOrEmpty()) "Lihat Bukti" else "Tidak Ada",
            tipeTransaksi = this.tipeTransaksi ?: "pemasukan",
            urlBukti = this.buktiTransaksi ?: ""
        ).also {
            Log.d(TAG, "toPemasukanEntry: Converted to PemasukanEntry - id=${it.id}, keterangan=${it.keterangan}")
        }
    }
    // ==================================================================================
}