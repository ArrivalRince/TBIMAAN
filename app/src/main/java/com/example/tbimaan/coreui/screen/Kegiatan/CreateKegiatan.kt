package com.example.tbimaan.coreui.screen.Kegiatan

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tbimaan.R
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.coreui.components.PrimaryButton
import com.example.tbimaan.coreui.components.SecondaryButton
import com.example.tbimaan.coreui.utils.getTempUri
import com.example.tbimaan.coreui.utils.uriToFile
import com.example.tbimaan.coreui.viewmodel.KegiatanViewModel
import com.example.tbimaan.model.UserSession
import com.example.tbimaan.network.KegiatanDto
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateKegiatanScreen(
    navController: NavController,
    viewModel: KegiatanViewModel
) {
    var nama by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") } // YYYY-MM-DD
    var waktu by remember { mutableStateOf("") } // e.g. 07:00 WIB
    var lokasi by remember { mutableStateOf("") }
    var penceramah by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }

    val status = "Akan Datang"
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUriHolder by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) imageUri = uri }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success -> if (success) imageUri = tempUriHolder }

    val calendar = remember { Calendar.getInstance() }

    // Date picker dialog
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                tanggal = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header image + back
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.masjiddua),
                        contentDescription = "Header",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    BackButtonOnImage(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tambah Data Kegiatan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E5B8A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = nama,
                            onValueChange = { nama = it },
                            label = { Text("Nama Kegiatan") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = tanggal,
                            onValueChange = {},
                            label = { Text("Tanggal (YYYY-MM-DD)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePickerDialog.show() },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialog.show() }) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Pilih tanggal")
                                }
                            }
                        )

                        OutlinedTextField(
                            value = waktu,
                            onValueChange = {},
                            label = { Text("Waktu (misal: 07:00 WIB)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val h = calendar.get(Calendar.HOUR_OF_DAY)
                                    val m = calendar.get(Calendar.MINUTE)
                                    TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            waktu = "%02d:%02d WIB".format(hourOfDay, minute)
                                        },
                                        h, m, true
                                    ).show()
                                },
                            readOnly = true
                        )

                        OutlinedTextField(
                            value = lokasi,
                            onValueChange = { lokasi = it },
                            label = { Text("Lokasi") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = penceramah,
                            onValueChange = { penceramah = it },
                            label = { Text("Penceramah") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = deskripsi,
                            onValueChange = { deskripsi = it },
                            label = { Text("Deskripsi Kegiatan") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            label = { Text("Status Kegiatan") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF0F5F9))
                                .border(1.dp, Color(0xFFDDEEFF), RoundedCornerShape(16.dp))
                                .clickable { showImageSourceDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUri == null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Tambah Foto",
                                        tint = Color(0xFF1E5B8A),
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Tambah Foto", color = Color(0xFF1E5B8A))
                                }
                            } else {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = "Foto Kegiatan",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SecondaryButton(
                        text = "Batal",
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f)
                    )

                    PrimaryButton(
                        text = if (isLoading) "Menyimpan..." else "Simpan",
                        onClick = {
                            // Validasi
                            if (nama.isBlank() || tanggal.isBlank() || waktu.isBlank() || lokasi.isBlank() || penceramah.isBlank()) {
                                Toast.makeText(context, "Harap isi semua field wajib.", Toast.LENGTH_SHORT).show()
                                return@PrimaryButton
                            }

                            // Konversi URI ke File (jika ada)
                            val file: File? = try {
                                imageUri?.let { uriToFile(context, it) }
                            } catch (e: Exception) {
                                null
                            }

                            if (imageUri != null && (file == null || !file.exists() || file.length() == 0L)) {
                                Toast.makeText(context, "Foto gagal diproses / tidak valid", Toast.LENGTH_SHORT).show()
                                return@PrimaryButton
                            }

                            // Build KegiatanDto sesuai ApiService kamu
                            val idUser = UserSession.idUser ?: 1
                            val kegiatanDto = KegiatanDto(
                                id_kegiatan = null,
                                id_user = idUser,
                                nama_kegiatan = nama,
                                tanggal_kegiatan = tanggal,
                                waktu_kegiatan = waktu,
                                lokasi = lokasi.ifBlank { null },
                                penceramah = penceramah.ifBlank { null },
                                deskripsi = deskripsi.ifBlank { null },
                                status_kegiatan = status,
                                // karena API saat ini tidak multipart, kirim nama file (atau null)
                                foto_kegiatan = file?.name
                            )

                            // Panggil ViewModel untuk menyimpan
                            isLoading = true
                            scope.launch {
                                viewModel.createKegiatan(
                                    kegiatanDto = kegiatanDto,
                                    onResult = { success, message ->
                                        isLoading = false
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        if (success) navController.popBackStack()
                                    }
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Overlay loading
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            // Dialog pilih sumber gambar
            if (showImageSourceDialog) {
                AlertDialog(
                    onDismissRequest = { showImageSourceDialog = false },
                    title = { Text("Pilih Sumber Foto") },
                    text = { Text("Pilih dari Galeri atau ambil foto baru?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showImageSourceDialog = false
                            val tmp = getTempUri(context)
                            tempUriHolder = tmp
                            cameraLauncher.launch(tmp)
                        }) { Text("Kamera") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showImageSourceDialog = false
                            galleryLauncher.launch("image/*")
                        }) { Text("Galeri") }
                    }
                )
            }
        }
    }
}
