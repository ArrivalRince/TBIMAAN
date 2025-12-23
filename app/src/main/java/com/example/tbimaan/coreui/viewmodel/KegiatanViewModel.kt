package com.example.tbimaan.coreui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbimaan.coreui.repository.KegiatanRepository
import com.example.tbimaan.model.KegiatanResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class KegiatanEntry(
    val id: String,
    val nama: String,
    val tanggal: String,
    val lokasi: String?,
    val penanggungjawab: String?,
    val deskripsi: String?,
    val status: String?,
    val fotoUrl: String?,
    val originalDate: Date?
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


    fun loadKegiatan(context: Context, idUser: Int) {
        if (_isLoading.value) return

        viewModelScope.launch {
            if (_kegiatanList.value.isEmpty()) _isLoading.value = true
            _errorMessage.value = ""

            repository.getKegiatan(idUser) { responseList ->
                _isLoading.value = false

                if (responseList != null) {
                    _kegiatanList.value =
                        responseList.mapNotNull { it.toKegiatanEntry() }

                    Log.d(
                        TAG,
                        "loadKegiatan: loaded ${_kegiatanList.value.size} items for user $idUser"
                    )
                } else {
                    _errorMessage.value = "Gagal mengambil data kegiatan."
                    Log.e(TAG, "loadKegiatan: response null")
                }
            }
        }
    }


    fun createKegiatanMultipart(
        idUser: String,
        nama: String,
        tanggal: String,
        lokasi: String?,
        penanggungjawab: String?,
        deskripsi: String?,
        status: String,
        fotoFile: File?,
        context: Context,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                fun textPart(v: String?): RequestBody? =
                    v?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())

                val fotoPart = fotoFile?.let {
                    MultipartBody.Part.createFormData(
                        "foto_kegiatan",
                        it.name,
                        it.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                repository.createKegiatanMultipart(
                    idUser = textPart(idUser)!!,
                    namaKegiatan = textPart(nama)!!,
                    tanggalKegiatan = textPart(tanggal)!!,
                    lokasi = textPart(lokasi),
                    penanggungjawab = textPart(penanggungjawab),
                    deskripsi = textPart(deskripsi),
                    statusKegiatan = textPart(status)!!,
                    foto = fotoPart
                ) { success, message ->
                    if (success) loadKegiatan(context, idUser.toInt())
                    onResult(success, message)
                }

            } catch (e: Exception) {
                Log.e(TAG, "createKegiatan error", e)
                onResult(false, e.message ?: "Gagal menyimpan kegiatan")
            }
        }
    }


    fun updateKegiatanMultipart(
        id: String,
        idUser: String,
        nama: String,
        tanggal: String,
        lokasi: String?,
        penanggungjawab: String?,
        deskripsi: String?,
        status: String,
        fotoFile: File?,
        context: Context,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                fun textPart(v: String?): RequestBody? =
                    v?.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())

                val fotoPart = fotoFile?.let {
                    MultipartBody.Part.createFormData(
                        "foto_kegiatan",
                        it.name,
                        it.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                repository.updateKegiatanMultipart(
                    id = id,
                    idUser = textPart(idUser)!!,
                    namaKegiatan = textPart(nama)!!,
                    tanggalKegiatan = textPart(tanggal)!!,
                    lokasi = textPart(lokasi),
                    penanggungjawab = textPart(penanggungjawab),
                    deskripsi = textPart(deskripsi),
                    statusKegiatan = textPart(status)!!,
                    foto = fotoPart
                ) { success, message ->
                    if (success) loadKegiatan(context, idUser.toInt())
                    onResult(success, message)
                }

            } catch (e: Exception) {
                Log.e(TAG, "updateKegiatan error", e)
                onResult(false, e.message ?: "Gagal update kegiatan")
            }
        }
    }


    // Di ViewModel
    fun deleteKegiatan(
        id: String,
        context: Context,
        idUser: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.deleteKegiatan(id) { success, message ->
                if (success) {
                    // Hapus item dari list lokal agar langsung hilang di UI
                    _kegiatanList.value = _kegiatanList.value.filter { it.id != id }
                } else {
                    _errorMessage.value = message
                }
                _isLoading.value = false
                onResult(success, message)
            }
        }
    }


    fun getKegiatanById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true

            repository.getKegiatanById(id) { response ->
                _selectedItem.value = response?.toKegiatanEntry()

                if (response == null) {
                    _errorMessage.value = "Gagal memuat detail kegiatan."
                }

                _isLoading.value = false
            }
        }
    }

    fun clearSelectedItem() {
        _selectedItem.value = null
    }

    private fun KegiatanResponse.toKegiatanEntry(): KegiatanEntry? {
        if (idKegiatan == null) return null

        val formats = listOf(
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            ).apply { timeZone = TimeZone.getTimeZone("UTC") },
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )

        var parsedDate: Date? = null
        for (f in formats) {
            try {
                parsedDate = f.parse(tanggalKegiatan ?: "")
                if (parsedDate != null) break
            } catch (_: ParseException) {}
        }

        val formattedDate =
            parsedDate?.let {
                SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID")).format(it)
            } ?: tanggalKegiatan ?: "-"

        return KegiatanEntry(
            id = idKegiatan.toString(),
            nama = namaKegiatan ?: "",
            tanggal = formattedDate,
            lokasi = lokasi,
            penanggungjawab = penanggungjawab,
            deskripsi = deskripsi,
            status = statusKegiatan,
            fotoUrl = fotoKegiatan,
            originalDate = parsedDate
        )
    }
}
