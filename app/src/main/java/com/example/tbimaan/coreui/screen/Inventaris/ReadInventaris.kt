package com.example.tbimaan.coreui.screen.Inventaris

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tbimaan.R
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.coreui.navigation.HOME_GRAPH_ROUTE
import com.example.tbimaan.coreui.navigation.INVENTARIS_GRAPH_ROUTE
import com.example.tbimaan.coreui.navigation.KEGIATAN_GRAPH_ROUTE
import com.example.tbimaan.coreui.navigation.KEUANGAN_GRAPH_ROUTE
import com.example.tbimaan.coreui.viewmodel.InventarisEntry
import com.example.tbimaan.coreui.viewmodel.InventarisViewModel

// =====================================================================
// ===        PERBAIKAN UTAMA: HAPUS DATA CLASS LOKAL INI          ===
// =====================================================================
// Definisi data class lokal yang menyebabkan masalah sudah dihapus dari sini.
// Kita sekarang menggunakan 'InventarisEntry' yang diimpor dari ViewModel.
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadInventarisScreen(
    navController: NavController,
    viewModel: InventarisViewModel = viewModel()
) {
    val context = LocalContext.current
    val inventarisList by viewModel.inventarisList
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    var itemToDelete by remember { mutableStateOf<InventarisEntry?>(null) }
    var itemToShowProof by remember { mutableStateOf<InventarisEntry?>(null) }

    // Memuat data saat screen pertama kali dibuka atau saat kembali ke screen ini
    LaunchedEffect(Unit) {
        viewModel.loadInventaris()
    }

    // Menampilkan pesan error dari ViewModel
    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_inventaris") },
                containerColor = Color(0xFF004AAD),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Default.Add, "Tambah") }
        },
        bottomBar = {
            // Menggunakan Bottom Nav Bar yang konsisten
            ConsistentBottomNavBar(
                navController = navController,
                currentRoute = INVENTARIS_GRAPH_ROUTE
            )
        }
    ) { innerPadding ->
        // Tampilkan loading indicator di tengah jika list masih kosong
        if (isLoading && inventarisList.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Tampilkan list data jika sudah ada atau loading selesai
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // Gunakan padding dari Scaffold
            ) {
                item { HeaderContent(navController) }
                item { SectionTitle("Data Inventaris Masjid") }

                // PERBAIKAN: Gunakan `items` untuk efisiensi LazyColumn
                items(inventarisList, key = { it.id }) { item ->
                    InventarisCard(
                        item = item,
                        onEditClick = { navController.navigate("update_inventaris/${item.id}") },
                        onDeleteClick = { itemToDelete = item },
                        onShowProofClick = { itemToShowProof = item }
                    )
                }

                item { Spacer(Modifier.height(80.dp)) } // Spacer di akhir list
            }
        }
    }

    // Dialog konfirmasi hapus
    itemToDelete?.let { item ->
        DeleteConfirmationDialog(
            itemName = item.namaBarang,
            onConfirm = {
                viewModel.deleteInventaris(item.id) { _, _ -> }
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null }
        )
    }

    // Dialog untuk menampilkan gambar
    itemToShowProof?.let { item ->
        ProofImageDialog(
            imageUrl = item.urlFoto,
            onDismiss = { itemToShowProof = null }
        )
    }
}

// ----------------------
// HELPER COMPOSABLES (Kode yang lain tidak dihapus)
// ----------------------

@Composable
fun InventarisCard(
    item: InventarisEntry,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onShowProofClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Composable untuk menampilkan gambar
            AsyncImage(
                model = item.urlFoto, // <-- Ini sekarang adalah URL LENGKAP dari ViewModel
                contentDescription = "Foto ${item.namaBarang}",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray) // Latar belakang jika gambar sedang loading
                    .clickable { onShowProofClick() },
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.logo_imaan), // Gambar saat loading
                error = painterResource(id = R.drawable.logo_imaan) // Gambar jika terjadi error
            )

            Spacer(Modifier.width(16.dp))

            // Kolom untuk teks
            Column(Modifier.weight(1f)) {
                Text(item.namaBarang, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E5B8A))
                Text("Jumlah: ${item.jumlah}", color = Color.Gray)
                Text(item.tanggal, color = Color.Gray, fontSize = 12.sp)
            }

            // Tombol Aksi
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFFFFA000))
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
            }
        }
    }
}

@Composable
fun HeaderContent(navController: NavController) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.masjid),
            contentDescription = "Header",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        BackButtonOnImage(
            onClick = { navController.navigate(HOME_GRAPH_ROUTE) { popUpTo(0) } },
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(16.dp),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1E5B8A)
    )
}

@Composable
fun ConsistentBottomNavBar(navController: NavController, currentRoute: String?) {
    Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp, color = Color.White) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 4.dp),
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
                            navController.navigate(route) { popUpTo(navController.graph.startDestinationId) }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RowScope.BottomNavBarItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
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

@Composable
fun DeleteConfirmationDialog(itemName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Hapus") },
        text = { Text("Yakin ingin menghapus \"$itemName\"?") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(Color.Red)) {
                Text("Hapus")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun ProofImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Foto Bukti",
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                contentScale = ContentScale.Fit,
                placeholder = painterResource(R.drawable.logo_imaan),
                error = painterResource(R.drawable.logo_imaan)
            )
        }
    }
}