package com.example.tbimaan.coreui.screen.Kegiatan

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.tbimaan.model.SessionManager
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateKegiatanScreen(
    navController: NavController,
    viewModel: KegiatanViewModel
) {
    // ================= FORM STATE =================
    var nama by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var penanggungjawab by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    val status = "Akan Datang"

    // ================= UI STATE =================
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var showImageDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // ================= IMAGE PICKER =================
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) imageUri = uri
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) imageUri = tempUri
        }

    // ================= DATE PICKER =================
    val calendar = Calendar.getInstance()

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                tanggal = "%04d-%02d-%02d".format(year, month + 1, day)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }
    }

    // ================= UI =================
    Scaffold(containerColor = Color.White) { padding ->
        Box(Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {

                // ===== HEADER =====
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.masjiddua),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    BackButtonOnImage(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "Tambah Data Kegiatan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E5B8A),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(Modifier.height(12.dp))

                // ===== FORM =====
                Card(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedTextField(
                            value = nama,
                            onValueChange = { nama = it },
                            label = { Text("Nama Kegiatan") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = tanggal,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tanggal (YYYY-MM-DD)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePickerDialog.show() },
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialog.show() }) {
                                    Icon(Icons.Default.DateRange, null)
                                }
                            }
                        )

                        OutlinedTextField(
                            value = lokasi,
                            onValueChange = { lokasi = it },
                            label = { Text("Lokasi") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = penanggungjawab,
                            onValueChange = { penanggungjawab = it },
                            label = { Text("Penanggungjawab") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = deskripsi,
                            onValueChange = { deskripsi = it },
                            label = { Text("Deskripsi") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )

                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // ===== FOTO =====
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF0F5F9))
                                .border(1.dp, Color(0xFFDDEEFF), RoundedCornerShape(16.dp))
                                .clickable { showImageDialog = true },
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
                                    Text("Tambah Foto", color = Color(0xFF1E5B8A))
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
                        text = if (isLoading) "Menyimpan..." else "Simpan",
                        modifier = Modifier.weight(1f),
                        onClick = {

                            val userId = sessionManager.idUser
                            val file = imageUri?.let {
                                try { uriToFile(context, it) } catch (e: Exception) { null }
                            }

                            if (userId == null) {
                                Toast.makeText(context, "Sesi tidak valid, silakan login ulang", Toast.LENGTH_LONG).show()
                                return@PrimaryButton
                            }

                            if (nama.isBlank() || tanggal.isBlank() || lokasi.isBlank() || penanggungjawab.isBlank()) {
                                Toast.makeText(context, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
                                return@PrimaryButton
                            }

                            if (file == null || !file.exists()) {
                                Toast.makeText(context, "Foto kegiatan wajib diisi", Toast.LENGTH_SHORT).show()
                                return@PrimaryButton
                            }

                            isLoading = true

                            viewModel.createKegiatanMultipart(
                                idUser = userId.toString(),
                                nama = nama,
                                tanggal = tanggal,
                                lokasi = lokasi,
                                penanggungjawab = penanggungjawab,
                                deskripsi = deskripsi,
                                status = status,
                                fotoFile = file,
                                context = context
                            ) { success, message ->
                                isLoading = false
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                if (success) navController.popBackStack()
                            }
                        }
                    )
                }

                Spacer(Modifier.height(32.dp))
            }

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
        }
    }

    if (showImageDialog) {
        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            title = { Text("Pilih Sumber Foto") },
            confirmButton = {
                TextButton(onClick = {
                    showImageDialog = false
                    val tmp = getTempUri(context)
                    tempUri = tmp
                    cameraLauncher.launch(tmp)
                }) { Text("Kamera") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageDialog = false
                    galleryLauncher.launch("image/*")
                }) { Text("Galeri") }
            }
        )
    }
}