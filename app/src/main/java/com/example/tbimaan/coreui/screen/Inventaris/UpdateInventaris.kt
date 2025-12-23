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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.example.tbimaan.coreui.utils.getTempUri
import com.example.tbimaan.coreui.utils.uriToFile
import com.example.tbimaan.coreui.viewmodel.InventarisViewModel
import com.example.tbimaan.model.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateInventarisScreen(
    navController: NavController,
    id: String,
    viewModel: InventarisViewModel
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val item by viewModel.selectedItem
    val isLoadingInitialData by viewModel.isLoading

    var namaBarang by remember { mutableStateOf("") }
    var jumlahBarang by remember { mutableStateOf("") }
    var kondisi by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var existingImageUrl by remember { mutableStateOf<String?>(null) }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }

    var isProcessing by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempUriHolder by remember { mutableStateOf<Uri?>(null) }


    LaunchedEffect(key1 = id) {
        viewModel.getInventarisById(id)
    }


    LaunchedEffect(key1 = item) {
        item?.let { loadedItem ->
            namaBarang = loadedItem.namaBarang
            jumlahBarang = loadedItem.jumlah
            kondisi = loadedItem.kondisi
            existingImageUrl = loadedItem.urlFoto


            tanggal = if (loadedItem.originalDate != null) {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(loadedItem.originalDate)
            } else {

                ""
            }
        }
    }

    DisposableEffect(Unit) { onDispose { viewModel.clearSelectedItem() } }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) newImageUri = uri
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) newImageUri = tempUriHolder
        }

    val datePickerDialog = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, y, m, d -> tanggal = "%04d-%02d-%02d".format(y, m + 1, d) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Scaffold(containerColor = Color.White) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            if (isLoadingInitialData && item == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Box(Modifier.fillMaxWidth().height(180.dp)) {
                        Image(
                            painter = painterResource(R.drawable.masjid),
                            contentDescription = "Header",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        BackButtonOnImage(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.align(Alignment.TopStart)
                        )
                    }

                    Text(
                        "Perbarui Data Inventaris",
                        fontSize = 22.sp,
                        color = Color(0xFF1E5B8A),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(namaBarang, { namaBarang = it }, label = { Text("Nama Barang") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(jumlahBarang, { jumlahBarang = it }, label = { Text("Jumlah Barang") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(kondisi, { kondisi = it }, label = { Text("Kondisi") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(
                            tanggal, {},
                            label = { Text("Tanggal") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePickerDialog.show() },
                            readOnly = true,
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = { Icon(Icons.Default.DateRange, "Pilih Tanggal", Modifier.clickable { datePickerDialog.show() }) }
                        )


                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF0F5F9))
                                .border(1.dp, Color(0xFFDDEEFF), RoundedCornerShape(16.dp))
                                .clickable { showImageSourceDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = newImageUri ?: existingImageUrl,
                                contentDescription = "Foto Barang",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                error = painterResource(R.drawable.ic_launcher_background)
                            )
                            if (newImageUri == null && existingImageUrl.isNullOrEmpty()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AddPhotoAlternate, "Upload", tint = Color(0xFF1E5B8A).copy(0.8f), modifier = Modifier.size(40.dp))
                                    Text("Ubah Foto", color = Color(0xFF1E5B8A), fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))


                    Row(Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        PrimaryButton(
                            text = "Update",
                            onClick = {
                                val file = newImageUri?.let { uriToFile(context, it) }
                                isProcessing = true
                                viewModel.updateInventaris(
                                    id = id,
                                    currentUserId = sessionManager.idUser, // 🔥 WAJIB
                                    namaBarang = namaBarang,
                                    kondisi = kondisi,
                                    jumlah = jumlahBarang,
                                    tanggal = tanggal,
                                    fotoFile = file,
                                    context = context,
                                    onResult = { isSuccess, message ->
                                        isProcessing = false
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        if (isSuccess) navController.popBackStack()
                                    }
                                )

                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }


                if (isProcessing) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(enabled = false, onClick = {}),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }


            if (showImageSourceDialog) {
                AlertDialog(
                    onDismissRequest = { showImageSourceDialog = false },
                    title = { Text("Pilih Sumber Foto") },
                    text = { Text("Ambil foto baru atau pilih dari galeri?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showImageSourceDialog = false
                            val tempUri = getTempUri(context)
                            tempUriHolder = tempUri
                            cameraLauncher.launch(tempUri)
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