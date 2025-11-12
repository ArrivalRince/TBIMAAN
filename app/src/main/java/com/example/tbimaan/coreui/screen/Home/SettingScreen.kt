package com.example.tbimaan.coreui.screen.Home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tbimaan.R

@Composable
fun SettingScreen(
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    var showLogoutDialog by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFF5A96AB)
    val titleColor = Color(0xFF0D47A1)
    val cardColor = Color.White
    val screenBgColor = Color(0xFFF8F9FA)

    Scaffold(
        bottomBar = {
            BottomNavBar(
                onInventarisClick = { navController.navigate("inventaris_graph") },
                onKeuanganClick = { navController.navigate("keuangan_graph") },
                onKegiatanClick = { navController.navigate("kegiatan_graph") },
                onHomeClick = { navController.navigate("home") }
            )
        },
        containerColor = screenBgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .background(screenBgColor)
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
                    .padding(horizontal = 24.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-30).dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { /* Aksi Tombol IMAAN */ },
                        modifier = Modifier.height(60.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = "     IMAAN     ",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .shadow(8.dp, CircleShape)
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(cardColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_imaan),
                            contentDescription = "Logo IMAAN",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }

                //JUDUL PENGATURAN & SEARCH BAR
                Text(text = "Pengaturan", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = titleColor, modifier = Modifier.padding(bottom = 16.dp))
                OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Cari Pengaturan") }, leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon") }, shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = cardColor, unfocusedContainerColor = cardColor, focusedBorderColor = primaryColor, unfocusedBorderColor = Color.LightGray), singleLine = true)
                Spacer(modifier = Modifier.height(24.dp))

                // menu
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        SettingItem(title = "Profile", icon = Icons.Default.Person, onClick = { Toast.makeText(context, "Profile clicked", Toast.LENGTH_SHORT).show() })
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        SettingItem(title = "Info Aplikasi", icon = Icons.Default.Info, onClick = { Toast.makeText(context, "Info Aplikasi clicked", Toast.LENGTH_SHORT).show() })
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        SettingItem(title = "Keamanan Akun", icon = Icons.Default.Security, onClick = { Toast.makeText(context, "Keamanan Akun clicked", Toast.LENGTH_SHORT).show() })
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        SettingItem(
                            title = "Keluar",
                            icon = Icons.AutoMirrored.Filled.Logout,
                            onClick = { showLogoutDialog = true } // Aksi tetap sama
                        )
                    }
                }
            }
        }
    }

    // memanggil Dialog Kustom, bukan AlertDialog standar
    if (showLogoutDialog) {
        CustomLogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                navController.navigate("signin") {
                    popUpTo(0) { inclusive = true }
                }
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }
}

@Composable
private fun CustomLogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Agar bisa custom lebar
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp), // Beri jarak dari tepi layar
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(top = 48.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Keluar",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Apakah anda yakin ingin keluar?",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A96AB)) // Warna biru-abu
                        ) {
                            Text("Ya")
                        }
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9534F)) // Warna merah
                        ) {
                            Text("Tidak")
                        }
                    }
                }
            }
            // Ikon Logout di atas Card
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-30).dp)) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFCE4E4)), // Latar belakang pink muda
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout Icon",
                            tint = Color(0xFFD9534F), // Ikon merah
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun SettingItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = Color.Gray, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray, modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Go to $title", tint = Color.Gray)
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun SettingScreenWithDialogPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CustomLogoutDialog(onConfirm = {}, onDismiss = {})
    }
    // }
}