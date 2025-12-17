package com.example.tbimaan.coreui.screen.Home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tbimaan.R
import com.example.tbimaan.network.ApiClient
import com.example.tbimaan.network.RegisterRequest
import com.example.tbimaan.network.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SignUpScreen(
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isTermsChecked by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val primaryBlue = Color(0xFF007BFF)

    // =======================================================================
    // ===                 STRUKTUR LAYOUT DIPERBAIKI DI SINI              ===
    // =======================================================================
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. Gambar gelombang sebagai latar belakang
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Wave",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Tombol kembali di pojok kiri atas
        IconButton(
            onClick = onSignInClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(8.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.5f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = Color.White
            )
        }

        // 3. Column untuk konten yang bisa di-scroll (kartu form)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Spacer untuk memberi ruang di atas kartu agar logo tidak tertutup
            Spacer(modifier = Modifier.height(180.dp))

            // Kartu Sign Up
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    // Padding atas untuk memberi ruang bagi logo yang menjorok
                    modifier = Modifier.padding(top = 80.dp, start = 24.dp, end = 24.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Get started for free",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // -- Form Fields --
                    CustomTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = "First name"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = "Last name"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isVisible = isPasswordVisible,
                        onVisibilityChange = { isPasswordVisible = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomPasswordField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm password",
                        isVisible = isConfirmPasswordVisible,
                        onVisibilityChange = { isConfirmPasswordVisible = it }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // -- Checkbox --
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isTermsChecked = !isTermsChecked }
                    ) {
                        Checkbox(
                            checked = isTermsChecked,
                            onCheckedChange = { isTermsChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = primaryBlue, uncheckedColor = Color.Gray)
                        )
                        Text(
                            text = "I agree with the terms and conditions by creating an account",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // -- Tombol Sign Up --
                    Button(
                        onClick = {
                            if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (password != confirmPassword) {
                                Toast.makeText(context, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!isTermsChecked) {
                                Toast.makeText(context, "Anda harus menyetujui syarat dan ketentuan", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isLoading = true
                            val fullName = "$firstName $lastName"
                            val request = RegisterRequest(nama_masjid = fullName, email = email, password = password, alamat = "")

                            ApiClient.instance.registerUser(request).enqueue(object : Callback<RegisterResponse> {
                                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Registrasi berhasil! Silakan login.", Toast.LENGTH_LONG).show()
                                        onSignUpClick()
                                    } else {
                                        Toast.makeText(context, "Registrasi gagal (Error: ${response.code()})", Toast.LENGTH_LONG).show()
                                    }
                                }
                                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                                    isLoading = false
                                    Toast.makeText(context, "Koneksi ke server gagal: ${t.message}", Toast.LENGTH_LONG).show()
                                }
                            })
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Sign Up", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
            // Spacer di akhir untuk memastikan bisa di-scroll jika keyboard muncul
            Spacer(modifier = Modifier.height(32.dp))
        }

        // 4. Logo IMAAN yang melayang di atas segalanya
        //    Logo ini akan ditumpuk di atas Column dan Image gelombang.
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp) // Sesuaikan jarak dari atas
                .size(90.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_imaan),
                contentDescription = "App Logo",
                modifier = Modifier.size(50.dp)
            )
        }
        // =======================================================================
    }
}

// Composable terpisah untuk TextField standar
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

// Composable terpisah untuk TextField password
@Composable
private fun CustomPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5)
        ),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        trailingIcon = {
            val image = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { onVisibilityChange(!isVisible) }) {
                Icon(imageVector = image, contentDescription = "Toggle password visibility")
            }
        }
    )
}
