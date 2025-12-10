package com.example.tbimaan.coreui.screen.Inventaris

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tbimaan.R
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.coreui.components.PrimaryButton
import com.example.tbimaan.coreui.components.SecondaryButton
import com.example.tbimaan.coreui.screen.Keuangan.uriToFile // Menggunakan kembali helper
import com.example.tbimaan.coreui.screen.Keuangan.getTempUri // Menggunakan kembali helper
import com.example.tbimaan.coreui.viewmodel.InventarisViewModel // <-- IMPORT PENTING
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInventarisScreen(
    navController: NavController,
    viewModel: InventarisViewModel // <-- PERBAIKAN: Terima ViewModel sebagai parameter
) {
    var namaBarang by remember { mutableStateOf("") }
    var jumlahBarang by remember { mutableStateOf("") }
    var kondisi by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") } // YYYY-MM-DD
    val textColorPrimary = Color(0xFF1E5B8A)

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempUriHolder by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if (uri != null) imageUri = uri }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success -> if (success) imageUri = tempUriHolder }

    val datePickerDialog = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                tanggal = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

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
                // Header (Gambar & Tombol Kembali)
                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                ) {
                    Image(painter = painterResource(id = R.drawable.masjid), contentDescription = "Header", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    BackButtonOnImage(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.TopStart))
                }

                // Judul
                Text(
                    text = "Tambahkan Data Inventaris Baru",
                    style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = textColorPrimary,
                    modifier = Modifier.padding(top = 24.dp).align(Alignment.CenterHorizontally)
                )

                // Form Fields
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(value = namaBarang, onValueChange = { namaBarang = it }, label = { Text("Nama Barang") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
                    OutlinedTextField(value = jumlahBarang, onValueChange = { jumlahBarang = it }, label = { Text("Jumlah Barang") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
                    OutlinedTextField(value = kondisi, onValueChange = { kondisi = it }, label = { Text("Kondisi (contoh: Baik, Rusak)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
                    OutlinedTextField(
                        value = tanggal, onValueChange = {}, label = { Text("Tanggal Pembelian (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                        readOnly = true, shape = RoundedCornerShape(16.dp),
                        trailingIcon = { Icon(Icons.Default.DateRange, "Pilih Tanggal", modifier = Modifier.clickable { datePickerDialog.show() }) }
                    )

                    // Upload Foto
                    Box(
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFF0F5F9)).border(1.dp, Color(0xFFDDEEFF), RoundedCornerShape(16.dp)).clickable { showImageSourceDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri == null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(imageVector = Icons.Default.AddPhotoAlternate, "Upload", tint = textColorPrimary.copy(alpha = 0.8f), modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Upload Foto", color = textColorPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        } else {
                            AsyncImage(model = imageUri, "Foto Barang", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Tombol Aksi
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SecondaryButton(text = "Batal", onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f))

                    // ==========================================================
                    // ===            PERBAIKAN UTAMA ADA DI SINI             ===
                    // ==========================================================
                    PrimaryButton(
                        text = "Simpan",
                        onClick = {
                            val file = imageUri?.let { uriToFile(context, it) }
                            if (namaBarang.isBlank() || jumlahBarang.isBlank() || tanggal.isBlank() || file == null) {
                                Toast.makeText(context, "Semua kolom dan foto wajib diisi", Toast.LENGTH_SHORT).show()
                            } else {
                                isLoading = true
                                // Panggil ViewModel untuk menyimpan data
                                viewModel.createInventaris(
                                    idUser = "1", // Ganti dengan ID user yang sedang login
                                    namaBarang = namaBarang,
                                    kondisi = kondisi.ifBlank { "Baik" },
                                    jumlah = jumlahBarang,
                                    tanggal = tanggal,
                                    fotoFile = file,
                                    onResult = { isSuccess, message ->
                                        isLoading = false
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        if (isSuccess) {
                                            navController.popBackStack() // Kembali jika sukses
                                        }
                                    }
                                )
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    // ==========================================================
                }
            }

            // --- UI Loading ---
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable(enabled = false, onClick = {}), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }

    // Dialog pilihan sumber gambar
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Pilih Sumber Foto") },
            text = { Text("Pilih dari Galeri atau ambil foto baru?") },
            confirmButton = { TextButton(onClick = { showImageSourceDialog = false; val tempUri = getTempUri(context); tempUriHolder = tempUri; cameraLauncher.launch(tempUri) }) { Text("Kamera") } },
            dismissButton = { TextButton(onClick = { showImageSourceDialog = false; galleryLauncher.launch("image/*") }) { Text("Galeri") } }
        )
    }
}
