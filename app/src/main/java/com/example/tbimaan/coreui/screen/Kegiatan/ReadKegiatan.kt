package com.example.tbimaan.coreui.screen.Kegiatan

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tbimaan.R
import com.example.tbimaan.coreui.components.AddFAB
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.coreui.navigation.HOME_GRAPH_ROUTE
import com.example.tbimaan.coreui.navigation.INVENTARIS_GRAPH_ROUTE
import com.example.tbimaan.coreui.navigation.KEGIATAN_GRAPH_ROUTE
import com.example.tbimaan.coreui.navigation.KEUANGAN_GRAPH_ROUTE
import com.example.tbimaan.coreui.viewmodel.KegiatanEntry
import com.example.tbimaan.coreui.viewmodel.KegiatanViewModel
import com.example.tbimaan.model.SessionManager

@Composable
fun ReadKegiatanScreen(
    navController: NavController,
    viewModel: KegiatanViewModel,
    onAddClick: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val idUser = sessionManager.idUser

    val kegiatanList by viewModel.kegiatanList
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedKegiatan by remember { mutableStateOf<KegiatanEntry?>(null) }

    LaunchedEffect(idUser) {
        idUser?.let {
            viewModel.loadKegiatan(context, it)
        }
    }

    var query by remember { mutableStateOf("") }
    val filteredList = kegiatanList.filter {
        it.nama.contains(query, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = { AddFAB(onClick = onAddClick) },
        bottomBar = {
            ConsistentBottomNavBar(
                navController = navController,
                currentRoute = KEGIATAN_GRAPH_ROUTE
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9F9F9))
        ) {

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.masjid),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                BackButtonOnImage(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            Text(
                "DAFTAR KEGIATAN",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF1E5B8A)
            )

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = { Text("Cari kegiatan...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            when {
                isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }

                errorMessage.isNotEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(errorMessage, color = Color.Red)
                }

                filteredList.isEmpty() -> Text(
                    "Tidak ada kegiatan",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    textAlign = TextAlign.Center
                )

                else -> LazyColumn {
                    items(filteredList) { kegiatan ->
                        KegiatanCard(
                            kegiatan = kegiatan,
                            onEdit = {
                                val route =
                                    "update_kegiatan/" +
                                            kegiatan.id + "/" +
                                            Uri.encode(kegiatan.nama) + "/" +
                                            Uri.encode(kegiatan.tanggal) + "/" +
                                            Uri.encode(kegiatan.lokasi ?: "") + "/" +
                                            Uri.encode(kegiatan.penanggungjawab ?: "") + "/" +
                                            Uri.encode(kegiatan.deskripsi ?: "") + "/" +
                                            Uri.encode(kegiatan.status ?: "") + "/" +
                                            Uri.encode(kegiatan.fotoUrl ?: "")
                                navController.navigate(route)
                            },
                            onDelete = {
                                selectedKegiatan = kegiatan
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog && selectedKegiatan != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                selectedKegiatan = null
            },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Yakin ingin menghapus \"${selectedKegiatan!!.nama}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        idUser?.let { uid ->
                            viewModel.deleteKegiatan(
                                id = selectedKegiatan!!.id,
                                context = context,
                                idUser = uid
                            ) { _, _ -> }
                        }
                        showDeleteDialog = false
                        selectedKegiatan = null
                    },
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) { Text("Hapus") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteDialog = false
                        selectedKegiatan = null
                    }
                ) { Text("Batal") }
            }
        )
    }
}

@Composable
fun KegiatanCard(
    kegiatan: KegiatanEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {

            AsyncImage(
                model = kegiatan.fotoUrl ?: R.drawable.kajianbulanan,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(12.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        kegiatan.nama,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )

                    Box(
                        modifier = Modifier
                            .background(
                                if (kegiatan.status == "Selesai")
                                    Color(0xFFE8F5E9)
                                else
                                    Color(0xFFFFEBEE),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(kegiatan.status ?: "-", fontSize = 9.sp)
                    }
                }

                Spacer(Modifier.height(6.dp))

                Text("Tanggal: ${kegiatan.tanggal}", fontSize = 11.sp)
                Text("Tempat: ${kegiatan.lokasi ?: "-"}", fontSize = 11.sp)
                Text("Penanggungjawab: ${kegiatan.penanggungjawab ?: "-"}", fontSize = 11.sp)

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        kegiatan.deskripsi ?: "-",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, null, tint = Color(0xFFFFA000))
                    }

                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, null, tint = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun ConsistentBottomNavBar(
    navController: NavController,
    currentRoute: String?
) {
    Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp, color = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navItems = listOf(
                "Home" to HOME_GRAPH_ROUTE,
                "Inventaris" to INVENTARIS_GRAPH_ROUTE,
                "Kegiatan" to KEGIATAN_GRAPH_ROUTE,
                "Keuangan" to KEUANGAN_GRAPH_ROUTE
            )
            navItems.forEach { (label, route) ->
                BottomNavBarItem(
                    label = label,
                    icon = when (route) {
                        HOME_GRAPH_ROUTE -> Icons.Default.Home
                        INVENTARIS_GRAPH_ROUTE -> Icons.Outlined.Inventory
                        KEGIATAN_GRAPH_ROUTE -> Icons.Default.List
                        else -> Icons.Outlined.Paid
                    },
                    isSelected = currentRoute == route,
                    onClick = {
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RowScope.BottomNavBarItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
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
        Text(
            text = label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}
