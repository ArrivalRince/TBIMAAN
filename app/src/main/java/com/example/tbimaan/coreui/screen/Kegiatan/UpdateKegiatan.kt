package com.example.tbimaan.coreui.screen.Kegiatan

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.tbimaan.coreui.navigation.KEGIATAN_GRAPH_ROUTE
import com.example.tbimaan.coreui.utils.getTempUri
import com.example.tbimaan.coreui.utils.uriToFile
import com.example.tbimaan.model.SessionManager   // ✅ GANTI UserSession
import com.example.tbimaan.network.ApiClient
import com.example.tbimaan.network.KegiatanDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateKegiatanScreen(
    navController: NavController,
    idKegiatan: Int,
    onNavigate: (String) -> Unit,
    namaAwal: String,
    tanggalAwal: String,
    lokasiAwal: String,
    penanggungjawabAwal: String,
    deskripsiAwal: String,
    statusAwal: String,
    fotoAwal: String?
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) } // ✅ SESSION

    // ================= STATE =================
    var nama by remember { mutableStateOf(namaAwal) }
    var tanggal by remember { mutableStateOf(tanggalAwal) }
    var lokasi by remember { mutableStateOf(lokasiAwal) }
    var penanggungjawab by remember { mutableStateOf(penanggungjawabAwal) }
    var deskripsi by remember { mutableStateOf(deskripsiAwal) }
    var status by remember { mutableStateOf(statusAwal) }

    var statusExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("Akan Datang", "Selesai")

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // ================= IMAGE PICKER =================
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) imageUri = it
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) imageUri = tempUri
        }

    // ================= DATE PICKER =================
    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                tanggal = "%04d-%02d-%02d".format(year, month + 1, day)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            KegiatanBottomAppBar(
                onNavigate = onNavigate,
                currentRoute = KEGIATAN_GRAPH_ROUTE
            )
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {

                // ================= HEADER =================
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
                    text = "Update Data Kegiatan",
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

                        ExposedDropdownMenuBox(
                            expanded = statusExpanded,
                            onExpandedChange = { statusExpanded = !statusExpanded }
                        ) {
                            OutlinedTextField(
                                value = status,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Status") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                                },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = statusExpanded,
                                onDismissRequest = { statusExpanded = false }
                            ) {
                                statusOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            status = option
                                            statusExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // ================= FOTO =================
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF0F5F9))
                                .border(1.dp, Color(0xFFDDEEFF), RoundedCornerShape(16.dp))
                                .clickable { showDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                imageUri != null -> {
                                    AsyncImage(
                                        model = imageUri,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                !fotoAwal.isNullOrBlank() -> {
                                    AsyncImage(
                                        model = fotoAwal,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                else -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.AddPhotoAlternate,
                                            null,
                                            tint = Color(0xFF1E5B8A),
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text("Ubah Foto", color = Color(0xFF1E5B8A))
                                    }
                                }
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
                        text = if (isLoading) "Menyimpan..." else "Update",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val userId = sessionManager.idUser
                            if (userId == null) return@PrimaryButton

                            if (
                                nama.isBlank() ||
                                tanggal.isBlank() ||
                                lokasi.isBlank() ||
                                penanggungjawab.isBlank()
                            ) return@PrimaryButton

                            isLoading = true

                            fun text(v: String) =
                                v.toRequestBody("text/plain".toMediaTypeOrNull())

                            val file = imageUri?.let { uriToFile(context, it) }

                            val fotoPart = file?.let {
                                MultipartBody.Part.createFormData(
                                    "foto_kegiatan",
                                    it.name,
                                    it.asRequestBody("image/*".toMediaTypeOrNull())
                                )
                            }

                            ApiClient.instance.updateKegiatanMultipart(
                                id = idKegiatan.toString(),
                                idUser = text(userId.toString()),   // ✅ SESSION DIPAKAI
                                namaKegiatan = text(nama),
                                tanggalKegiatan = text(tanggal),
                                lokasi = lokasi.takeIf { it.isNotBlank() }?.let { text(it) },
                                penanggungjawab = penanggungjawab.takeIf { it.isNotBlank() }?.let { text(it) },
                                deskripsi = deskripsi.takeIf { it.isNotBlank() }?.let { text(it) },
                                statusKegiatan = text(status),
                                foto_kegiatan = fotoPart
                            ).enqueue(object : Callback<KegiatanDto> {
                                override fun onResponse(
                                    call: Call<KegiatanDto>,
                                    response: Response<KegiatanDto>
                                ) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        navController.popBackStack()
                                    }
                                }

                                override fun onFailure(call: Call<KegiatanDto>, t: Throwable) {
                                    isLoading = false
                                }
                            })
                        }
                    )
                }
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

    // ================= DIALOG FOTO =================
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Pilih Sumber Foto") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    val t = getTempUri(context)
                    tempUri = t
                    cameraLauncher.launch(t)
                }) { Text("Kamera") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    galleryLauncher.launch("image/*")
                }) { Text("Galeri") }
            }
        )
    }
}

// ================= BOTTOM BAR =================
@Composable
private fun KegiatanBottomAppBar(
    onNavigate: (String) -> Unit,
    currentRoute: String
) {
    Surface(color = Color(0xFFF8F8F8), shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavItem("Home", Icons.Default.Home, false) { onNavigate("home") }
            BottomNavItem("Inventaris", Icons.Outlined.Inventory, false) { onNavigate("inventaris_graph") }
            BottomNavItem("Kegiatan", Icons.Default.List, true) {}
            BottomNavItem("Keuangan", Icons.Outlined.Paid, false) { onNavigate("keuangan_graph") }
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) Color(0xFF1E5B8A) else Color.Gray
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = color)
        Text(label, fontSize = 11.sp, color = color)
    }
}