package com.example.tbimaan.coreui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbimaan.coreui.repository.KegiatanRepository
import com.example.tbimaan.network.KegiatanDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class KegiatanEntry(
    val id: String,
    val nama: String,
    val tanggal: String,
    val waktu: String?,
    val lokasi: String?,
    val penanggungjawab: String?,
    val deskripsi: String?,
    val status: String?,
    val fotoUrl: String?
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

    // --- LOAD ALL ---
    fun loadKegiatan() {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            repository.getKegiatan { responseList ->
                if (responseList != null) {
                    _kegiatanList.value = responseList.mapNotNull { it.toKegiatanEntry() }
                    Log.d(TAG, "loadKegiatan: loaded ${_kegiatanList.value.size}")
                } else {
                    _errorMessage.value = "Gagal mengambil data kegiatan."
                    Log.e(TAG, "loadKegiatan: response is null")
                }
                _isLoading.value = false
            }
        }
    }

    // --- GET BY ID ---
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

    // --- CREATE ---
    fun createKegiatanMultipart(
        idUser: String,
        nama: String,
        tanggal: String,
        lokasi: String?,
        penanggungjawab: String?,
        deskripsi: String?,
        status: String,
        fotoFile: File?,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // text helper
                fun textPart(value: String?): RequestBody? {
                    return value?.takeIf { it.isNotBlank() }
                        ?.toRequestBody("text/plain".toMediaTypeOrNull())
                }

                val idUserPart = idUser.toRequestBody("text/plain".toMediaTypeOrNull())
                val namaPart = nama.toRequestBody("text/plain".toMediaTypeOrNull())
                val tanggalPart = tanggal.toRequestBody("text/plain".toMediaTypeOrNull())
                val lokasiPart = textPart(lokasi)
                val penanggungjawabPart = textPart(penanggungjawab)
                val deskripsiPart = textPart(deskripsi)
                val statusPart = status.toRequestBody("text/plain".toMediaTypeOrNull())

                val fotoPart = fotoFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData(
                        "foto_kegiatan",
                        it.name,
                        requestFile
                    )
                }

                repository.createKegiatanMultipart(
                    idUser = idUserPart,
                    namaKegiatan = namaPart,
                    tanggalKegiatan = tanggalPart,
                    lokasi = lokasiPart,
                    penanggungjawab = penanggungjawabPart,
                    deskripsi = deskripsiPart,
                    statusKegiatan = statusPart,
                    foto = fotoPart
                ) { isSuccess, message ->
                    if (isSuccess) loadKegiatan()
                    onResult(isSuccess, message)
                }

            } catch (e: Exception) {
                Log.e(TAG, "createKegiatanMultipart error: ${e.message}")
                onResult(false, "Gagal menyimpan kegiatan")
            }
        }
    }

    // --- UPDATE ---
    fun updateKegiatan(
        id: String,
        kegiatanDto: KegiatanDto,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.updateKegiatan(id, kegiatanDto) { isSuccess, message ->
                    if (isSuccess) loadKegiatan()
                    onResult(isSuccess, message)
                }
            } catch (e: Exception) {
                Log.e(TAG, "updateKegiatan exception: ${e.message}")
                onResult(false, "Terjadi error: ${e.message}")
            }
        }
    }

    // --- DELETE ---
    fun deleteKegiatan(id: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteKegiatan(id) { isSuccess, message ->
                if (isSuccess) loadKegiatan()
                else _errorMessage.value = message
                _isLoading.value = false
                onResult(isSuccess, message)
            }
        }
    }

    fun clearSelectedItem() { _selectedItem.value = null }

    // --- Helper konversi dari KegiatanDto ke KegiatanEntry untuk UI ---
    private fun KegiatanDto.toKegiatanEntry(): KegiatanEntry? {
        // id_kegiatan bisa null — skip bila null
        if (this.id_kegiatan == null) {
            Log.w(TAG, "toKegiatanEntry: skipping, id_kegiatan == null")
            return null
        }

        // Format tanggal jika backend mengirim ISO timestamp, fallback ke substring
        val tanggalFormatted = try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(this.tanggal_kegiatan ?: "")
            val out = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
            out.format(date!!)
        } catch (e: Exception) {
            this.tanggal_kegiatan?.substringBefore("T") ?: this.tanggal_kegiatan ?: "Tanggal Invalid"
        }

        // Jika foto_kegiatan berisi nama file atau path, jangan ubah — UI bisa menangani URL lengkap atau relatif
        val foto = this.foto_kegiatan

        return KegiatanEntry(
            id = this.id_kegiatan.toString(),
            nama = this.nama_kegiatan ?: "",
            tanggal = tanggalFormatted,
            waktu = this.waktu_kegiatan,
            lokasi = this.lokasi,
            penanggungjawab = this.penanggungjawab,
            deskripsi = this.deskripsi,
            status = this.status_kegiatan,
            fotoUrl = foto
        )
    }
}