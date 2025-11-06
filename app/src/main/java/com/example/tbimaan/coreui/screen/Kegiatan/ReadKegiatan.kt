package com.example.tbimaan.coreui.screen.Kegiatan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
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
            status = "Aktif",
            fotoResId = R.drawable.masjid
        )
    )

    var searchQuery by remember { mutableStateOf("") }
    val filteredList = kegiatanList.filter {
        it.nama.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = { AddFAB(onClick = onAddClick) },
        bottomBar = {
            KegiatanBottomAppBar(onNavigate = onNavigate, currentRoute = KEGIATAN_GRAPH_ROUTE)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            // Header Image
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

            // Page Title
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
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp,
                    modifier = Modifier.width(180.dp)
                )
            }

            // Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari Kegiatan", color = Color.Gray) },
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Search Icon",
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp)
                    )
                },
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .height(50.dp),
                singleLine = true
            )

            // List of Kegiatan
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
                Column(modifier = Modifier.fillMaxSize()) {
                    filteredList.forEach { kegiatan ->
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Foto Kegiatan
                                Image(
                                    painter = painterResource(id = kegiatan.fotoResId),
                                    contentDescription = "Foto Kegiatan",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                // Info Kegiatan
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = kegiatan.nama,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1E5B8A)
                                    )
                                    Text("Tanggal: ${kegiatan.tanggal}", fontSize = 14.sp)
                                    Text("Waktu: ${kegiatan.waktu}", fontSize = 14.sp)
                                    Text("Lokasi: ${kegiatan.lokasi}", fontSize = 14.sp)
                                    Text("Penceramah: ${kegiatan.penceramah}", fontSize = 14.sp)
                                    Text(
                                        "Deskripsi: ${kegiatan.deskripsi}",
                                        fontSize = 13.sp,
                                        color = Color.Gray,
                                        maxLines = 3
                                    )
                                    Text(
                                        "Status: ${kegiatan.status}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (kegiatan.status == "Aktif") Color(0xFF28A745) else Color.Red
                                    )

                                    // Tombol Edit
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        EditButton(
                                            onClick = {
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
                                        )
                                    }
                                }
                            }
                        }
                    }
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
            BottomNavItem("Home", Icons.Default.Home, currentRoute == "home") { onNavigate("home") }
            BottomNavItem("Inventaris", Icons.Outlined.Inventory, currentRoute == "inventaris_graph") { onNavigate("inventaris_graph") }
            BottomNavItem("Kegiatan", Icons.Default.List, currentRoute == "kegiatan_graph") { onNavigate("kegiatan_graph") }
            BottomNavItem("Keuangan", Icons.Outlined.Paid, currentRoute == "keuangan_graph") { onNavigate("keuangan_graph") }
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
        Text(
            text = label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReadKegiatanScreenPreview() {
    ReadKegiatanScreen(
        navController = rememberNavController(),
        onNavigate = {},
        onAddClick = {},
        onEditClick = {}
    )
}