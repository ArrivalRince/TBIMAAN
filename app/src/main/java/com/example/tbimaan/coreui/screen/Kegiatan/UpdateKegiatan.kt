package com.example.tbimaan.coreui.screen.Kegiatan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavController
import com.example.tbimaan.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateKegiatanScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    namaAwal: String,
    tanggalAwal: String,
    waktuAwal: String,
    lokasiAwal: String,
    penceramahAwal: String,
    deskripsiAwal: String,
    statusAwal: String
) {
    var namaKegiatan by remember { mutableStateOf(namaAwal) }
    var tanggalKegiatan by remember { mutableStateOf(tanggalAwal) }
    var waktuKegiatan by remember { mutableStateOf(waktuAwal) }
    var lokasi by remember { mutableStateOf(lokasiAwal) }
    var penceramah by remember { mutableStateOf(penceramahAwal) }
    var deskripsi by remember { mutableStateOf(deskripsiAwal) }
    var status by remember { mutableStateOf(statusAwal) }
    var isStatusExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("Akan Datang", "Selesai")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .verticalScroll(rememberScrollState())
    ) {
        // 🔷 Header
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
                    .padding(12.dp)
                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(50))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Perbarui Data Kegiatan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF004AAD),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 🔷 Card Form
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
                // Form fields
                OutlinedTextField(
                    value = namaKegiatan,
                    onValueChange = { namaKegiatan = it },
                    label = { Text("Nama Kegiatan") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = tanggalKegiatan,
                    onValueChange = { tanggalKegiatan = it },
                    label = { Text("Tanggal") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = waktuKegiatan,
                    onValueChange = { waktuKegiatan = it },
                    label = { Text("Waktu") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = lokasi,
                    onValueChange = { lokasi = it },
                    label = { Text("Lokasi") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = penceramah,
                    onValueChange = { penceramah = it },
                    label = { Text("Penceramah") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi Kegiatan") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(8.dp)
                )

                // 🔷 Dropdown Status
                ExposedDropdownMenuBox(
                    expanded = isStatusExpanded,
                    onExpandedChange = { isStatusExpanded = !isStatusExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
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

                // 🔷 Upload Foto
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color(0xFFEAF2FF), RoundedCornerShape(12.dp))
                        .clickable { /* pilih foto */ },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = "Add Photo",
                            tint = Color(0xFF004AAD),
                            modifier = Modifier.size(36.dp)
                        )
                        Text("Tambah Foto", color = Color(0xFF004AAD))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔷 Tombol
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
                        onClick = { onBackClick() },
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

        Spacer(modifier = Modifier.height(20.dp))
    }
}