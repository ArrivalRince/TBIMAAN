package com.example.tbimaan.coreui.screen.Inventaris

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.tbimaan.coreui.components.AddFAB
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.coreui.components.EditButton
import com.example.tbimaan.coreui.navigation.INVENTARIS_GRAPH_ROUTE

@Composable
fun ReadInventarisScreen(
    navController: NavController,
    onNavigate: (String) -> Unit, // <<< TAMBAHKAN PARAMETER INI
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            AddFAB(onClick = onAddClick)
        },
        // ===== PERBAIKAN: TAMBAHKAN BOTTOMNAVBAR DI SINI =====
        bottomBar = {
            InventarisBottomAppBar(
                onNavigate = onNavigate,
                currentRoute = INVENTARIS_GRAPH_ROUTE
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
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
                BackButtonOnImage(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            Card(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Loudspeaker Luar (TOA)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Jumlah: 4")
                        Text("Kondisi: Baik")
                        Text("Tanggal: 27/10/2025")
                        Spacer(modifier = Modifier.height(8.dp))
                        EditButton(onClick = { onEditClick("ID_TOA_123") })
                    }
                    Image(
                        painter = painterResource(id = R.drawable.toa),
                        contentDescription = "TOA Speaker",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

// ===== PERBAIKAN: SALIN KOMPONEN BOTTOMNAVBAR KE SINI =====
@Composable
private fun InventarisBottomAppBar(onNavigate: (String) -> Unit, currentRoute: String) {
    Surface(shadowElevation = 8.dp, color = Color(0xFFF8F8F8)) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(label = "Home", icon = Icons.Default.Home, isSelected = currentRoute == "home", onClick = { onNavigate("home") })
            BottomNavItem(label = "Inventaris", icon = Icons.Outlined.Inventory, isSelected = currentRoute == "inventaris_graph", onClick = { onNavigate("inventaris_graph") })
            BottomNavItem(label = "Kegiatan", icon = Icons.Default.List, isSelected = currentRoute == "kegiatan_graph", onClick = { onNavigate("kegiatan_graph") })
            BottomNavItem(label = "Keuangan", icon = Icons.Outlined.Paid, isSelected = currentRoute == "keuangan_graph", onClick = { onNavigate("keuangan_graph") })
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val contentColor = if (isSelected) Color(0xFF1E5B8A) else Color.Gray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.weight(1f).height(64.dp).clickable(onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = contentColor)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = contentColor, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center)
    }
}

@Preview(showBackground = true)
@Composable
fun ReadInventarisScreenPreview() {
    ReadInventarisScreen(
        navController = rememberNavController(),
        onNavigate = {},
        onAddClick = {},
        onEditClick = {}
    )
}
