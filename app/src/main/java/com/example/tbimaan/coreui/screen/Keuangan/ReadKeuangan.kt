package com.example.tbimaan.coreui.screen.Keuangan

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tbimaan.R
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.coreui.navigation.KEUANGAN_GRAPH_ROUTE
import com.example.tbimaan.coreui.viewmodel.KeuanganViewModel
import java.text.NumberFormat
import java.util.Locale

// Data class (sudah benar)
data class PemasukanEntry(
    val id: String,
    val keterangan: String,
    val jumlah: Double,
    val tanggal: String,
    val namaBukti: String = "LihatBukti",
    val tipeTransaksi: String = "",
    val urlBukti: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadKeuanganScreen(
    navController: NavController,
    viewModel: KeuanganViewModel = viewModel()
) {
    val context = LocalContext.current
    val pemasukanList by viewModel.pemasukanList
    val pengeluaranList by viewModel.pengeluaranList
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<PemasukanEntry?>(null) }
    var itemToShowProof by remember { mutableStateOf<PemasukanEntry?>(null) }

    LaunchedEffect(key1 = Unit) {
        viewModel.loadData()
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("create_keuangan") },
                    containerColor = Color(0xFF004AAD),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Data")
                }
            },
            bottomBar = {
                // BottomAppBar dipindah ke sini dari CreateKeuanganScreen untuk konsistensi
                KeuanganBottomAppBar(
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Logic navigasi agar tidak menumpuk
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    currentRoute = KEUANGAN_GRAPH_ROUTE // Menandakan kita di modul Keuangan
                )
            }
        ) { innerPadding ->
            if (isLoading && pemasukanList.isEmpty() && pengeluaranList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                ) {
                    item { HeaderContent(navController) }
                    item {
                        SectionTitle("Data Pemasukan", Color(0xFF1E5B8A))
                        KeuanganTable(
                            data = pemasukanList,
                            onEditClick = { id -> navController.navigate("update_keuangan/$id") },
                            onDeleteClick = { item ->
                                itemToDelete = item
                                showDeleteDialog = true
                            },
                            onShowProofClick = { item ->
                                if (item.urlBukti.isNotBlank()) {
                                    itemToShowProof = item
                                } else {
                                    Toast.makeText(context, "Tidak ada bukti untuk item ini", Toast.LENGTH_SHORT).show()
                                }
                            },
                            isPemasukan = true
                        )
                    }
                    item {
                        SectionTitle("Data Pengeluaran", Color(0xFFD9534F))
                        KeuanganTable(
                            data = pengeluaranList,
                            onEditClick = { id -> navController.navigate("update_keuangan/$id") },
                            onDeleteClick = { item ->
                                itemToDelete = item
                                showDeleteDialog = true
                            },
                            onShowProofClick = { item ->
                                if (item.urlBukti.isNotBlank()) {
                                    itemToShowProof = item
                                } else {
                                    Toast.makeText(context, "Tidak ada bukti untuk item ini", Toast.LENGTH_SHORT).show()
                                }
                            },
                            isPemasukan = false
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) } // Spacer di akhir list
                }
            }
        }

        if (isLoading && (pemasukanList.isNotEmpty() || pengeluaranList.isNotEmpty())) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(enabled = false, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            itemName = itemToDelete?.keterangan ?: "",
            onConfirm = {
                itemToDelete?.let { viewModel.deleteKeuangan(it.id) }
                showDeleteDialog = false
                itemToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                itemToDelete = null
            }
        )
    }

    itemToShowProof?.let { item ->
        ProofImageDialog(
            imageUrl = item.urlBukti,
            onDismiss = { itemToShowProof = null }
        )
    }
}

// ================== PERBAIKAN UTAMA DAN FINAL ADA DI SINI ==================
@Composable
fun KeuanganTable(
    data: List<PemasukanEntry>,
    onEditClick: (String) -> Unit,
    onDeleteClick: (PemasukanEntry) -> Unit,
    onShowProofClick: (PemasukanEntry) -> Unit,
    isPemasukan: Boolean
) {
    val tableHeaderColor = if (isPemasukan) Color(0xFFCDEEFE) else Color(0xFFFFDDE0)
    val borderColor = if (isPemasukan) Color(0xFFB0D4E2) else Color(0xFFE5B0B0)
    val evenRowColor = Color.White
    val oddRowColor = Color(0xFFF8FBFF)

    if (data.isEmpty()) {
        Text(
            text = "Tidak ada data.",
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        return
    }

    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        Box(
            modifier = Modifier
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .horizontalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.width(700.dp)) { // Lebar tabel
                // Header
                Row(
                    Modifier.background(tableHeaderColor).padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell(text = "No", weight = 0.5f, title = true)
                    TableCell(text = "Keterangan", weight = 2f, title = true)
                    TableCell(text = "Jumlah", weight = 1.5f, title = true, alignment = TextAlign.End)
                    TableCell(text = "Tanggal", weight = 1.5f, title = true)
                    TableCell(text = "Bukti", weight = 1f, title = true)
                    TableCell(text = "Aksi", weight = 1.5f, title = true) // Judul kolom untuk tombol
                }
                // Body
                data.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.background(if (index % 2 == 0) evenRowColor else oddRowColor),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableCell(text = (index + 1).toString(), weight = 0.5f)
                        TableCell(text = item.keterangan, weight = 2f)
                        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
                            maximumFractionDigits = 0
                        }
                        TableCell(text = formatter.format(item.jumlah), weight = 1.5f, alignment = TextAlign.End)
                        TableCell(text = item.tanggal, weight = 1.5f)

                        // Sel untuk Tombol "Lihat Bukti"
                        TableCellClickable(
                            text = item.namaBukti,
                            weight = 1f,
                            onClick = { onShowProofClick(item) }
                        )


// ... di dalam KeuanganTable, di dalam Row ...

// --- INI ADALAH SEL UNTUK TOMBOL AKSI (EDIT & HAPUS) ---
                        Row(
                            modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Tombol Edit yang sesungguhnya
                            IconButton(
                                onClick = { onEditClick(item.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = Color(0xFFFFA000) // Warna oranye
                                )
                            }

                            // ================== INI BAGIAN YANG SEBELUMNYA HILANG ==================
                            // Tombol Hapus yang sesungguhnya
                            IconButton(
                                onClick = { onDeleteClick(item) },
                                modifier = Modifier.size(36.dp) // <-- Baris yang hilang
                            ) { // <-- Kurung kurawal yang hilang
                                Icon( // <-- Icon yang hilang
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus",
                                    tint = Color(0xFFD32F2F) // Warna merah
                                )
                            } // <-- Kurung kurawal yang hilang
                            // =======================================================================
                        }
                    }
                }
            }
        }
    }
}

// Composable untuk sel teks biasa
@Composable
private fun RowScope.TableCell(
    text: String,
    weight: Float,
    alignment: TextAlign = TextAlign.Center,
    title: Boolean = false
) {
    Text(
        text = text,
        modifier = Modifier
            .weight(weight)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        fontWeight = if (title) FontWeight.Bold else FontWeight.Normal,
        fontSize = if (title) 14.sp else 13.sp,
        textAlign = alignment,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

// Composable BARU untuk sel teks yang bisa diklik (untuk "Lihat Bukti")
@Composable
private fun RowScope.TableCellClickable(
    text: String,
    weight: Float,
    onClick: () -> Unit
) {
    Text(
        text = text,
        modifier = Modifier
            .weight(weight)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        color = Color(0xFF1E88E5), // Warna biru untuk menandakan bisa diklik
        textDecoration = TextDecoration.Underline,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

// Composable lain (Header, Dialog, dll.) tidak ada perubahan dan sudah benar.
@Composable
private fun SectionTitle(title: String, color: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
private fun HeaderContent(navController: NavController) {
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
            onClick = { navController.navigate("home") { popUpTo(0) } },
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(itemName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold) },
        text = { Text("Apakah Anda yakin ingin menghapus data \"$itemName\"?") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9534F))) {
                Text("Hapus", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun ProofImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(400.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Bukti Transaksi",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(id = R.drawable.logo_imaan),
                    error = painterResource(id = R.drawable.logo_imaan)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun KeuanganBottomAppBar(onNavigate: (String) -> Unit, currentRoute: String) {
    Surface(shadowElevation = 8.dp, color = Color.White) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(label = "Home", icon = Icons.Default.Home, isSelected = false, onClick = { onNavigate("home") })
            BottomNavItem(label = "Inventaris", icon = Icons.Outlined.Inventory, isSelected = false, onClick = { onNavigate("inventaris_graph") })
            BottomNavItem(label = "Kegiatan", icon = Icons.Default.List, isSelected = false, onClick = { onNavigate("kegiatan_graph") })
            BottomNavItem(label = "Keuangan", icon = Icons.Outlined.Paid, isSelected = currentRoute == KEUANGAN_GRAPH_ROUTE, onClick = {})
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