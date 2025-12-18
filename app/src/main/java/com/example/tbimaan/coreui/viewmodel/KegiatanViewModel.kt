package com.example.tbimaan.coreui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbimaan.coreui.Notification.KegiatanNotification
import com.example.tbimaan.coreui.repository.KegiatanRepository
import com.example.tbimaan.network.KegiatanDto
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Pastikan data class ini ada dan sesuai
data class KegiatanEntry(
    val id: String,
    val nama: String,
    val tanggal: String, // Format dd MMMM yyyy
    val waktu: String?,
    val lokasi: String?,
    val penanggungjawab: String?,
    val deskripsi: String?,
    val status: String?,
    val fotoUrl: String?,
    val originalDate: Date? // Tanggal asli untuk komparasi
)

class KegiatanViewModel : ViewModel() {
    private val repository = KegiatanRepository()
    private val TAG = "KegiatanViewModel"

    private val _kegiatanList = mutableStateOf<List<KegiatanEntry>>(emptyList())
    val kegiatanList: State<List<KegiatanEntry>> = _kegiatanList

    private val _selectedItem = mutableStateOf<KegiatanEntry?>(null)
    val selectedItem: State<KegiatanEntry?> = _selectedItem

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    // --- FUNGSI BARU UNTUK LOAD DATA & NOTIFIKASI ---
    fun loadKegiatan(context: Context) {
        if (_isLoading.value) return
        viewModelScope.launch {
            if (_kegiatanList.value.isEmpty()) _isLoading.value = true
            _errorMessage.value = ""
            repository.getKegiatan { responseList ->
                _isLoading.value = false
                if (responseList != null) {
                    _kegiatanList.value = responseList.mapNotNull { it.toKegiatanEntry() }
                    Log.d(TAG, "loadKegiatan: loaded ${_kegiatanList.value.size} items. Checking notifications.")
                    checkUpcomingEventsAndNotify(context) // Panggil pengecekan notifikasi
                } else {
                    _errorMessage.value = "Gagal mengambil data kegiatan."
                    Log.e(TAG, "loadKegiatan: response is null")
                }
            }
        }
    }

    // --- FUNGSI BARU UNTUK LOGIKA NOTIFIKASI H-1 ---
    private fun checkUpcomingEventsAndNotify(context: Context) {
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.time
        val tomorrowStartOfDay = getStartOfDay(tomorrow)

        val upcomingEvents = _kegiatanList.value.filter { kegiatan ->
            // Gunakan originalDate yang sudah pasti berformat Date
            kegiatan.originalDate != null && getStartOfDay(kegiatan.originalDate) == tomorrowStartOfDay &&
                    kegiatan.status.equals("Akan Datang", ignoreCase = true)
        }

        if (upcomingEvents.isNotEmpty()) {
            Log.d(TAG, "${upcomingEvents.size} kegiatan ditemukan untuk besok. MENAMPILKAN NOTIFIKASI.")
            KegiatanNotification.showEventReminderNotification(context, upcomingEvents)
        } else {
            Log.d(TAG, "Tidak ada kegiatan untuk besok. Membatalkan notifikasi jika ada.")
            KegiatanNotification.cancelEventReminderNotification(context)
        }
    }

    // Helper untuk membandingkan tanggal tanpa memperdulikan jam
    private fun getStartOfDay(date: Date): Date {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    // --- PERBAIKAN CREATE ---
    fun createKegiatanMultipart(
        idUser: String,
        nama: String,
        tanggal: String, // Format yyyy-MM-dd
        lokasi: String?,
        penanggungjawab: String?,
        deskripsi: String?,
        status: String,
        fotoFile: File?,
        context: Context, // <-- Tambahkan context
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                fun textPart(value: String?): RequestBody? = value?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())

                val idUserPart = idUser.toRequestBody("text/plain".toMediaTypeOrNull())
                val namaPart = nama.toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalPart = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())
                val statusPart = status.toRequestBody("text/plain".toMediaTypeOrNull())

                val fotoPart = fotoFile?.let {
                    MultipartBody.Part.createFormData("foto_kegiatan", it.name, it.asRequestBody("image/*".toMediaTypeOrNull()))
                }

                repository.createKegiatanMultipart(
                    idUser = idUserPart,
                    namaKegiatan = namaPart,
                    tanggalKegiatan = tanggalPart,
                    lokasi = textPart(lokasi),
                    penanggungjawab = textPart(penanggungjawab),
                    deskripsi = textPart(deskripsi),
                    statusKegiatan = statusPart,
                    foto = fotoPart
                ) { isSuccess, message ->
                    if (isSuccess) {
                        loadKegiatan(context) // <-- Panggil loadKegiatan di sini untuk refresh
                    }
                    onResult(isSuccess, message)
                }
            } catch (e: Exception) {
                Log.e(TAG, "createKegiatanMultipart error: ${e.message}")
                onResult(false, "Gagal menyimpan kegiatan: ${e.message}")
            }
        }
    }

    // --- PERBAIKAN DELETE ---
    fun deleteKegiatan(id: String, context: Context, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteKegiatan(id) { isSuccess, message ->
                if (isSuccess) {
                    loadKegiatan(context) // <-- Panggil loadKegiatan di sini untuk refresh
                } else {
                    _errorMessage.value = message
                }
                _isLoading.value = false
                onResult(isSuccess, message)
            }
        }
    }

    fun getKegiatanById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getKegiatanById(id) { dto ->
                _selectedItem.value = dto?.toKegiatanEntry()
                if (dto == null) _errorMessage.value = "Gagal memuat detail kegiatan."
                _isLoading.value = false
            }
        }
    }

    fun clearSelectedItem() { _selectedItem.value = null }

    // --- Helper konversi dari KegiatanDto ke KegiatanEntry untuk UI ---
    private fun KegiatanDto.toKegiatanEntry(): KegiatanEntry? {
        if (this.id_kegiatan == null) return null

        val dateFormats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") },
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )

        var parsedDate: Date? = null
        for (format in dateFormats) {
            try {
                parsedDate = format.parse(this.tanggal_kegiatan ?: "")
                if (parsedDate != null) break
            } catch (e: ParseException) { /* Lanjutkan */ }
        }

        val tanggalFormatted = parsedDate?.let { SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID")).format(it) } ?: this.tanggal_kegiatan ?: "Tanggal Invalid"

        return KegiatanEntry(
            id = this.id_kegiatan.toString(),
            nama = this.nama_kegiatan ?: "",
            tanggal = tanggalFormatted,
            waktu = this.waktu_kegiatan,
            lokasi = this.lokasi,
            penanggungjawab = this.penanggungjawab,
            deskripsi = this.deskripsi,
            status = this.status_kegiatan,
            fotoUrl = this.foto_kegiatan,
            originalDate = parsedDate // Simpan tanggal asli
        )
    }
}