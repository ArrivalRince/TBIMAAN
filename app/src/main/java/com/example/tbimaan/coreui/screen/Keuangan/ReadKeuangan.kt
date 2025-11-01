package com.example.tbimaan.coreui.screen.Keuangan

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tbimaan.R
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.coreui.navigation.KEUANGAN_GRAPH_ROUTE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadKeuanganScreen(
    navController: NavController,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit,
    onUploadClick: () -> Unit
) {
    val textColorPrimary = Color(0xFF1E5B8A)
    val pemasukanColor = Color(0xFFB0D4E2)
    val pengeluaranColor = Color(0xFFD97D7D)
    val backgroundColor = Color.White

    Scaffold(
        floatingActionButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(
                    onClick = onUploadClick,
                    shape = RoundedCornerShape(16.dp),
                    containerColor = Color.White,
                    contentColor = textColorPrimary
                ) {
                    Icon(Icons.Default.Upload, contentDescription = "Upload Data")
                }
                FloatingActionButton(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(16.dp),
                    containerColor = pemasukanColor,
                    contentColor = textColorPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Data")
                }
            }
        },
        bottomBar = {
            KeuanganBottomAppBar(
                onNavigate = onNavigate,
                currentRoute = KEUANGAN_GRAPH_ROUTE
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.masjid),
                    contentDescription = "Masjid Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Tombol kembali yang konsisten
                BackButtonOnImage(
                    onClick = { navController.navigate("home") }, // Kembali ke Home
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Data Keuangan Masjid",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColorPrimary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Konten lainnya tetap sama...
                Button(
                    onClick = { /* ... */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = pemasukanColor),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("PEMASUKAN", color = textColorPrimary, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { /* ... */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = pengeluaranColor),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("PENGELUARAN", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada data keuangan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

        }
    }
}

@Composable
private fun KeuanganBottomAppBar(onNavigate: (String) -> Unit, currentRoute: String) {
    Surface(shadowElevation = 8.dp, color = Color(0xFFF8F8F8)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(label = "Home", icon = Icons.Default.Home, isSelected = false, onClick = { onNavigate("home") })
            BottomNavItem(label = "Inventaris", icon = Icons.Outlined.Inventory, isSelected = false, onClick = { onNavigate("inventaris_graph") })
            BottomNavItem(label = "Kegiatan", icon = Icons.Default.List, isSelected = false, onClick = { onNavigate("kegiatan_graph") })
            BottomNavItem(label = "Keuangan", icon = Icons.Outlined.Paid, isSelected = true, onClick = { /* Sedang di sini */ })
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(label: String, icon: ImageVector, onClick: () -> Unit, isSelected: Boolean) {
    val contentColor = if (isSelected) Color(0xFF1E5B8A) else Color.Gray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .height(64.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = contentColor)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = contentColor, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun ReadKeuanganScreenPreview() {
    ReadKeuanganScreen(
        navController = rememberNavController(),
        onNavigate = {},
        onAddClick = {},
        onUploadClick = {}
    )
}
