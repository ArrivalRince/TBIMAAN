package com.example.tbimaan.coreui.screen.Keuangan

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tbimaan.R
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.coreui.navigation.HOME_GRAPH_ROUTE
import com.example.tbimaan.coreui.navigation.INVENTARIS_GRAPH_ROUTE
import com.example.tbimaan.coreui.navigation.KEGIATAN_GRAPH_ROUTE
import com.example.tbimaan.coreui.navigation.KEUANGAN_GRAPH_ROUTE
import com.example.tbimaan.coreui.viewmodel.KeuanganViewModel
import com.example.tbimaan.model.SessionManager
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadKeuanganScreen(
    navController: NavController,
    viewModel: KeuanganViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val pemasukanList by viewModel.pemasukanList
    val pengeluaranList by viewModel.pengeluaranList
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<PemasukanEntry?>(null) }
    var itemToShowProof by remember { mutableStateOf<PemasukanEntry?>(null) }

    // --- Permintaan Izin Notifikasi ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(context, "Izin notifikasi ditolak.", Toast.LENGTH_SHORT).show()
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    // ---------------------------------

    LaunchedEffect(key1 = Unit) {
        sessionManager.idUser?.let {
            viewModel.loadData(it, context)
        }
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
                // PERBAIKAN: Menggunakan Row di dalam BottomAppBar, bukan NavigationBarItem langsung
                KeuanganBottomAppBar(navController = navController, currentRoute = KEUANGAN_GRAPH_ROUTE)
            }
        ) { innerPadding ->
            if (isLoading && pemasukanList.isEmpty() && pengeluaranList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                ) {
                    item { HeaderContent(navController) }

                    // --- Pemasukan ---
                    item {
                        SectionTitle("Data Pemasukan", Color(0xFF1E5B8A))
                        KeuanganTable(
                            data = pemasukanList,
                            onEditClick = { id -> navController.navigate("update_keuangan/$id") },
                            onDeleteClick = { item -> itemToDelete = item; showDeleteDialog = true },
                            onShowProofClick = { item ->
                                if (item.urlBukti.isNotBlank()) itemToShowProof = item
                                else Toast.makeText(context, "Tidak ada bukti untuk item ini", Toast.LENGTH_SHORT).show()
                            },
                            isPemasukan = true
                        )
                    }
                    item {
                        val totalPemasukan = pemasukanList.sumOf { it.jumlah }
                        TotalRow(label = "Total Pemasukan", total = totalPemasukan, color = Color(0xFF1E5B8A), borderColor = Color(0xFFB0D4E2))
                    }

                    // --- Pengeluaran ---
                    item {
                        SectionTitle("Data Pengeluaran", Color(0xFFD9534F))
                        KeuanganTable(
                            data = pengeluaranList,
                            onEditClick = { id -> navController.navigate("update_keuangan/$id") },
                            onDeleteClick = { item -> itemToDelete = item; showDeleteDialog = true },
                            onShowProofClick = { item ->
                                if (item.urlBukti.isNotBlank()) itemToShowProof = item
                                else Toast.makeText(context, "Tidak ada bukti untuk item ini", Toast.LENGTH_SHORT).show()
                            },
                            isPemasukan = false
                        )
                    }
                    item {
                        val totalPengeluaran = pengeluaranList.sumOf { it.jumlah }
                        TotalRow(label = "Total Pengeluaran", total = totalPengeluaran, color = Color(0xFFD9534F), borderColor = Color(0xFFE5B0B0))
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        // --- Dialogs ---
        if (showDeleteDialog) {
            DeleteConfirmationDialog(
                itemName = itemToDelete?.keterangan ?: "",
                onConfirm = {
                    itemToDelete?.let { viewModel.deleteKeuangan(it.id, sessionManager.idUser, context) }
                    showDeleteDialog = false
                    itemToDelete = null
                },
                onDismiss = { showDeleteDialog = false; itemToDelete = null }
            )
        }
        itemToShowProof?.let { item ->
            ProofImageDialog(imageUrl = item.urlBukti, onDismiss = { itemToShowProof = null })
        }
    }
}

// PERBAIKAN: Bottom Bar yang benar
@Composable
fun KeuanganBottomAppBar(navController: NavController, currentRoute: String?) {
    BottomAppBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            "Home" to HOME_GRAPH_ROUTE,
            "Inventaris" to INVENTARIS_GRAPH_ROUTE,
            "Kegiatan" to KEGIATAN_GRAPH_ROUTE,
            "Keuangan" to KEUANGAN_GRAPH_ROUTE
        )
        navItems.forEach { (label, route) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = when (label) {
                            "Home" -> Icons.Default.Home
                            "Inventaris" -> Icons.Outlined.Inventory
                            "Kegiatan" -> Icons.Default.List
                            else -> Icons.Outlined.Paid
                        },
                        contentDescription = label
                    )
                },
                label = { Text(label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF004AAD),
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color(0xFF004AAD),
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFFE3F2FD)
                )
            )
        }
    }
}

@Composable
fun TotalRow(label: String, total: Double, color: Color, borderColor: Color) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        maximumFractionDigits = 0
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
            .background(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
        Text(text = formatter.format(total), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun HeaderContent(navController: NavController) {
    Box(Modifier.fillMaxWidth().height(180.dp)) {
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
fun SectionTitle(title: String, color: Color) {
    Text(text = title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color, modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp))
}

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
    if (data.isEmpty()) {
        Text("Tidak ada data.", modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), textAlign = TextAlign.Center, color = Color.Gray)
        return
    }
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        Box(modifier = Modifier.border(1.dp, borderColor, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)).horizontalScroll(rememberScrollState())) {
            Column(modifier = Modifier.width(700.dp)) {
                Row(Modifier.background(tableHeaderColor, shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)).padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    TableCell("No", 0.5f, title = true)
                    TableCell("Keterangan", 2f, title = true)
                    TableCell("Jumlah", 1.5f, alignment = TextAlign.End, title = true)
                    TableCell("Tanggal", 1.5f, title = true)
                    TableCell("Bukti", 1f, title = true)
                    TableCell("Aksi", 1.5f, title = true)
                }
                data.forEachIndexed { index, item ->
                    Row(modifier = Modifier.background(if (index % 2 == 0) Color.White else Color(0xFFF8FBFF)), verticalAlignment = Alignment.CenterVertically) {
                        TableCell((index + 1).toString(), 0.5f)
                        TableCell(item.keterangan, 2f)
                        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply { maximumFractionDigits = 0 }
                        TableCell(formatter.format(item.jumlah), 1.5f, alignment = TextAlign.End)
                        TableCell(item.tanggal, 1.5f)
                        TableCellClickable(item.namaBukti, 1f) { onShowProofClick(item) }
                        Row(Modifier.weight(1.5f).padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            IconButton({ onEditClick(item.id) }, Modifier.size(36.dp)) { Icon(Icons.Default.Edit, "Edit", tint = Color(0xFFFFA000)) }
                            IconButton({ onDeleteClick(item) }, Modifier.size(36.dp)) { Icon(Icons.Default.Delete, "Hapus", tint = Color.Red) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float, alignment: TextAlign = TextAlign.Start, title: Boolean = false) {
    Text(text, Modifier.weight(weight).padding(horizontal = 8.dp, vertical = 10.dp), fontWeight = if (title) FontWeight.Bold else FontWeight.Normal, textAlign = alignment, overflow = TextOverflow.Ellipsis, maxLines = 1, fontSize = 12.sp)
}

@Composable
fun RowScope.TableCellClickable(text: String, weight: Float, onClick: () -> Unit) {
    Text(text, Modifier.weight(weight).clickable(onClick = onClick).padding(horizontal = 8.dp, vertical = 10.dp), fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, textDecoration = TextDecoration.Underline, color = Color.Blue, fontSize = 12.sp)
}

@Composable
fun DeleteConfirmationDialog(itemName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Hapus") },
        text = { Text("Apakah Anda yakin ingin menghapus data \"$itemName\"?") },
        confirmButton = { Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Hapus") } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun ProofImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(Modifier.fillMaxWidth().wrapContentHeight(), shape = RoundedCornerShape(16.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(model = imageUrl, contentDescription = "Bukti Transaksi", modifier = Modifier.fillMaxWidth().height(300.dp), contentScale = ContentScale.Fit)
                Button(onClick = onDismiss, modifier = Modifier.padding(16.dp)) { Text("Tutup") }
            }
        }
    }
}

// Dummy data class
data class PemasukanEntry(
    val id: String,
    val keterangan: String,
    val jumlah: Double,
    val tanggal: String,
    val namaBukti: String = "Lihat Bukti",
    val tipeTransaksi: String = "",
    val urlBukti: String = ""
)
