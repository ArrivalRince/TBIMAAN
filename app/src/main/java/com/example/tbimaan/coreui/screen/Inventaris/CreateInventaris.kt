package com.example.tbimaan.coreui.screen.Inventaris

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.coreui.components.PrimaryButton
import com.example.tbimaan.coreui.components.SecondaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInventarisScreen(
    navController: NavController,
    onNavigate: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    var namaBarang by remember { mutableStateOf("") }
    var jumlahBarang by remember { mutableStateOf("") }
    var kondisi by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            InventarisBottomAppBar(
                onNavigate = onNavigate,
                currentRoute = "inventaris_graph"
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.masjid),
                    contentDescription = "Header Masjid",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // PANGGIL KOMPONEN TOMBOL BARU
                BackButtonOnImage(
                    onClick = onCancel,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            Text(
                text = "Tambahkan Data Inventaris Baru",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E5B8A),
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = namaBarang,
                    onValueChange = { namaBarang = it },
                    label = { Text("Nama Barang") },
                    modifier = Modifier.fillMaxWidth()
                )
                // ... (TextFields lain biarkan sama)
                OutlinedTextField(value = jumlahBarang, onValueChange = { jumlahBarang = it }, label = { Text("Jumlah Barang") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = kondisi, onValueChange = { kondisi = it }, label = { Text("Kondisi (contoh: Baik, Rusak)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = tanggal, onValueChange = { tanggal = it }, label = { Text("Tanggal Pembelian (DD/MM/YY)") }, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.weight(1f))

            // PANGGIL KOMPONEN TOMBOL BARU
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecondaryButton(
                    text = "Batal",
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = "Simpan",
                    onClick = onSave,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ... (Sisa file CreateInventaris.kt seperti InventarisBottomAppBar, BottomNavItem, dan Preview biarkan sama persis)
@Composable
private fun InventarisBottomAppBar(onNavigate: (String) -> Unit, currentRoute: String) {
    Surface(shadowElevation = 8.dp, color = Color(0xFFF8F8F8)) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(label = "Home", icon = Icons.Default.Home, isSelected = currentRoute == "home", onClick = { onNavigate("home") })
            BottomNavItem(label = "Inventaris", icon = Icons.Outlined.Inventory, isSelected = currentRoute == "inventaris_graph", onClick = { /* ... */ })
            BottomNavItem(label = "Kegiatan", icon = Icons.Default.List, isSelected = currentRoute == "kegiatan_graph", onClick = { onNavigate("kegiatan_graph") })
            BottomNavItem(label = "Keuangan", icon = Icons.Outlined.Paid, isSelected = currentRoute == "keuangan_graph", onClick = { onNavigate("keuangan_graph") })
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

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun CreateInventarisScreenPreview() {
    CreateInventarisScreen(
        navController = rememberNavController(),
        onNavigate = {},
        onSave = {},
        onCancel = {}
    )
}
