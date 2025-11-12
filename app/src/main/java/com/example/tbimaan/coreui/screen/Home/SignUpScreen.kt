package com.example.tbimaan.coreui.screen.Home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tbimaan.R
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.network.ApiClient
import com.example.tbimaan.network.RegisterRequest
import com.example.tbimaan.network.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SignUpScreen(
    navController: NavController? = null,
    onSignUpClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }

    val primaryBlue = Color(0xFF007BFF)
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Background images
        Image(
            painter = painterResource(id = R.drawable.masjid_orange),
            contentDescription = "Background Masjid Oranye",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .align(Alignment.Center)
                .offset(y = 65.dp),
            contentScale = ContentScale.Fit
        )
        Image(
            painter = painterResource(id = R.drawable.gelombang),
            contentDescription = "Background Wave",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            contentScale = ContentScale.FillWidth
        )

        // Back button
        BackButtonOnImage(onClick = onBackClick)

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Get started for free",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Form fields (memanggil composable bantuan di bawah)
                    SignUpTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = "First name"
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    SignUpTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = "Last name"
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    SignUpTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    PasswordTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isVisible = isPasswordVisible,
                        onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    PasswordTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm password",
                        isVisible = isConfirmPasswordVisible,
                        onVisibilityChange = { isConfirmPasswordVisible = !isConfirmPasswordVisible }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = primaryBlue,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = "I agree with the terms and conditions by creating an account",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // BUTTON: lakukan validasi lalu panggil API
                    Button(
                        onClick = {
                            // Validasi sederhana
                            if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (password != confirmPassword) {
                                Toast.makeText(context, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!isChecked) {
                                Toast.makeText(context, "Setujui syarat terlebih dahulu", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Buat request sesuai backend
                            val request = RegisterRequest(
                                nama_masjid = "$firstName $lastName",
                                email = email,
                                password = password,
                                alamat = "Alamat belum diisi"
                            )

                            ApiClient.instance.registerUser(request)
                                .enqueue(object : Callback<RegisterResponse> {
                                    override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                                            onSignUpClick()
                                        } else {
                                            Toast.makeText(context, "Registrasi gagal: ${response.code()}", Toast.LENGTH_LONG).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                                        Toast.makeText(context, "Error koneksi: ${t.message}", Toast.LENGTH_LONG).show()
                                    }
                                })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                    ) {
                        Text("Sign Up", fontSize = 16.sp, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

/**
 * Reusable text field composable
 */
@Composable
private fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val lightGray = Color(0xFFF5F5F5)
    val primaryBlue = Color(0xFF007BFF)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryBlue,
            unfocusedBorderColor = lightGray,
            unfocusedContainerColor = lightGray,
            focusedContainerColor = lightGray
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

/**
 * Reusable password field composable
 */
@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onVisibilityChange: () -> Unit
) {
    val lightGray = Color(0xFFF5F5F5)
    val primaryBlue = Color(0xFF007BFF)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryBlue,
            unfocusedBorderColor = lightGray,
            unfocusedContainerColor = lightGray,
            focusedContainerColor = lightGray
        ),
        singleLine = true,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onVisibilityChange) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle password visibility"
                )
            }
        }
    )
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(onSignUpClick = {}, onBackClick = {})
}
