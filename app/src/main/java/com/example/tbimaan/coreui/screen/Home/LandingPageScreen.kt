package com.example.tbimaan.coreui.screen.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tbimaan.R

@Composable
fun LandingPageScreen(
    onGetStartedClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val primaryBlue = Color(0xFF007BFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 🔹 Gelombang biru di bagian atas
        Image(
            painter = painterResource(id = R.drawable.gelombang),
            contentDescription = "Blue Wave Background",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            contentScale = ContentScale.FillWidth
        )

        Image(
            painter = painterResource(id = R.drawable.bg_splash),
            contentDescription = "Splash Background with Logo",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .align(Alignment.Center)
                .offset(y = 65.dp),
            contentScale = ContentScale.Fit
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .width(220.dp)
                    .height(56.dp)
                    .offset(y = (-315).dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
            ) {
                Text(
                    text = "Get Started",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}
