package com.example.tbimaan.coreui.screen.Kegiatan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// PERBAIKAN 1: Import ikon Search dari material icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tbimaan.R
import com.example.tbimaan.coreui.components.AddFAB
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.coreui.components.EditButton
import com.example.tbimaan.coreui.navigation.KEGIATAN_GRAPH_ROUTE
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class Kegiatan(
    val nama: String,
    val tanggal: String,
    val waktu: String,
    val lokasi: String,
    val penceramah: String,
    val deskripsi: String,
    val status: String,
    val fotoResId: Int
)

@Composable
fun ReadKegiatanScreen(
    navController: NavController,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val kegiatanList = listOf(
        Kegiatan(
            nama = "Kerja Bakti Mingguan",
            tanggal = "02/11/2025",
            waktu = "07.00 WIB",
            lokasi = "Halaman Masjid",
            penceramah = "Ust. Fajar",
            deskripsi = "Membersihkan area wudhu dan halaman masjid secara gotong royong.",
            status = "Selesai",
            fotoResId = R.drawable.baktikerja
        ),
        Kegiatan(
            nama = "Kajian Bulanan",
            tanggal = "15/11/2025",
            waktu = "20.00 WIB",
            lokasi = "Aula Utama",
            penceramah = "Ust. Rafi",
            deskripsi = "Kajian rutin bulanan dengan topik akhlak mulia.",
            status = "Akan Datang",
            fotoResId = R.drawable.kajianbulanan
        )
    )

    var searchQuery by remember { mutableStateOf("") }
    val filteredList = kegiatanList.filter {
        it.nama.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            AddFAB(onClick = onAddClick)
        },
        floatingActionButtonPosition = FabPosition.End,
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
                .padding(innerPadding)
                .background(Color(0xFFF9F9F9))
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.masjid),
                    contentDescription = "Header Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                BackButtonOnImage(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            // Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "DAFTAR KEGIATAN",
                    color = Color(0xFF1E5B8A),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp,
                    modifier = Modifier.width(180.dp)
                )
            }

            // PERBAIKAN 2: Ganti Search Bar agar sama seperti di ReadInventaris.kt
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                placeholder = { Text("Cari kegiatan...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                shape = RoundedCornerShape(24.dp), // Bentuk sudut yang sama
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF1E5B8A),
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )
            // ================================================================


            // List Scrollable
            if (filteredList.isEmpty()) {
                Text(
                    text = "Tidak ada kegiatan ditemukan.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(filteredList) { kegiatan ->
                        KegiatanCard(kegiatan = kegiatan) {
                            val namaEnc = URLEncoder.encode(kegiatan.nama, StandardCharsets.UTF_8.toString())
                            val tanggalEnc = URLEncoder.encode(kegiatan.tanggal, StandardCharsets.UTF_8.toString())
                            val waktuEnc = URLEncoder.encode(kegiatan.waktu, StandardCharsets.UTF_8.toString())
                            val lokasiEnc = URLEncoder.encode(kegiatan.lokasi, StandardCharsets.UTF_8.toString())
                            val pjEnc = URLEncoder.encode(kegiatan.penceramah, StandardCharsets.UTF_8.toString())
                            val deskripsiEnc = URLEncoder.encode(kegiatan.deskripsi, StandardCharsets.UTF_8.toString())
                            val statusEnc = URLEncoder.encode(kegiatan.status, StandardCharsets.UTF_8.toString())

                            navController.navigate(
                                "update_kegiatan/$namaEnc/$tanggalEnc/$waktuEnc/$lokasiEnc/$pjEnc/$deskripsiEnc/$statusEnc"
                            )
                        }
                    }
                }
            }
        }
    }
}

// ... (Sisa file tidak ada perubahan, KegiatanCard dan BottomAppBar biarkan apa adanya)
@Composable
fun KegiatanCard(kegiatan: Kegiatan, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        ) {
            // Gambar kegiatan
            Image(
                painter = painterResource(id = kegiatan.fotoResId),
                contentDescription = "Foto Kegiatan",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            )

            // Konten teks
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                // Nama kegiatan + status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = kegiatan.nama.uppercase(), // huruf besar semua
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color(0xFF1E5B8A),
                        modifier = Modifier.weight(1f)
                    )

                    val (bgColor, textColor) = when (kegiatan.status) {
                        "Selesai" -> Color(0xFF4CAF50) to Color.White
                        "Akan Datang" -> Color(0xFFE53935) to Color.White
                        else -> Color(0xFF9E9E9E) to Color.White
                    }

                    Box(
                        modifier = Modifier
                            .background(bgColor, shape = RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = kegiatan.status,
                            color = textColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Spacer kecil biar nama agak terpisah dari info
                Spacer(modifier = Modifier.height(1.dp))

                // Info kegiatan (super rapat)
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text("Tanggal: ${kegiatan.tanggal}", fontSize = 12.sp)
                    Text("Waktu: ${kegiatan.waktu}", fontSize = 12.sp)
                    Text("Lokasi: ${kegiatan.lokasi}", fontSize = 12.sp)
                    Text("Penceramah: ${kegiatan.penceramah}", fontSize = 12.sp)
                    Text(
                        kegiatan.deskripsi,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 3
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    EditButton(onClick = onEditClick)
                }
            }
        }
    }
}

@Composable
private fun KegiatanBottomAppBar(onNavigate: (String) -> Unit, currentRoute: String) {
    Surface(shadowElevation = 8.dp, color = Color(0xFFF8F8F8)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(label = "Home", icon = Icons.Default.Home, isSelected = false, onClick = { onNavigate("home") })
            BottomNavItem(label = "Inventaris", icon = Icons.Outlined.Inventory, isSelected = false, onClick = { onNavigate("inventaris_graph") })
            BottomNavItem(label = "Kegiatan", icon = Icons.Default.List, isSelected = true, onClick = { /* Sedang di sini */ })
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
        modifier = Modifier
            .weight(1f)
            .height(64.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = contentColor)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = contentColor, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun ReadKegiatanScreenPreview() {
    ReadKegiatanScreen(
        navController = rememberNavController(),
        onNavigate = {},
        onAddClick = {},
        onEditClick = {}
    )
}