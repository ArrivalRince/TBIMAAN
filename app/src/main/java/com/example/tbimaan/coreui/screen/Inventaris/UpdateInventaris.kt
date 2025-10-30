package com.example.tbimaan.coreui.screen.Inventaris

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tbimaan.R
import androidx.navigation.NavController

@Composable
fun UpdateInventarisScreen(
    onBackClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // State untuk form input
    var namaBarang by remember { mutableStateOf("") }
    var jumlahBarang by remember { mutableStateOf("") }
    var kondisi by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        // 🔷 Gambar masjid di bagian atas
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

            // Tombol kembali
            IconButton(
                onClick = { onBackClick() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(50))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔷 Judul halaman
        Text(
            text = "Perbarui Data Inventaris",
            fontSize = 20.sp,
            color = Color(0xFF004AAD),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 🔷 Card form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Input nama barang
                OutlinedTextField(
                    value = namaBarang,
                    onValueChange = { namaBarang = it },
                    label = { Text("Nama Barang") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // Input jumlah barang
                OutlinedTextField(
                    value = jumlahBarang,
                    onValueChange = { jumlahBarang = it },
                    label = { Text("Jumlah Barang") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // Input kondisi
                OutlinedTextField(
                    value = kondisi,
                    onValueChange = { kondisi = it },
                    label = { Text("Kondisi") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // Input tanggal
                OutlinedTextField(
                    value = tanggal,
                    onValueChange = { tanggal = it },
                    label = { Text("Tanggal") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // 🔷 Area upload foto
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFFEAF2FF), RoundedCornerShape(12.dp))
                        .clickable { /* TODO: implementasi pilih foto nanti */ },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Add Photo",
                            tint = Color(0xFF004AAD),
                            modifier = Modifier.size(36.dp)
                        )
                        Text("Tambah Foto", color = Color(0xFF004AAD))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔷 Tombol Update, Batal, dan Hapus
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { onUpdateClick() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004AAD)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Update")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { onBackClick() }, // ✅ kembali tanpa ubah
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF004AAD)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Batal")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onDeleteClick() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9534F)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Hapus")
                    }
                }
            }
        }
    }
}