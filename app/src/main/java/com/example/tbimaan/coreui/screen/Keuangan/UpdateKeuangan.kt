package com.example.tbimaan.coreui.screen.Keuangan

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image // Import tambahan
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.res.painterResource // Import tambahan
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tbimaan.R // Import untuk mengakses R.drawable
import com.example.tbimaan.coreui.components.BackButtonOnImage // Import tambahan
import com.example.tbimaan.coreui.viewmodel.KeuanganViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * UpdateKeuangan.kt (perbaikan)
 *
 * Perbaikan utama:
 * - DatePickerDialog dibuat menggunakan Activity bila tersedia (hindari Window token errors).
 * - Pembentukan dialog dibungkus dengan remember agar stabil di recomposition.
 * - Icon kalender dibuat clickable untuk membuka DatePicker.
 * - Parsing tanggal dengan beberapa fallback format dan format output "yyyy-MM-dd".
 * - Konversi `jumlah` aman (membiarkan string kosong bila null).
 * - Membersihkan selectedItem pada onDispose.
 * - **Perbaikan Baru**: Menghapus TopAppBar, menambahkan HeaderContent (gambar masjid) dan Judul di bawahnya.
 *
 * Salin file ini dan replace file lama, lalu rebuild aplikasi.
 */

// Helper functions (bisa dipindah ke FileUtils.kt)
private fun getTempUriUpdate(context: Context): Uri {
    val tempFile = File.createTempFile("temp_image_update_${System.currentTimeMillis()}", ".jpg", context.cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
}

private fun uriToFileUpdate(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload_update_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// =======================================================================
// === HeaderContent untuk Gambar Masjid dan Tombol Kembali (DISALIN) ===
// =======================================================================
@Composable
private fun HeaderContent(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.masjid),
            contentDescription = "Masjid Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        BackButtonOnImage(
            onClick = { navController.navigateUp() },
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}
// =======================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateKeuanganScreen(
    navController: NavController,
    id: String,
    viewModel: KeuanganViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = LocalContext.current as? Activity
    val item by viewModel.selectedItem
    val isLoadingInitialData by viewModel.isLoading

    // --- FORM STATES ---
    var keterangan by remember { mutableStateOf("") }
    var jumlah by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") } // Format YYYY-MM-DD
    val options = listOf("Pemasukan", "Pengeluaran")
    var selectedTipe by remember { mutableStateOf("") }
    var isTipeExpanded by remember { mutableStateOf(false) }

    // --- GAMBAR STATES ---
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var existingImageUrl by remember { mutableStateOf<String?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempUriHolder by remember { mutableStateOf<Uri?>(null) }

    var isUpdating by remember { mutableStateOf(false) }

    // Ambil data dari server sekali saat screen dibuka
    LaunchedEffect(key1 = id) {
        viewModel.getKeuanganById(id)
    }

    // Isi form setelah data berhasil dimuat
    LaunchedEffect(key1 = item) {
        item?.let { loadedItem ->
            keterangan = loadedItem.keterangan ?: ""

            // Konversi jumlah dengan aman
            jumlah = loadedItem.jumlah?.toString() ?: ""

            // Parsing tanggal dengan fallback
            val outputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate: Date? = try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(loadedItem.tanggal ?: "")
            } catch (e1: Exception) {
                try {
                    SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID")).parse(loadedItem.tanggal ?: "")
                } catch (e2: Exception) {
                    null
                }
            }
            tanggal = parsedDate?.let { outputFormatter.format(it) } ?: (loadedItem.tanggal ?: "")

            selectedTipe = loadedItem.tipeTransaksi?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            } ?: ""
            existingImageUrl = loadedItem.urlBukti
        }
    }

    // Bersihkan state saat keluar screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSelectedItem()
        }
    }

    // --- LAUNCHERS & DIALOGS ---
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if (uri != null) imageUri = uri }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success -> if (success) imageUri = tempUriHolder }

    // Buat DatePickerDialog menggunakan Activity bila tersedia
    val datePickerDialog = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            activity ?: context,
            { _, year, month, dayOfMonth ->
                tanggal = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // --- UI ---
    Scaffold(
        // === TopAppBar DIHAPUS agar gambar masjid bisa full-width dan judul diletakkan di bawahnya ===
        /*
        topBar = {
            TopAppBar(
                title = { Text("Perbarui Data Keuangan", fontWeight = FontWeight.Bold, color = Color(0xFF1E5B8A)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        */
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        if (isLoadingInitialData && item == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // =====================================================
                // === KODE BARU: Gambar Masjid (sebagai Header) ===
                // =====================================================
                HeaderContent(navController)

                // =====================================================
                // === KODE BARU: Judul di bawah Gambar Masjid ===
                // =====================================================
                Text(
                    text = "Perbarui Data Keuangan", // Mengganti teks dari TopAppBar yang dihapus
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E5B8A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp, bottom = 8.dp)
                )
                // =====================================================

                OutlinedTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = { Text("Keterangan") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), // Menambahkan padding
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = jumlah,
                    onValueChange = { jumlah = it },
                    label = { Text("Jumlah") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), // Menambahkan padding
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = tanggal,
                    onValueChange = {},
                    label = { Text("Tanggal") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp) // Menambahkan padding
                        .clickable { datePickerDialog.show() },
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                        }
                    }
                )

                ExposedDropdownMenuBox(
                    expanded = isTipeExpanded,
                    onExpandedChange = { isTipeExpanded = !isTipeExpanded },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp) // Menambahkan padding
                ) {
                    OutlinedTextField(
                        value = selectedTipe,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipe Transaksi") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTipeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    ExposedDropdownMenu(expanded = isTipeExpanded, onDismissRequest = { isTipeExpanded = false }) {
                        options.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = { selectedTipe = option; isTipeExpanded = false })
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 24.dp) // Menambahkan padding
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF0F5F9))
                        .border(1.dp, Color(0xFFDDEEFF), RoundedCornerShape(16.dp))
                        .clickable { showImageSourceDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    val imageToShow = imageUri?.toString() ?: existingImageUrl
                    if (imageToShow.isNullOrBlank()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Upload Icon", tint = Color(0xFF1E5B8A), modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Ubah Bukti", color = Color(0xFF1E5B8A), fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        AsyncImage(model = imageToShow, contentDescription = "Bukti Transaksi", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }

                Button(
                    onClick = {
                        val file = imageUri?.let { uriToFileUpdate(context, it) }

                        if (keterangan.isBlank() || jumlah.isBlank() || tanggal.isBlank()) {
                            Toast.makeText(context, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isUpdating = true

                        viewModel.updateKeuangan(
                            id = id,
                            keterangan = keterangan,
                            tipeTransaksi = selectedTipe,
                            tanggal = tanggal,
                            jumlah = jumlah,
                            buktiFile = file,
                            onResult = { isSuccess, message ->
                                isUpdating = false
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                if (isSuccess) {
                                    navController.popBackStack()
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp) // Menambahkan padding
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BC0DE))
                ) {
                    Text("SIMPAN PERUBAHAN", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Pilih Sumber Gambar") },
            text = { Text("Pilih dari Galeri atau ambil foto baru?") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    val uri = getTempUriUpdate(context)
                    tempUriHolder = uri
                    cameraLauncher.launch(uri)
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

    // Overlay loading saat update
    if (isUpdating) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}