package com.example.tbimaan.coreui.screen.Home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tbimaan.R
import com.example.tbimaan.model.SessionManager
import com.example.tbimaan.network.ApiClient
import com.example.tbimaan.network.LoginRequest
import com.example.tbimaan.network.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SignInScreen(
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val primaryBlue = Color(0xFF007BFF)
    val lightGray = Color(0xFFF5F5F5)
    val linkColor = Color(0xFF0062CC)


    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(180.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 80.dp, start = 24.dp, end = 24.dp, bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Menjaga amanah dan privasi dengan penuh keimanan",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            unfocusedBorderColor = lightGray,
                            unfocusedContainerColor = lightGray,
                            focusedContainerColor = lightGray
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryBlue,
                            unfocusedBorderColor = lightGray,
                            unfocusedContainerColor = lightGray,
                            focusedContainerColor = lightGray
                        ),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        trailingIcon = {
                            val image = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isLoading = true
                            val request = LoginRequest(email = email, password = password)
                            Log.d("SignIn", "Request: $request")

                            ApiClient.instance.loginUser(request)
                                .enqueue(object : Callback<LoginResponse> {
                                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                        isLoading = false
                                        Log.d("SignIn", "HTTP ${response.code()} body=${response.body()}")

                                        if (response.isSuccessful) {
                                            val loginResponse = response.body()
                                            val user = loginResponse?.user

                                            if (user != null) {
                                                // Pemanggilan ini sekarang aman karena SessionManager sudah diubah
                                                sessionManager.createLoginSession(
                                                    idUser = user.id_user,
                                                    namaMasjid = user.nama_masjid,
                                                    email = user.email,
                                                    alamat = user.alamat // <-- Mengirim nilai null sekarang tidak akan crash
                                                )

                                                Log.d("SignInScreen", "Login Berhasil. Sesi untuk User ID: ${user.id_user} telah disimpan.")
                                                Toast.makeText(context, "Selamat datang ${user.nama_masjid}", Toast.LENGTH_SHORT).show()
                                                onSignInClick()
                                            } else {
                                                Toast.makeText(context, "Login gagal: Data pengguna tidak valid.", Toast.LENGTH_LONG).show()
                                                Log.e("SignIn", "Login sukses tapi data user null.")
                                            }
                                        } else {
                                            val errorMsg = when (response.code()) {
                                                401 -> "Email atau password salah."
                                                else -> "Login gagal (Error: ${response.code()})"
                                            }
                                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                            Log.e("SignIn", "Login gagal: code=${response.code()} err=${response.errorBody()?.string()}")
                                        }
                                    }

                                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                        isLoading = false
                                        Log.e("SignIn", "onFailure: ${t.message}", t)
                                        Toast.makeText(context, "Gagal koneksi ke server: ${t.message}", Toast.LENGTH_LONG).show()
                                    }
                                })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("Sign In", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // =======================================================================
                    // ===                 PERBAIKAN UTAMA DAN FINAL ADA DI SINI           ===
                    // =======================================================================
                    Text(
                        modifier = Modifier.clickable(onClick = onSignUpClick),
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Gray)) {
                                append("Tidak punya akun? ")
                            }
                            withStyle(style = SpanStyle(color = linkColor, fontWeight = FontWeight.Bold)) {
                                // Mengubah "Daftar" menjadi "Sign Up"
                                append("Sign Up")
                            }
                        }
                    )
                    // =======================================================================
                }
            }
            // Spacer di akhir untuk memastikan bisa di-scroll
            Spacer(modifier = Modifier.height(32.dp))
        }

        Image(
            painter = painterResource(id = R.drawable.logo_imaan),
            contentDescription = "App Logo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
                .size(120.dp)
        )
    }
}
