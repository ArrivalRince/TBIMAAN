package com.example.tbimaan.coreui.screen.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun InfoAplikasiScreen(navController: NavController) {
    val titleColor = Color(0xFF0D47A1)
    val textColor = Color(0xFF616161)

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.masjid),
                    contentDescription = "Header Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(50))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-80).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // =================== PERBAIKAN FINAL DI SINI ===================
                        Image(
                            painter = painterResource(id = R.drawable.logo_imaan),
                            contentDescription = "Logo IMAAN",
                            modifier = Modifier.size(90.dp) // Atur ukuran logo
                        )
                        // =============================================================

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "IMAAN",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aplikasi ini dirancang untuk mendukung pengelolaan masjid secara modern sehingga aktivitas dapat berjalan lebih rapi dan terkoordinasi.",
                            fontSize = 14.sp,
                            color = textColor,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        InfoModuleItem(
                            title = "Modul Inventaris",
                            description = "Modul inventaris berfungsi untuk mengelola data barang atau aset, mencakup pencatatan, pembaruan, dan pemantauan jumlah, kondisi, serta lokasi inventaris.",
                            titleColor = titleColor,
                            textColor = textColor
                        )
                        Divider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))
                        InfoModuleItem(
                            title = "Modul Kegiatan",
                            description = "Modul Kegiatan berfungsi untuk membantu pengurus mengatur, mencatat, dan memantau jadwal serta pelaksanaan berbagai aktivitas masjid agar lebih terkelola dengan baik.",
                            titleColor = titleColor,
                            textColor = textColor
                        )
                        Divider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))
                        InfoModuleItem(
                            title = "Modul Keuangan",
                            description = "Modul Keuangan berfungsi untuk membantu pengurus masjid dalam mencatat, mengelola, dan memantau seluruh transaksi keuangan masjid secara digital dan terorganisir.",
                            titleColor = titleColor,
                            textColor = textColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ... (Sisa file: InfoModuleItem dan Preview tidak berubah)
@Composable
private fun InfoModuleItem(
    title: String,
    description: String,
    titleColor: Color,
    textColor: Color
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            fontSize = 14.sp,
            color = textColor,
            lineHeight = 20.sp
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun InfoAplikasiScreenPreview() {
    InfoAplikasiScreen(navController = rememberNavController())
}
