package com.example.tbimaan.coreui.screen.Keuangan

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
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
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tbimaan.R
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.coreui.viewmodel.KeuanganViewModel
import com.example.tbimaan.model.SessionManager
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

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

private fun getTempUriUpdate(context: Context): Uri {
    val tempFile = File.createTempFile("temp_image_update_${System.currentTimeMillis()}", ".jpg", context.cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateKeuanganScreen(
    navController: NavController,
    id: String,
    viewModel: KeuanganViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val item by viewModel.selectedItem
    val isLoadingInitialData by viewModel.isLoading
    var isUpdating by remember { mutableStateOf(false) }

    var keterangan by remember { mutableStateOf("") }
    var jumlah by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    val options = listOf("Pemasukan", "Pengeluaran")
    var selectedTipe by remember { mutableStateOf("") }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }
    var existingImageUrl by remember { mutableStateOf<String?>(null) }

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempUriHolder by remember { mutableStateOf<Uri?>(null) }
    val activity = LocalContext.current as? Activity

    LaunchedEffect(key1 = id) {
        viewModel.getKeuanganById(id)
    }

    LaunchedEffect(key1 = item) {
        item?.let { loadedItem ->
            keterangan = loadedItem.keterangan
            jumlah = loadedItem.jumlah.toLong().toString()
            selectedTipe = loadedItem.tipeTransaksi.replaceFirstChar { it.titlecase(Locale.getDefault()) }
            existingImageUrl = loadedItem.urlBukti
            tanggal = try {
                val parser = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
                val date = parser.parse(loadedItem.tanggal)
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                formatter.format(date!!)
            } catch (e: Exception) {
                loadedItem.tanggal
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.clearSelectedItem() }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) newImageUri = uri
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) newImageUri = tempUriHolder
    }

    val datePickerDialog = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            activity ?: context,
            { _, y, m, d -> tanggal = "%04d-%02d-%02d".format(y, m + 1, d) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Scaffold(containerColor = Color(0xFFF8F9FA)) { innerPadding ->
        if (isLoadingInitialData && item == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HeaderContent(navController)
                Text(
                    "Perbarui Data Keuangan",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E5B8A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp, bottom = 8.dp)
                )

                OutlinedTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = { Text("Keterangan") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = jumlah,
                    onValueChange = { jumlah = it },
                    label = { Text("Jumlah") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = tanggal,
                    onValueChange = {},
                    label = { Text("Tanggal") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).clickable { datePickerDialog.show() },
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = { IconButton({ datePickerDialog.show() }) { Icon(Icons.Default.DateRange, "Pilih Tanggal") } }
                )

                var isDropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                ) {
                    OutlinedTextField(
                        value = selectedTipe,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipe Transaksi") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedTipe = option
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF0F5F9))
                        .border(1.dp, Color(0xFFDDEEFF), RoundedCornerShape(16.dp))
                        .clickable { showImageSourceDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = newImageUri ?: existingImageUrl,
                        contentDescription = "Foto Bukti",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(R.drawable.logo_imaan)
                    )
                    if (newImageUri == null && existingImageUrl.isNullOrBlank()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.AddPhotoAlternate, "Upload",
                                tint = Color(0xFF1E5B8A).copy(0.8f),
                                modifier = Modifier.size(40.dp)
                            )
                            Text("Ubah Foto Bukti", color = Color(0xFF1E5B8A), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(Modifier.weight(1f))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f),
                        enabled = !isUpdating,
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Batal", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val file = newImageUri?.let { uriToFileUpdate(context, it) }
                            if (keterangan.isBlank() || jumlah.isBlank() || tanggal.isBlank()) {
                                Toast.makeText(context, "Keterangan, Jumlah, dan Tanggal wajib diisi.", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                            isUpdating = true
                            viewModel.updateKeuangan(
                                id = id,
                                currentUserId = sessionManager.idUser,
                                keterangan = keterangan,
                                tipeTransaksi = selectedTipe,
                                tanggal = tanggal,
                                jumlah = jumlah,
                                buktiFile = file,
                                onResult = { isSuccess, message ->
                                    isUpdating = false
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (isSuccess) {
                                        navController.popBackStack()
                                    }
                                }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isUpdating,
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004AAD))
                    ) {
                        Text("Update", color = Color.White)
                    }
                }
            }

            if (isUpdating) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(false) {},
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
            title = { Text("Pilih Sumber Foto") },
            text = { Text("Ambil foto baru atau pilih dari galeri?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        val uri = getTempUriUpdate(context)
                        tempUriHolder = uri
                        cameraLauncher.launch(uri)
                    }
                ) { Text("Kamera") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        galleryLauncher.launch("image/*")
                    }
                ) { Text("Galeri") }
            }
        )
    }
}
