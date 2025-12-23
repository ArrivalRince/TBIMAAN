package com.example.tbimaan.coreui.screen.Home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.tbimaan.R
import com.example.tbimaan.model.SessionManager
import com.example.tbimaan.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController
) {
    val context = LocalContext.current
    // Gunakan SessionManager untuk proses Logout
    val sessionManager = remember { SessionManager(context) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFF5A96AB)
    val titleColor = Color(0xFF0D47A1)
    val cardColor = Color.White
    val screenBgColor = Color(0xFFF8F9FA)

    Scaffold(
        containerColor = screenBgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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

                Text(
                    text = "Pengaturan",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Search Bar tidak perlu diubah, biarkan seperti apa adanya
                var searchQuery by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari Pengaturan") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardColor,
                        unfocusedContainerColor = cardColor,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        SettingItem(
                            title = "Profile",
                            icon = Icons.Default.Person,
                            onClick = { navController.navigate("profile") }
                        )

                        Divider(modifier = Modifier.padding(horizontal = 16.dp))

                        SettingItem(
                            title = "Info Aplikasi",
                            icon = Icons.Default.Info,
                            onClick = { navController.navigate("info_aplikasi") }
                        )

                        Divider(modifier = Modifier.padding(horizontal = 16.dp))

                        SettingItem(
                            title = "Keamanan Akun",
                            icon = Icons.Default.Security,
                            onClick = { navController.navigate("keamanan_akun") }
                        )

                        Divider(modifier = Modifier.padding(horizontal = 16.dp))

                        SettingItem(
                            title = "Keluar",
                            icon = Icons.AutoMirrored.Filled.Logout,
                            onClick = { showLogoutDialog = true },
                            )
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        CustomLogoutDialog(
            onConfirm = {

                val userId = sessionManager.idUser
                

                if (userId != null) {
                    ApiClient.instance.deleteFcmToken(userId)
                        .enqueue(object : Callback<Map<String, String>> {
                            override fun onResponse(
                                call: Call<Map<String, String>>,
                                response: Response<Map<String, String>>
                            ) {
                                Log.d("FCM", "✅ FCM token dihapus dari backend")
                            }

                            override fun onFailure(
                                call: Call<Map<String, String>>,
                                t: Throwable
                            ) {
                                Log.e("FCM", "❌ Gagal hapus FCM token: ${t.message}")
                            }
                        })
                }
                

                sessionManager.logoutUser()
                showLogoutDialog = false
                

                navController.navigate("landing") {
                    popUpTo(0) { inclusive = true }
                }
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }
}

// Fungsi ini tidak perlu diubah
@Composable
private fun CustomLogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A96AB))
                        ) {
                            Text("Ya")
                        }
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9534F))
                        ) {
                            Text("Tidak")
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-30).dp)) {
                Box(
                    modifier = Modifier
                        .shadow(12.dp, CircleShape)
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD9534F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout Icon",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

// Fungsi ini tidak perlu diubah
@Composable
private fun SettingItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    contentColor: Color = LocalContentColor.current
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = contentColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, modifier = Modifier.weight(1f), color = contentColor)
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = contentColor.copy(alpha = 0.6f))
    }
}
