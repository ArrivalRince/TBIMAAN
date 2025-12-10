package com.example.tbimaan.coreui.screen.Inventaris

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home // Import tambahan
import androidx.compose.material.icons.filled.List // Import tambahan
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Import tambahan untuk BottomNavItem
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tbimaan.R
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.coreui.navigation.INVENTARIS_GRAPH_ROUTE
import com.example.tbimaan.coreui.navigation.KEUANGAN_GRAPH_ROUTE
import com.example.tbimaan.coreui.viewmodel.InventarisViewModel


data class InventarisEntry(
    val id: String,
    val namaBarang: String,
    val kondisi: String,
    val jumlah: String,
    val tanggal: String,
    val urlFoto: String
)

// --- Composable Navigasi Baru yang Disalin dari ReadKeuanganScreen ---

@Composable
fun KeuanganBottomAppBar(onNavigate: (String) -> Unit, currentRoute: String) {
    Surface(shadowElevation = 8.dp, color = Color.White) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(label = "Home", icon = Icons.Default.Home, isSelected = false, onClick = { onNavigate("home") })
            BottomNavItem(label = "Inventaris", icon = Icons.Outlined.Inventory, isSelected = currentRoute == INVENTARIS_GRAPH_ROUTE, onClick = { onNavigate(INVENTARIS_GRAPH_ROUTE) })
            BottomNavItem(label = "Kegiatan", icon = Icons.Default.List, isSelected = false, onClick = { onNavigate("kegiatan_graph") })
            BottomNavItem(label = "Keuangan", icon = Icons.Outlined.Paid, isSelected = currentRoute == KEUANGAN_GRAPH_ROUTE, onClick = { onNavigate(KEUANGAN_GRAPH_ROUTE) })
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


// --- Hapus/Nonaktifkan InventarisBottomAppBar lama ---
// @Composable
// private fun InventarisBottomAppBar(onNavigate: (String) -> Unit, currentRoute: String) {
//     /* ... (kode bottom app bar lama) ... */
// }
// @Composable
// private fun RowScope.BottomNavItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
//     /* ... (kode bottom nav item lama) ... */
// }


// --- HeaderContent (sudah benar) ---
@Composable
private fun HeaderContent(navController: NavController) {
    Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
        Image(
            painter = painterResource(id = R.drawable.masjid),
            contentDescription = "Masjid Header",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Mengubah navigateUp() ke "home" untuk konsistensi dengan KeuanganBottomAppBar
        BackButtonOnImage(onClick = { navController.navigate("home") { popUpTo(0) } }, modifier = Modifier.align(Alignment.TopStart))
    }
}

// --- SectionTitle (sudah benar) ---
@Composable
fun SectionTitle(title: String, color: Color) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

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
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<InventarisEntry?>(null) }
    var itemToShowProof by remember { mutableStateOf<InventarisEntry?>(null) }

    LaunchedEffect(key1 = Unit) { viewModel.loadInventaris() }
    LaunchedEffect(errorMessage) { if (errorMessage.isNotEmpty()) Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show() }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate("create_inventaris") }, containerColor = Color(0xFF004AAD), contentColor = Color.White, shape = RoundedCornerShape(16.dp)) {
                    Icon(Icons.Default.Add, "Tambah")
                }
            },
            bottomBar = {
                // =======================================================================
                // === PENGGANTIAN: Menggunakan KeuanganBottomAppBar yang baru/lengkap ===
                // =======================================================================
                KeuanganBottomAppBar(
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    currentRoute = INVENTARIS_GRAPH_ROUTE // Menandakan kita di modul Inventaris
                )
                // =======================================================================
            }
        ) { innerPadding ->
            if (isLoading && inventarisList.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            } else {
                LazyColumn(Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())) {
                    item { HeaderContent(navController) }
                    item {
                        SectionTitle("Data Inventaris Masjid", Color(0xFF1E5B8A))
                        InventarisTable(
                            data = inventarisList,
                            onEditClick = { id -> navController.navigate("update_inventaris/$id") },
                            onDeleteClick = { item -> itemToDelete = item; showDeleteDialog = true },
                            onShowProofClick = { item ->
                                if (item.urlFoto.isNotBlank()) {
                                    itemToShowProof = item
                                } else {
                                    Toast.makeText(context, "Tidak ada foto", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        // Loading Overlay
        if (isLoading && inventarisList.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.3f))
                    .clickable(enabled = false, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            itemName = itemToDelete?.namaBarang ?: "",
            onConfirm = { itemToDelete?.let { viewModel.deleteInventaris(it.id) { _, _ -> } }; showDeleteDialog = false },
            onDismiss = { showDeleteDialog = false }
        )
    }

    itemToShowProof?.let {
        ProofImageDialog(
            imageUrl = it.urlFoto,
            onDismiss = { itemToShowProof = null }
        )
    }
}

// --- Composable lainnya (tetap dipertahankan) ---
@Composable fun InventarisTable(data: List<InventarisEntry>, onEditClick: (String) -> Unit, onDeleteClick: (InventarisEntry) -> Unit, onShowProofClick: (InventarisEntry) -> Unit) { /* ... kode sama ... */
    if (data.isEmpty()) { Text("Tidak ada data inventaris.", Modifier.fillMaxWidth().padding(24.dp), textAlign = TextAlign.Center, color = Color.Gray); return }
    Box(Modifier.padding(horizontal = 16.dp)) {
        Box(Modifier.border(1.dp, Color(0xFFB0D4E2), RoundedCornerShape(8.dp)).horizontalScroll(rememberScrollState())) {
            Column(Modifier.width(700.dp)) {
                Row(Modifier.background(Color(0xFFCDEEFE)).padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    TableCell("No", 0.5f, title = true); TableCell("Nama Barang", 2f, title = true); TableCell("Jumlah", 1f, title = true); TableCell("Kondisi", 1.5f, title = true); TableCell("Tanggal Masuk", 1.5f, title = true); TableCell("Foto", 1f, title = true); TableCell("Aksi", 1.5f, title = true)
                }
                data.forEachIndexed { index, item ->
                    Row(Modifier.background(if (index % 2 == 0) Color.White else Color(0xFFF8FBFF)), verticalAlignment = Alignment.CenterVertically) {
                        TableCell((index + 1).toString(), 0.5f); TableCell(item.namaBarang, 2f); TableCell(item.jumlah, 1f); TableCell(item.kondisi, 1.5f); TableCell(item.tanggal, 1.5f); TableCellClickable("Lihat Foto", 1f) { onShowProofClick(item) }
                        Row(Modifier.weight(1.5f).padding(4.dp), Arrangement.Center, Alignment.CenterVertically) {
                            IconButton({ onEditClick(item.id) }, Modifier.size(36.dp)) { Icon(Icons.Default.Edit, "Edit", tint = Color(0xFFFFA000)) }
                            IconButton({ onDeleteClick(item) }, Modifier.size(36.dp)) { Icon(Icons.Default.Delete, "Hapus", tint = Color.Red) }
                        }
                    }
                }
            }
        }
    }
}
@Composable fun RowScope.TableCell(text: String, weight: Float, alignment: TextAlign = TextAlign.Center, title: Boolean = false) { /* ... kode sama ... */
    Text(text, Modifier.weight(weight).padding(8.dp, 12.dp), fontWeight = if (title) FontWeight.Bold else FontWeight.Normal, textAlign = alignment, overflow = TextOverflow.Ellipsis, maxLines = 1)
}
@Composable
fun RowScope.TableCellClickable(text: String, weight: Float, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .weight(weight)
            .clickable { onClick() }
            .padding(8.dp, 12.dp),
        textAlign = TextAlign.Center,
        color = Color(0xFF004AAD),
        textDecoration = TextDecoration.Underline
    )
}
@Composable fun DeleteConfirmationDialog(itemName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) { /* ... kode sama ... */
    AlertDialog(onDismiss, title = { Text("Konfirmasi Hapus") }, text = { Text("Yakin ingin menghapus \"$itemName\"?") }, confirmButton = { Button({ onConfirm() }, colors = ButtonDefaults.buttonColors(Color.Red)) { Text("Hapus") } }, dismissButton = { OutlinedButton({ onDismiss() }) { Text("Batal") } })
}
@Composable fun ProofImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(onDismiss) {
        Box(Modifier.fillMaxWidth(0.95f).wrapContentHeight().clip(RoundedCornerShape(16.dp)).background(Color.White)) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Foto Bukti",
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_launcher_background)
            )
        }
    }
}