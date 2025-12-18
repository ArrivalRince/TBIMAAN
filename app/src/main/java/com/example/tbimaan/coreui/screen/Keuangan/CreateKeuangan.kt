package com.example.tbimaan.coreui.screen.Keuangan

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tbimaan.coreui.viewmodel.KeuanganViewModel
import com.example.tbimaan.model.SessionManager
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateKeuanganScreen(
    navController: NavController,
    viewModel: KeuanganViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    var keterangan by remember { mutableStateOf("") }
    var jumlah by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    val options = listOf("Pemasukan", "Pengeluaran")
    var selectedTipe by remember { mutableStateOf(options[0]) }
    var isTipeExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempUriHolder by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if (uri != null) imageUri = uri }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success -> if (success) imageUri = tempUriHolder }
    val datePickerDialog = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            activity ?: context,
            { _, year, month, dayOfMonth -> tanggal = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Scaffold(containerColor = Color(0xFFF8F9FA)) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HeaderContent(navController)
                Text(text = "Tambahkan Data Keuangan", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1E5B8A), modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 16.dp, bottom = 8.dp))
                OutlinedTextField(value = keterangan, onValueChange = { keterangan = it }, label = { Text("Keterangan") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), shape = RoundedCornerShape(16.dp))
                OutlinedTextField(value = jumlah, onValueChange = { jumlah = it }, label = { Text("Jumlah (contoh: 50000)") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), shape = RoundedCornerShape(16.dp))
                OutlinedTextField(
                    value = tanggal, onValueChange = {}, label = { Text("Tanggal (YYYY-MM-DD)") }, readOnly = true, singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).clickable { datePickerDialog.show() },
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = { IconButton(onClick = { datePickerDialog.show() }) { Icon(Icons.Default.DateRange, "Pilih Tanggal") } }
                )
                ExposedDropdownMenuBox(
                    expanded = isTipeExpanded, onExpandedChange = { isTipeExpanded = !isTipeExpanded },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                ) {
                    OutlinedTextField(
                        value = selectedTipe, onValueChange = {}, readOnly = true, label = { Text("Tipe Transaksi") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTipeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(16.dp)
                    )
                    ExposedDropdownMenu(expanded = isTipeExpanded, onDismissRequest = { isTipeExpanded = false }) {
                        options.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { selectedTipe = option; isTipeExpanded = false }) }
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 24.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFF0F5F9)).border(1.dp, Color(0xFFDDEEFF), RoundedCornerShape(16.dp)).clickable { showImageSourceDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, "Upload Icon", tint = Color(0xFF1E5B8A).copy(alpha = 0.8f), modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Upload Bukti", color = Color(0xFF1E5B8A), fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        AsyncImage(imageUri, "Bukti Terpilih", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Batal", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val file = try { imageUri?.let { uriToFile(context, it) } } catch (e: Exception) { null }
                            if (keterangan.isBlank() || jumlah.isBlank() || tanggal.isBlank() || file == null) {
                                Toast.makeText(context, "Semua field dan foto bukti wajib diisi", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                            isLoading = true
                            viewModel.createKeuangan(
                                currentUserId = sessionManager.idUser,
                                keterangan = keterangan,
                                tipeTransaksi = selectedTipe,
                                tanggal = tanggal,
                                jumlah = jumlah,
                                buktiFile = file,
                                context = context,
                                onResult = { isSuccess, message ->
                                    isLoading = false
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    if (isSuccess) {
                                        navController.popBackStack()
                                    }
                                }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004AAD))
                    ) {
                        Text("Simpan", color = Color.White)
                    }
                }
            }
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable(enabled = false) {}, contentAlignment = Alignment.Center) {
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
            confirmButton = { TextButton(onClick = { showImageSourceDialog = false; val uri = getTempUri(context); tempUriHolder = uri; cameraLauncher.launch(uri) }) { Text("Kamera") } },
            dismissButton = { TextButton(onClick = { showImageSourceDialog = false; galleryLauncher.launch("image/*") }) { Text("Galeri") } }
        )
    }
}
