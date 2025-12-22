package com.example.tbimaan.coreui.screen.Home

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.tbimaan.network.FcmTokenRequest
import com.example.tbimaan.network.LoginRequest
import com.example.tbimaan.network.LoginResponse
import com.google.firebase.messaging.FirebaseMessaging
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

    fun goToApp(namaMasjid: String) {
        Toast.makeText(context, "Selamat datang $namaMasjid", Toast.LENGTH_SHORT).show()
        isLoading = false
        onSignInClick()
    }

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
                        visualTransformation = if (isPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        trailingIcon = {
                            val image =
                                if (isPasswordVisible) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff

                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (isLoading) return@Button

                            val emailTrim = email.trim()
                            val passwordTrim = password

                            if (emailTrim.isBlank() || passwordTrim.isBlank()) {
                                Toast.makeText(context, "Email dan password tidak boleh kosong.", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            if (!Patterns.EMAIL_ADDRESS.matcher(emailTrim).matches()) {
                                Toast.makeText(context, "Format email tidak valid.", Toast.LENGTH_LONG).show()
                                return@Button
                            }

                            isLoading = true

                            val request = LoginRequest(email = emailTrim, password = passwordTrim)

                            ApiClient.instance.loginUser(request)
                                .enqueue(object : Callback<LoginResponse> {

                                    override fun onResponse(
                                        call: Call<LoginResponse>,
                                        response: Response<LoginResponse>
                                    ) {
                                        if (!response.isSuccessful) {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Email atau password salah",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            return
                                        }

                                        val user = response.body()?.user
                                        if (user == null) {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Login gagal: Data user tidak valid",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            return
                                        }

                                        Log.d("FCM", "LOGIN OK userId=${user.id_user}")

                                        sessionManager.createLoginSession(
                                            idUser = user.id_user,
                                            namaMasjid = user.nama_masjid,
                                            email = user.email,
                                            alamat = user.alamat
                                        )

                                        // Ambil token & simpan ke backend (tidak menghalangi masuk)
                                        FirebaseMessaging.getInstance().token
                                            .addOnSuccessListener { token ->
                                                Log.d("FCM", "FCM Token: $token")

                                                val req = FcmTokenRequest(
                                                    userId = user.id_user,
                                                    fcmToken = token
                                                )

                                                ApiClient.instance.saveInventoryToken(req)
                                                    .enqueue(object :
                                                        Callback<Map<String, String>> {

                                                        override fun onResponse(
                                                            call: Call<Map<String, String>>,
                                                            response: Response<Map<String, String>>
                                                        ) {
                                                            Log.d(
                                                                "FCM",
                                                                "save token code=${response.code()} body=${response.body()}"
                                                            )
                                                        }

                                                        override fun onFailure(
                                                            call: Call<Map<String, String>>,
                                                            t: Throwable
                                                        ) {
                                                            Log.e(
                                                                "FCM",
                                                                "save token error=${t.message}"
                                                            )
                                                        }
                                                    })
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e("FCM", "get token error=${e.message}")
                                            }
                                    }

                                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                        isLoading = false
                                        Toast.makeText(context, "Gagal koneksi: ${t.message}", Toast.LENGTH_LONG).show()
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
                            Text(
                                text = "Sign In",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        modifier = Modifier.clickable(onClick = onSignUpClick),
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Gray)) { append("Tidak punya akun? ") }
                            withStyle(style = SpanStyle(color = linkColor, fontWeight = FontWeight.Bold)) { append("Sign Up") }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
