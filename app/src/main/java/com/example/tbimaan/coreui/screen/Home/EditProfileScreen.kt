package com.example.tbimaan.coreui.screen.Home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tbimaan.R

@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current

    var namaMasjid by remember { mutableStateOf("Husnul Khatimah") }
    var email by remember { mutableStateOf("husnulkhatimah@gmail.com") }
    var lokasi by remember { mutableStateOf("Padang, Indonesia") }

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
                    .offset(y = (-80).dp),
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
                Text(
                    text = "EDIT PROFILE",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(24.dp))

                // --- FORMULIR EDIT ---
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
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        EditProfileTextField(label = "Nama", value = namaMasjid, onValueChange = { namaMasjid = it })
                        EditProfileTextField(label = "Email", value = email, onValueChange = { email = it })
                        EditProfileTextField(label = "Lokasi", value = lokasi, onValueChange = { lokasi = it })
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- TOMBOL SIMPAN ---
                Button(
                    onClick = {
                        Toast.makeText(context, "Perubahan disimpan!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(
                        "Simpan",
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

// ... (Sisa file: EditProfileTextField dan Preview tidak berubah)
@Composable
private fun EditProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0D47A1),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0F5F9),
                unfocusedContainerColor = Color(0xFFF0F5F9),
                focusedBorderColor = Color(0xFF5A96AB),
                unfocusedBorderColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun EditProfileScreenNewPreview() {
    EditProfileScreen(navController = rememberNavController())
}
