package com.example.tbimaan.coreui.screen.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tbimaan.R

@Composable
fun ProfileScreen(navController: NavController) {
    val titleColor = Color(0xFF5A96AB)
    val buttonColor = Color(0xFF5A96AB)

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER GAMBAR ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.masjid),
                    contentDescription = "Header Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Tombol Kembali
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

            // --- KONTEN OVERLAP ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-80).dp), // Geser ke atas
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // =================== PERBAIKAN FINAL DI SINI ===================
                // Hanya menampilkan gambar logo dengan ukuran yang ditentukan.
                Image(
                    painter = painterResource(id = R.drawable.logo_imaan),
                    contentDescription = "Logo IMAAN",
                    modifier = Modifier.size(130.dp) // Atur ukuran logo sesuai keinginan
                )
                // =============================================================

                Spacer(modifier = Modifier.height(16.dp))

                // --- JUDUL ---
                Text(
                    text = "PROFILE",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- KARTU INFORMASI ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        ProfileInfoItem(
                            icon = Icons.Default.Person,
                            label = "Nama Masjid",
                            value = "Husnul Khatimah"
                        )
                        Divider(color = Color.LightGray, thickness = 1.dp)
                        ProfileInfoItem(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = "husnulkhatimah@gmail.com"
                        )
                        Divider(color = Color.LightGray, thickness = 1.dp)
                        ProfileInfoItem(
                            icon = Icons.Default.LocationOn,
                            label = "Lokasi",
                            value = "Padang, Indonesia"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- TOMBOL EDIT PROFILE ---
                Button(
                    onClick = { navController.navigate("edit_profile") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(
                        "Edit Profile",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ... (Sisa file: ProfileInfoItem dan Preview tidak berubah)
@Composable
private fun ProfileInfoItem(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun ProfileScreenNewPreview() {
    ProfileScreen(navController = rememberNavController())
}
