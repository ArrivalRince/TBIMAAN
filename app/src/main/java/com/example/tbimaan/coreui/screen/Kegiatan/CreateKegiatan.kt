package com.example.tbimaan.coreui.screen.Kegiatan

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.DateRange
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
import java.io.File
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateKegiatanScreen(
    navController: NavController,
    viewModel: KegiatanViewModel
) {
    // ================= STATE FORM =================
    var nama by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var penanggungjawab by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    val status = "Akan Datang"

    // ================= STATE UI =================
    val context = LocalContext.current
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
    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, y, m, d ->
                tanggal = "%04d-%02d-%02d".format(y, m + 1, d)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Scaffold(containerColor = Color.White) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {

                // ================= HEADER =================
                Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
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
                    text = "Tambah Data Kegiatan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E5B8A),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(Modifier.height(12.dp))

                // ================= FORM =================
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

                        // ================= FOTO =================
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

                // ================= BUTTON =================
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
                            val file: File? = try {
                                imageUri?.let { uriToFile(context, it) }
                            } catch (e: Exception) {
                                null
                            }

                            if (
                                nama.isBlank() ||
                                tanggal.isBlank() ||
                                lokasi.isBlank() ||
                                penanggungjawab.isBlank()
                            ) {
                                Toast.makeText(
                                    context,
                                    "Semua field wajib diisi",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@PrimaryButton
                            }

                            if (file == null || !file.exists()) {
                                Toast.makeText(
                                    context,
                                    "Foto kegiatan wajib diisi",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@PrimaryButton
                            }

                            isLoading = true

                            viewModel.createKegiatanMultipart(
                                idUser = UserSession.idUser.toString(),
                                nama = nama,
                                tanggal = tanggal,
                                lokasi = lokasi,
                                penanggungjawab = penanggungjawab,
                                deskripsi = deskripsi,
                                status = status,
                                fotoFile = file
                            ) { success, message ->
                                isLoading = false
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) navController.popBackStack()
                            }
                        }
                    )
                }
            }

            // ================= LOADING =================
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

            // ================= DIALOG FOTO =================
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
    }
}