package com.example.tbimaan.coreui.screen.Home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tbimaan.R

@Composable
fun KeamananAkunScreen(navController: NavController) {
    val context = LocalContext.current

    var passwordLama by remember { mutableStateOf("") }
    var passwordBaru by remember { mutableStateOf("") }
    var konfirmasiPassword by remember { mutableStateOf("") }

    var isPasswordLamaVisible by remember { mutableStateOf(false) }
    var isPasswordBaruVisible by remember { mutableStateOf(false) }
    var isKonfirmasiPasswordVisible by remember { mutableStateOf(false) }

    val titleColor = Color(0xFF5A96AB)
    val buttonColor = Color(0xFFC97B7B)

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-80).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.logo_imaan),
                    contentDescription = "Logo IMAAN",
                    modifier = Modifier.size(130.dp) // Atur ukuran logo
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "UBAH PASSWORD",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PasswordTextField(
                        label = "Password Lama",
                        value = passwordLama,
                        onValueChange = { passwordLama = it },
                        isVisible = isPasswordLamaVisible,
                        onVisibilityChange = { isPasswordLamaVisible = !isPasswordLamaVisible }
                    )
                    PasswordTextField(
                        label = "Password Baru",
                        value = passwordBaru,
                        onValueChange = { passwordBaru = it },
                        isVisible = isPasswordBaruVisible,
                        onVisibilityChange = { isPasswordBaruVisible = !isPasswordBaruVisible }
                    )
                    PasswordTextField(
                        label = "Konfirmasi Password",
                        value = konfirmasiPassword,
                        onValueChange = { konfirmasiPassword = it },
                        isVisible = isKonfirmasiPasswordVisible,
                        onVisibilityChange = { isKonfirmasiPasswordVisible = !isKonfirmasiPasswordVisible }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        if (passwordLama.isBlank() || passwordBaru.isBlank() || konfirmasiPassword.isBlank()) {
                            Toast.makeText(context, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                        } else if (passwordBaru != konfirmasiPassword) {
                            Toast.makeText(context, "Password baru dan konfirmasi tidak cocok!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Password berhasil diupdate!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(
                        text = "Update Password",
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

@Composable
private fun PasswordTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onVisibilityChange: () -> Unit
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
            singleLine = true,
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val icon = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = onVisibilityChange) {
                    Icon(imageVector = icon, contentDescription = "Toggle visibility")
                }
            }
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun KeamananAkunScreenPreview() {
    KeamananAkunScreen(navController = rememberNavController())
}
