package com.example.tbimaan.coreui.screen.Home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Paid

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign // <<< IMPORT BARU YANG DIPERLUKAN
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tbimaan.R

// ... (Data class ModuleInfo biarkan sama)
data class ModuleInfo(
    val title: String,
    val description: String,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    onInventarisClick: () -> Unit,
    onKeuanganClick: () -> Unit,
    onKegiatanClick: () -> Unit
) {
    // ... (Isi fungsi HomeScreen biarkan sama)
    val context = LocalContext.current
    val modules = listOf(
        ModuleInfo(
            title = "Inventaris",
            description = "Modul inventaris berfungsi untuk mengelola data barang atau aset, mencakup pencatatan, pembaruan, dan pemantauan jumlah, kondisi, serta lokasi inventaris.",
            onClick = onInventarisClick
        ),
        ModuleInfo(
            title = "Keuangan",
            description = "Modul Keuangan berfungsi untuk membantu pengurus masjid dalam mencatat, mengelola & memantau seluruh transaksi keuangan masjid secara digital .",
            onClick = onKeuanganClick
        ),
        ModuleInfo(
            title = "Kegiatan",
            description = "Modul Kegiatan berfungsi untuk mengatur, mencatat, dan memantau jadwal serta pelaksanaan berbagai aktivitas masjid agar lebih terkelola dengan baik.",
            onClick = onKegiatanClick
        )
    )

    val backgroundColor = Color(0xFFE3F2FD)
    val cardColor = Color.White
    val textColorPrimary = Color(0xFF0D47A1)
    val textColorSecondary = Color(0xFF424242)

    Scaffold(
        bottomBar = {
            BottomNavBar(
                onInventarisClick = onInventarisClick,
                onKeuanganClick = onKeuanganClick,
                onKegiatanClick = onKegiatanClick
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp, bottom = innerPadding.calculateBottomPadding())
        ) {
            Image(
                painter = painterResource(id = R.drawable.masjid),
                contentDescription = "Latar Belakang Masjid",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { Toast.makeText(context, "Settings Clicked!", Toast.LENGTH_SHORT).show() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.3f), shape = RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.fillMaxHeight(0.25f))
                ImaanInfoCard(
                    textColorPrimary = textColorPrimary,
                    textColorSecondary = textColorSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    modules.forEach { module ->
                        ModuleCard(
                            moduleInfo = module,
                            modifier = Modifier.weight(1f),
                            cardColor = cardColor,
                            textColorPrimary = textColorPrimary,
                            textColorSecondary = textColorSecondary
                        )
                    }
                }
            }
        }
    }
}

// ... (ImaanInfoCard biarkan sama)
@Composable
fun ImaanInfoCard(textColorPrimary: Color, textColorSecondary: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "IMAAN",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary,
                )
                Divider(
                    modifier = Modifier.width(60.dp).padding(vertical = 4.dp),
                    thickness = 2.dp,
                    color = textColorPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "IMAAN merupakan aplikasi manajemen masjid sederhana yang dapat membantu pengurus dalam mengelola kegiatan, keuangan, inventaris, dan informasi secara digital, transparan, dan efisien.",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColorSecondary,
                    lineHeight = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = painterResource(id = R.drawable.logo_imaan),
                contentDescription = "Logo IMAAN",
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp))
            )
        }
    }
}


// ===== PERBAIKAN UTAMA ADA DI FUNGSI INI =====
@Composable
fun ModuleCard(moduleInfo: ModuleInfo, modifier: Modifier, cardColor: Color, textColorPrimary: Color, textColorSecondary: Color) {
    Card(
        modifier = modifier.clickable(onClick = moduleInfo.onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // 1. Column sekarang diatur agar item-item di dalamnya rata tengah secara horizontal
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // <<< DARI 'Start' MENJADI 'CenterHorizontally'
        ) {
            Text(
                text = moduleInfo.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColorPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 2. Text deskripsi juga dibuat rata tengah
            Text(
                text = moduleInfo.description,
                style = MaterialTheme.typography.bodySmall,
                color = textColorSecondary,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center // <<< TAMBAHAN UNTUK MEMASTIKAN TEKS RATA TENGAH
            )
        }
    }
}

// ... (BottomNavBar dan BottomNavItem biarkan sama)
@Composable
fun BottomNavBar(onInventarisClick: () -> Unit, onKeuanganClick: () -> Unit, onKegiatanClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(label = "Home", icon = Icons.Default.Home, onClick = {}, isSelected = true)
            BottomNavItem(label = "Inventaris", icon = Icons.Outlined.Inventory, onClick = onInventarisClick)
            BottomNavItem(label = "Kegiatan", icon = Icons.Default.List, onClick = onKegiatanClick)
            BottomNavItem(label = "Keuangan", icon = Icons.Outlined.Paid, onClick = onKeuanganClick)
        }
    }
}

@Composable
fun BottomNavItem(label: String, icon: ImageVector, onClick: () -> Unit, isSelected: Boolean = false) {
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp)
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
fun HomeScreenPreview() {
    HomeScreen(onInventarisClick = {}, onKeuanganClick = {}, onKegiatanClick = {})
}
