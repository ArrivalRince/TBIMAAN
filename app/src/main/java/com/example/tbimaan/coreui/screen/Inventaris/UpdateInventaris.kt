package com.example.tbimaan.coreui.screen.Inventaris

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
import androidx.compose.material.icons.filled.* // <<< IMPORT BARU UNTUK IKON NAVBAR
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.example.tbimaan.coreui.navigation.INVENTARIS_GRAPH_ROUTE
import com.example.tbimaan.coreui.screen.Keuangan.getTempUri

@Composable
fun UpdateInventarisScreen(
    navController: NavController,
    itemId: String,
    onBackClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onNavigate: (String) -> Unit // <<< PERBAIKAN 1: TAMBAHKAN PARAMETER onNavigate
) {
    // ... (Logika state, launcher, dan data dummy tidak berubah)
    val namaAwal = "Loudspeaker Luar (TOA)"
    val jumlahAwal = "4"
    val kondisiAwal = "Baik"
    val tanggalAwal = "27/10/2025"

    var namaBarang by remember { mutableStateOf(namaAwal) }
    var jumlahBarang by remember { mutableStateOf(jumlahAwal) }
    var kondisi by remember { mutableStateOf(kondisiAwal) }
    var tanggal by remember { mutableStateOf(tanggalAwal) }
    val context = LocalContext.current

    LaunchedEffect(key1 = itemId) {
        Toast.makeText(context, "Mengedit item dengan ID: $itemId", Toast.LENGTH_SHORT).show()
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempUriHolder by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> if (uri != null) imageUri = uri }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) imageUri = tempUriHolder }
    )

    // ===== PERBAIKAN 2: GUNAKAN SCAFFOLD SEBAGAI LAYOUT UTAMA =====
    Scaffold(
        bottomBar = {
            InventarisBottomAppBar(
                onNavigate = onNavigate,
                currentRoute = INVENTARIS_GRAPH_ROUTE // Tandai bahwa kita masih di dalam modul inventaris
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()) // Gunakan padding dari scaffold
                .verticalScroll(rememberScrollState())
        ) {
            // ... (Seluruh isi Column tidak ada yang berubah)
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
                IconButton(
                    onClick = { onBackClick() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Perbarui Data Inventaris",
                fontSize = 22.sp,
                color = Color(0xFF1E5B8A),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                    OutlinedTextField(value = namaBarang, onValueChange = { namaBarang = it }, label = { Text("Nama Barang") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = jumlahBarang, onValueChange = { jumlahBarang = it }, label = { Text("Jumlah Barang") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = kondisi, onValueChange = { kondisi = it }, label = { Text("Kondisi") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = tanggal, onValueChange = { tanggal = it }, label = { Text("Tanggal") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))

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
                                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Upload Icon", tint = Color(0xFF1E5B8A).copy(alpha = 0.8f), modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Ubah Foto", color = Color(0xFF1E5B8A), fontWeight = FontWeight.SemiBold)
                            }
                        } else {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Foto Barang Terpilih",
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

// ===== PERBAIKAN 3: TAMBAHKAN KOMPONEN NAVBAR DI SINI =====
// (Dapat disalin dari file Inventaris lain agar konsisten)
@Composable
private fun InventarisBottomAppBar(onNavigate: (String) -> Unit, currentRoute: String) {
    Surface(shadowElevation = 8.dp, color = Color(0xFFF8F8F8)) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(label = "Home", icon = Icons.Default.Home, isSelected = false, onClick = { onNavigate("home") })
            BottomNavItem(label = "Inventaris", icon = Icons.Outlined.Inventory, isSelected = true, onClick = { /* Sedang di sini */ })
            BottomNavItem(label = "Kegiatan", icon = Icons.Default.List, isSelected = false, onClick = { onNavigate("kegiatan_graph") })
            BottomNavItem(label = "Keuangan", icon = Icons.Outlined.Paid, isSelected = false, onClick = { onNavigate("keuangan_graph") })
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
        Text(text = label, color = contentColor, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateInventarisScreenPreview() {
    UpdateInventarisScreen(
        navController = rememberNavController(),
        itemId = "preview_id",
        onBackClick = {},
        onUpdateClick = {},
        onDeleteClick = {},
        onNavigate = {} // Tambahkan untuk preview
    )
}
