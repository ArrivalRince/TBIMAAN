package com.example.tbimaan.coreui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Tombol aksi utama dengan latar belakang biru (untuk Simpan).
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF5BC0DE) // Warna biru
        )
    ) {
        Text(text, fontSize = 16.sp)
    }
}

/**
 * Tombol aksi sekunder dengan latar belakang merah (untuk Batal).
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD9534F) // Warna merah
        )
    ) {
        Text(text, fontSize = 16.sp)
    }
}

/**
 * Floating Action Button (FAB) untuk aksi 'Tambah'.
 */
@Composable
fun AddFAB(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(0xFF004AAD),
        contentColor = Color.White
    ) {
        Icon(Icons.Default.Add, contentDescription = "Tambah Data")
    }
}

/**
 * Tombol Edit kecil di dalam Card.
 */
@Composable
fun EditButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004AAD)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            Icons.Default.Edit,
            contentDescription = "Edit",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("Edit", color = Color.White)
    }
}

/**
 * IconButton untuk kembali (Back) yang ditempatkan di atas gambar.
 */
@Composable
fun BackButtonOnImage(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .statusBarsPadding()
            .padding(8.dp)
            .background(Color.Black.copy(alpha = 0.3f), shape = RoundedCornerShape(50))
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Kembali",
            tint = Color.White
        )
    }
}
