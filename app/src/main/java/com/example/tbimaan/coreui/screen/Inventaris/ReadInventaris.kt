package com.example.tbimaan.coreui.screen.Inventaris

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
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
import com.example.tbimaan.coreui.navigation.INVENTARIS_GRAPH_ROUTE

data class InventarisEntry(
    val id: String,
    val nama: String,
    val jumlah: Int,
    val kondisi: String,
    val tanggal: String,
    @DrawableRes val imageRes: Int
)

@Composable
fun ReadInventarisScreen(
    navController: NavController,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val inventarisList = remember {
        listOf(
            InventarisEntry("INV001", "Loudspeaker Luar (TOA)", 4, "Baik", "27/10/2025", R.drawable.toa),
            InventarisEntry("INV002", "Karpet Sajadah", 20, "Baik", "15/01/2024", R.drawable.karpet),
            InventarisEntry("INV003", "AC Split 2 PK", 3, "Perlu Servis", "10/06/2023", R.drawable.ac),
            InventarisEntry("INV004", "Proyektor InFocus", 1, "Baik", "05/03/2024", R.drawable.proyektor),
            InventarisEntry("INV005", "Mimbar Khutbah", 1, "Baik", "01/01/2022", R.drawable.mimbar)
        )
    }

    val filteredList = if (searchQuery.isBlank()) {
        inventarisList
    } else {
        inventarisList.filter {
            it.nama.contains(searchQuery, ignoreCase = true) ||
                    it.kondisi.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        floatingActionButton = {
            AddFAB(onClick = onAddClick)
        },
        bottomBar = {
            InventarisBottomAppBar(
                onNavigate = onNavigate,
                currentRoute = INVENTARIS_GRAPH_ROUTE
            )
        },
        // PERBAIKAN: Gunakan Column sebagai wrapper untuk layout yang tidak scrollable
        // dan LazyColumn untuk yang scrollable
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Terapkan padding dari Scaffold di sini
                .background(Color(0xFFF9F9F9))
        ) {
            // Bagian Header (Gambar) - Tidak perlu di-scroll
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
                BackButtonOnImage(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            // Bagian Search Bar - Tidak perlu di-scroll
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                placeholder = { Text("Cari barang...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF1E5B8A),
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )

            // Bagian Daftar (Scrollable)
            LazyColumn(
                modifier = Modifier.fillMaxSize() // LazyColumn sekarang mengisi sisa ruang
            ) {
                items(filteredList, key = { it.id }) { inventarisItem ->
                    InventarisItemCard(
                        item = inventarisItem,
                        onEditClick = { onEditClick(inventarisItem.id) }
                    )
                }
            }
        }
    }
}

// Composable untuk Card Item (sudah benar, tidak perlu diubah)
@Composable
fun InventarisItemCard(item: InventarisEntry, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nama,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Jumlah: ${item.jumlah}", fontSize = 14.sp, color = Color.Gray)
                Text("Kondisi: ${item.kondisi}", fontSize = 14.sp, color = Color.Gray)
                Text("Tanggal: ${item.tanggal}", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                EditButton(onClick = onEditClick)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.nama,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}


// --- Komponen BottomAppBar dan Preview tidak berubah ---

@Composable
private fun InventarisBottomAppBar(onNavigate: (String) -> Unit, currentRoute: String) {
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

@Preview(showBackground = true)
@Composable
fun ReadInventarisScreenPreview() {
    ReadInventarisScreen(
        navController = rememberNavController(),
        onNavigate = {},
        onAddClick = {},
        onEditClick = {}
    )
}
