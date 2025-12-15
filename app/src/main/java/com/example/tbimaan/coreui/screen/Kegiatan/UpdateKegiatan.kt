package com.example.tbimaan.coreui.screen.Kegiatan

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.tbimaan.R
import com.example.tbimaan.coreui.navigation.KEGIATAN_GRAPH_ROUTE
import com.example.tbimaan.coreui.screen.Keuangan.getTempUri
import com.example.tbimaan.coreui.utils.uriToFile
import com.example.tbimaan.model.UserSession
import com.example.tbimaan.network.ApiClient
import com.example.tbimaan.network.KegiatanDto
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateKegiatanScreen(
    navController: NavController,
    idKegiatan: Int,
    onBackClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onNavigate: (String) -> Unit,
    namaAwal: String,
    tanggalAwal: String,
    waktuAwal: String,
    lokasiAwal: String,
    penceramahAwal: String,
    deskripsiAwal: String,
    statusAwal: String
) {

    // STATE INPUT
    var namaKegiatan by remember { mutableStateOf(namaAwal) }
    var tanggalKegiatan by remember { mutableStateOf(tanggalAwal) }
    var waktuKegiatan by remember { mutableStateOf(waktuAwal) }
    var lokasi by remember { mutableStateOf(lokasiAwal) }
    var penceramah by remember { mutableStateOf(penceramahAwal) }
    var deskripsi by remember { mutableStateOf(deskripsiAwal) }
    var status by remember { mutableStateOf(statusAwal) }

    var isStatusExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("Akan Datang", "Selesai")

    // FOTO
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUriHolder by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) imageUri = uri }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) imageUri = tempUriHolder
    }

    // DATE & TIME DIALOG
    val calendar = remember { Calendar.getInstance() }

    // API / UI state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            KegiatanBottomAppBar(
                onNavigate = onNavigate,
                currentRoute = KEGIATAN_GRAPH_ROUTE
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {

            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.masjiddua),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Perbarui Data Kegiatan",
                fontSize = 22.sp,
                color = Color(0xFF1E5B8A),
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            // CARD FORM PUTIH + SHADOW
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // NAMA
                    OutlinedTextField(
                        value = namaKegiatan,
                        onValueChange = { namaKegiatan = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nama Kegiatan") }
                    )

                    // TANGGAL
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val y = calendar.get(Calendar.YEAR)
                                val m = calendar.get(Calendar.MONTH)
                                val d = calendar.get(Calendar.DAY_OF_MONTH)

                                DatePickerDialog(
                                    context,
                                    { _, yy, mm, dd ->
                                        tanggalKegiatan = String.format(
                                            "%04d-%02d-%02d",
                                            yy,
                                            mm + 1,
                                            dd
                                        )
                                    },
                                    y, m, d
                                ).show()
                            }
                    ) {
                        OutlinedTextField(
                            value = tanggalKegiatan,
                            onValueChange = {},
                            enabled = false,
                            readOnly = true,
                            label = { Text("Tanggal (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // WAKTU
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val h = calendar.get(Calendar.HOUR_OF_DAY)
                                val mm = calendar.get(Calendar.MINUTE)

                                TimePickerDialog(
                                    context,
                                    { _, hh, minu ->
                                        waktuKegiatan = String.format("%02d:%02d WIB", hh, minu)
                                    },
                                    h, mm, true
                                ).show()
                            }
                    ) {
                        OutlinedTextField(
                            value = waktuKegiatan,
                            onValueChange = {},
                            enabled = false,
                            readOnly = true,
                            label = { Text("Waktu (HH:mm WIB)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // LOKASI
                    OutlinedTextField(
                        value = lokasi,
                        onValueChange = { lokasi = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Lokasi") }
                    )

                    // PENCERAMAH
                    OutlinedTextField(
                        value = penceramah,
                        onValueChange = { penceramah = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Penceramah") }
                    )

                    // DESKRIPSI
                    OutlinedTextField(
                        value = deskripsi,
                        onValueChange = { deskripsi = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        label = { Text("Deskripsi") }
                    )

                    // STATUS DROPDOWN
                    ExposedDropdownMenuBox(
                        expanded = isStatusExpanded,
                        onExpandedChange = { isStatusExpanded = !isStatusExpanded }
                    ) {

                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status") },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = isStatusExpanded
                                )
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = isStatusExpanded,
                            onDismissRequest = { isStatusExpanded = false }
                        ) {
                            statusOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        status = option
                                        isStatusExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // FOTO
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
                                    contentDescription = "",
                                    tint = Color(0xFF1E5B8A),
                                    modifier = Modifier.size(40.dp)
                                )
                                Text("Tambah Foto", color = Color(0xFF1E5B8A))
                            }
                        } else {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // BUTTONS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Hapus
                Button(
                    onClick = {
                        isLoading = true
                        ApiClient.instance.deleteKegiatan(idKegiatan.toString()).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                isLoading = false
                                if (response.isSuccessful) {
                                    coroutineScope.launch { snackbarHostState.showSnackbar("Kegiatan dihapus!") }
                                    onDeleteClick()
                                    navController.navigate("kegiatan_graph") {
                                        popUpTo("kegiatan_graph") { inclusive = true }
                                    }
                                } else {
                                    coroutineScope.launch { snackbarHostState.showSnackbar("Gagal hapus (kode ${response.code()})") }
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                isLoading = false
                                coroutineScope.launch { snackbarHostState.showSnackbar("Koneksi gagal: ${t.message}") }
                            }
                        })
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9534F)),
                    enabled = !isLoading
                ) {
                    Text("Hapus")
                }

                // Update
                Button(
                    onClick = {

                        if (
                            namaKegiatan.isBlank() ||
                            tanggalKegiatan.isBlank() ||
                            waktuKegiatan.isBlank() ||
                            lokasi.isBlank() ||
                            penceramah.isBlank()
                        ) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Harap isi semua field wajib.")
                            }
                            return@Button
                        }

                        val userId = UserSession.idUser ?: 1

                        // jika ada imageUri, coba ubah jadi File dan kirim nama file (backend saat ini tidak multipart)
                        val file: File? = try {
                            imageUri?.let { uriToFile(context, it) }
                        } catch (e: Exception) {
                            null
                        }

                        val payload = KegiatanDto(
                            id_kegiatan = idKegiatan,
                            id_user = userId,
                            nama_kegiatan = namaKegiatan,
                            tanggal_kegiatan = tanggalKegiatan,
                            waktu_kegiatan = waktuKegiatan,
                            lokasi = lokasi.ifBlank { null },
                            penceramah = penceramah.ifBlank { null },
                            deskripsi = deskripsi.ifBlank { null },
                            status_kegiatan = status,
                            foto_kegiatan = file?.name ?: null
                        )

                        isLoading = true
                        ApiClient.instance.updateKegiatan(idKegiatan.toString(), payload).enqueue(object : Callback<KegiatanDto> {
                            override fun onResponse(call: Call<KegiatanDto>, response: Response<KegiatanDto>) {
                                isLoading = false
                                if (response.isSuccessful) {
                                    coroutineScope.launch { snackbarHostState.showSnackbar("Kegiatan diperbarui!") }
                                    onUpdateClick()
                                    navController.navigate("kegiatan_graph") {
                                        popUpTo("kegiatan_graph") { inclusive = true }
                                    }
                                } else {
                                    coroutineScope.launch { snackbarHostState.showSnackbar("Gagal update (kode ${response.code()})") }
                                }
                            }

                            override fun onFailure(call: Call<KegiatanDto>, t: Throwable) {
                                isLoading = false
                                coroutineScope.launch { snackbarHostState.showSnackbar("Koneksi gagal: ${t.message}") }
                            }
                        })
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BC0DE)),
                    enabled = !isLoading
                ) {

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else Text("Update")
                }
            }
        }
    }

    // DIALOG FOTO
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Pilih Sumber Foto") },
            text = { Text("Ambil foto atau pilih dari galeri") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    val t = getTempUri(context)
                    tempUriHolder = t
                    cameraLauncher.launch(t)
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

// BOTTOM NAV BAR
@Composable
private fun KegiatanBottomAppBar(
    onNavigate: (String) -> Unit,
    currentRoute: String
) {

    Surface(shadowElevation = 8.dp, color = Color(0xFFF8F8F8)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
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
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val color = if (isSelected) Color(0xFF1E5B8A) else Color.Gray

    Column(
        modifier = Modifier
            .weight(1f)
            .height(64.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = "", tint = color)
        Text(label, color = color, fontSize = 11.sp)
    }
}


@Preview(showBackground = true)
@Composable
fun UpdateKegiatanScreenPreview() {
    UpdateKegiatanScreen(
        navController = rememberNavController(),
        idKegiatan = 1,
        onBackClick = {},
        onUpdateClick = {},
        onDeleteClick = {},
        onNavigate = {},
        namaAwal = "Kajian Jumat",
        tanggalAwal = "2025-11-15",
        waktuAwal = "20:00 WIB",
        lokasiAwal = "Aula Utama",
        penceramahAwal = "Ust. Ahmad",
        deskripsiAwal = "Kajian rutin.",
        statusAwal = "Akan Datang"
    )
}
