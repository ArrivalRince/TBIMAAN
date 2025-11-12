package com.example.tbimaan.coreui.screen.Kegiatan

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateKegiatanScreen(
    navController: NavController,
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
    // State untuk input data
    var namaKegiatan by remember { mutableStateOf(namaAwal) }
    var tanggalKegiatan by remember { mutableStateOf(tanggalAwal) }
    var waktuKegiatan by remember { mutableStateOf(waktuAwal) }
    var lokasi by remember { mutableStateOf(lokasiAwal) }
    var penceramah by remember { mutableStateOf(penceramahAwal) }
    var deskripsi by remember { mutableStateOf(deskripsiAwal) }
    var status by remember { mutableStateOf(statusAwal) }
    var isStatusExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("Akan Datang", "Selesai")

    // State untuk upload foto
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempUriHolder by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    // Launcher galeri
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> if (uri != null) imageUri = uri }
    )

    // Launcher kamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) imageUri = tempUriHolder }
    )

    Scaffold(
        bottomBar = {
            KegiatanBottomAppBar(
                onNavigate = onNavigate,
                currentRoute = KEGIATAN_GRAPH_ROUTE
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // 🔷 Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.masjiddua),
                    contentDescription = "Masjid Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { onBackClick() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Perbarui Data Kegiatan",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E5B8A),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🔷 Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(value = namaKegiatan, onValueChange = { namaKegiatan = it }, label = { Text("Nama Kegiatan") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = tanggalKegiatan, onValueChange = { tanggalKegiatan = it }, label = { Text("Tanggal") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = waktuKegiatan, onValueChange = { waktuKegiatan = it }, label = { Text("Waktu") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = lokasi, onValueChange = { lokasi = it }, label = { Text("Lokasi") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = penceramah, onValueChange = { penceramah = it }, label = { Text("Penceramah") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = deskripsi, onValueChange = { deskripsi = it }, label = { Text("Deskripsi Kegiatan") }, modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(12.dp))

                    // 🔽 Dropdown Status
                    ExposedDropdownMenuBox(expanded = isStatusExpanded, onExpandedChange = { isStatusExpanded = !isStatusExpanded }) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = isStatusExpanded, onDismissRequest = { isStatusExpanded = false }) {
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

                    // 🔷 Upload Foto
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
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add Photo", tint = Color(0xFF1E5B8A).copy(alpha = 0.8f), modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Tambah Foto", color = Color(0xFF1E5B8A), fontWeight = FontWeight.SemiBold)
                            }
                        } else {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Foto Kegiatan",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { onDeleteClick() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9534F)), modifier = Modifier.weight(1f), shape = RoundedCornerShape(50)) {
                    Text("Hapus")
                }
                Button(onClick = { onUpdateClick() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BC0DE)), modifier = Modifier.weight(1f), shape = RoundedCornerShape(50)) {
                    Text("Update")
                }
            }
        }
    }

    // 🔷 Dialog pilih sumber gambar
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Pilih Sumber Foto") },
            text = { Text("Pilih dari Galeri atau ambil foto baru?") },
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

// 🔹 Bottom Nav Bar (sama seperti sebelumnya)
@Composable
private fun KegiatanBottomAppBar(onNavigate: (String) -> Unit, currentRoute: String) {
    Surface(shadowElevation = 8.dp, color = Color(0xFFF8F8F8)) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem("Home", Icons.Default.Home, false) { onNavigate("home") }
            BottomNavItem("Inventaris", Icons.Outlined.Inventory, false) { onNavigate("inventaris_graph") }
            BottomNavItem("Kegiatan", Icons.Default.List, true) { }
            BottomNavItem("Keuangan", Icons.Outlined.Paid, false) { onNavigate("keuangan_graph") }
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val contentColor = if (isSelected) Color(0xFF1E5B8A) else Color.Gray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.weight(1f).height(64.dp).clickable(onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = contentColor)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = contentColor, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateKegiatanScreenPreview() {
    UpdateKegiatanScreen(
        navController = rememberNavController(),
        onBackClick = {},
        onUpdateClick = {},
        onDeleteClick = {},
        onNavigate = {},
        namaAwal = "Kajian Malam Jumat",
        tanggalAwal = "15/11/2025",
        waktuAwal = "20.00 WIB",
        lokasiAwal = "Aula Utama",
        penceramahAwal = "Ust. Rafi",
        deskripsiAwal = "Kajian bulanan rutin.",
        statusAwal = "Akan Datang"
    )
}