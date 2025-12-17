package com.example.tbimaan.coreui.screen.Inventaris

import android.app.Activity
import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.tbimaan.coreui.utils.getTempUri
import com.example.tbimaan.coreui.utils.uriToFile
import com.example.tbimaan.coreui.viewmodel.InventarisViewModel
import com.example.tbimaan.model.SessionManager
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInventarisScreen(
    navController: NavController,
    viewModel: InventarisViewModel
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val sessionManager = remember { SessionManager(context) }

    var namaBarang by remember { mutableStateOf("") }
    var jumlahBarang by remember { mutableStateOf("") }
    var kondisi by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempUriHolder by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) imageUri = uri
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) imageUri = tempUriHolder
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

    Scaffold(containerColor = Color.White) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {

                // ===== HEADER =====
                Box(Modifier.fillMaxWidth().height(180.dp)) {
                    Image(
                        painter = painterResource(R.drawable.masjid),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    BackButtonOnImage(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }

                Text(
                    text = "Tambah Data Inventaris",
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E5B8A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    OutlinedTextField(
                        value = namaBarang,
                        onValueChange = { namaBarang = it },
                        label = { Text("Nama Barang") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = jumlahBarang,
                        onValueChange = { jumlahBarang = it },
                        label = { Text("Jumlah Barang") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = kondisi,
                        onValueChange = { kondisi = it },
                        label = { Text("Kondisi Barang") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    OutlinedTextField(
                        value = tanggal,
                        onValueChange = {},
                        label = { Text("Tanggal Pembelian") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() },
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Default.DateRange, null)
                            }
                        },
                        shape = RoundedCornerShape(16.dp)
                    )

                    // ===== FOTO =====
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
                                    contentDescription = null,
                                    tint = Color(0xFF1E5B8A),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("Upload Foto", color = Color(0xFF1E5B8A))
                            }
                        } else {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ===== BUTTON =====
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
                        text = "Simpan",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val file = try {
                                imageUri?.let { uriToFile(context, it) }
                            } catch (e: Exception) {
                                null
                            }

                            if (
                                namaBarang.isBlank() ||
                                jumlahBarang.isBlank() ||
                                tanggal.isBlank() ||
                                file == null
                            ) {
                                Toast.makeText(
                                    context,
                                    "Semua field dan foto wajib diisi",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@PrimaryButton
                            }

                            isLoading = true
                            viewModel.createInventaris(
                                currentUserId = sessionManager.idUser,
                                namaBarang = namaBarang,
                                kondisi = kondisi.ifBlank { "Baik" },
                                jumlah = jumlahBarang,
                                tanggal = tanggal,
                                fotoFile = file
                            ) { success, message ->
                                isLoading = false
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                if (success) navController.popBackStack()
                            }
                        }
                    )
                }
            }

            // ===== LOADING =====
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }

    // ===== IMAGE SOURCE DIALOG =====
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Pilih Sumber Foto") },
            text = { Text("Ambil foto baru atau pilih dari galeri") },
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
