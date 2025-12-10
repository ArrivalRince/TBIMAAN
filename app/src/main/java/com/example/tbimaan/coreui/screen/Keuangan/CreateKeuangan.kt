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
import java.util.Calendar

fun getTempUri(context: Context): Uri {
    val tempFile = File.createTempFile("temp_image_${System.currentTimeMillis()}", ".jpg", context.cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
}

fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
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
// === HeaderContent untuk Gambar Masjid dan Tombol Kembali ===
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
fun CreateKeuanganScreen(
    navController: NavController,
    viewModel: KeuanganViewModel = viewModel()
) {
    // --- States untuk Form ---
    var keterangan by remember { mutableStateOf("") }
    var jumlah by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    val options = listOf("Pemasukan", "Pengeluaran")
    var selectedTipe by remember { mutableStateOf(options[0]) }
    var isTipeExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = LocalContext.current as? Activity

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempUriHolder by remember { mutableStateOf<Uri?>(null) }

    // --- Image Pickers ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> if (uri != null) imageUri = uri }
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) imageUri = tempUriHolder }
    )

    // ================= DatePicker: remembered and using Activity context if available =================
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
        ).apply {
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
    }
    // ================================================================================================

    Scaffold(
        // === TopAppBar DIHAPUS agar gambar masjid bisa full-width dan judul diletakkan di bawahnya ===
        // Kode asli TopAppBar yang dihapus:
        /*
        topBar = {
            TopAppBar(
                title = { Text("Tambahkan Data Keuangan", fontWeight = FontWeight.Bold, color = Color(0xFF1E5B8A)) },
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
        Box(modifier = Modifier.fillMaxSize()) {
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
                    text = "Tambahkan Data Keuangan", // Mengganti teks dari TopAppBar yang dihapus
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
                    label = { Text("Jumlah (contoh: 50000)") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), // Menambahkan padding
                    shape = RoundedCornerShape(16.dp)
                )

                // ============ Tanggal field (readOnly) ============
                OutlinedTextField(
                    value = tanggal,
                    onValueChange = {},
                    label = { Text("Tanggal (YYYY-MM-DD)") },
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp) // Menambahkan padding
                        .clickable {
                            try {
                                datePickerDialog.show()
                            } catch (e: Exception) {
                                // silent fail; Anda bisa log jika perlu
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            try {
                                datePickerDialog.show()
                            } catch (e: Exception) {
                                // ignore
                            }
                        }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                        }
                    }
                )
                // =====================================================

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
                        .height(180.dp)
                        .padding(horizontal = 24.dp) // Menambahkan padding
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF0F5F9))
                        .border(1.dp, Color(0xFFDDEEFF), RoundedCornerShape(16.dp))
                        .clickable { showImageSourceDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Upload Icon", tint = Color(0xFF1E5B8A).copy(alpha = 0.8f), modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Upload Bukti", color = Color(0xFF1E5B8A), fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        AsyncImage(model = imageUri, contentDescription = "Bukti Terpilih", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val buktiUri = imageUri
                        if (keterangan.isBlank() || jumlah.isBlank() || tanggal.isBlank() || buktiUri == null) {
                            Toast.makeText(context, "Semua field dan foto bukti wajib diisi", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        val file = uriToFile(context, buktiUri)
                        if (file == null) {
                            Toast.makeText(context, "Gagal memproses file gambar", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        isLoading = true

                        viewModel.createKeuangan(
                            idUser = "1", // Ganti dengan ID user yang sedang login nanti
                            keterangan = keterangan,
                            tipeTransaksi = selectedTipe,
                            tanggal = tanggal,
                            jumlah = jumlah,
                            buktiFile = file,
                            onResult = { isSuccess, message ->
                                isLoading = false
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
                    Text("SIMPAN", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
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
                    val uri = getTempUri(context)
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
}