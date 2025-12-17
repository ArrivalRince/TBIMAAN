package com.example.tbimaan.coreui.screen.Keuangan

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import com.example.tbimaan.coreui.navigation.HOME_GRAPH_ROUTE
import com.example.tbimaan.coreui.navigation.INVENTARIS_GRAPH_ROUTE
import com.example.tbimaan.coreui.navigation.KEGIATAN_GRAPH_ROUTE
import com.example.tbimaan.coreui.navigation.KEUANGAN_GRAPH_ROUTE
import com.example.tbimaan.coreui.viewmodel.KeuanganViewModel
import com.example.tbimaan.model.SessionManager
import java.text.NumberFormat
import java.util.Locale

data class PemasukanEntry(
    val id: String,
    val keterangan: String,
    val jumlah: Double,
    val tanggal: String,
    val namaBukti: String = "Lihat Bukti",
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
    val sessionManager = remember { SessionManager(context) }

    val pemasukanList by viewModel.pemasukanList
    val pengeluaranList by viewModel.pengeluaranList
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage

    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<PemasukanEntry?>(null) }
    var itemToShowProof by remember { mutableStateOf<PemasukanEntry?>(null) }

    LaunchedEffect(key1 = Unit) {
        viewModel.loadData(sessionManager.idUser)
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
                KeuanganBottomAppBar(
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    currentRoute = KEUANGAN_GRAPH_ROUTE
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
                    item { Spacer(modifier = Modifier.height(80.dp)) }
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
                itemToDelete?.let { viewModel.deleteKeuangan(it.id, sessionManager.idUser) }
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
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
    )
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
            Column(modifier = Modifier.width(700.dp)) {
                Row(
                    Modifier.background(tableHeaderColor).padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell(text = "No", weight = 0.5f, title = true)
                    TableCell(text = "Keterangan", weight = 2f, title = true)
                    TableCell(text = "Jumlah", weight = 1.5f, title = true, alignment = TextAlign.End)
                    TableCell(text = "Tanggal", weight = 1.5f, title = true)
                    TableCell(text = "Bukti", weight = 1f, title = true)
                    TableCell(text = "Aksi", weight = 1.5f, title = true)
                }
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
                        TableCellClickable(
                            text = item.namaBukti,
                            weight = 1f,
                            onClick = { onShowProofClick(item) }
                        )
                        Row(
                            modifier = Modifier.weight(1.5f).padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(onClick = { onEditClick(item.id) }, modifier = Modifier.size(36.dp)) {
                                Icon(imageVector = Icons.Default.Edit, "Edit", tint = Color(0xFFFFA000))
                            }
                            IconButton(onClick = { onDeleteClick(item) }, modifier = Modifier.size(36.dp)) {
                                Icon(imageVector = Icons.Default.Delete, "Hapus", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float, alignment: TextAlign = TextAlign.Start, title: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.weight(weight).padding(horizontal = 8.dp, vertical = 10.dp),
        fontWeight = if (title) FontWeight.Bold else FontWeight.Normal,
        textAlign = alignment,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        fontSize = 12.sp
    )
}

@Composable
fun RowScope.TableCellClickable(text: String, weight: Float, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .weight(weight)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline,
        color = Color.Blue,
        fontSize = 12.sp
    )
}

@Composable
fun DeleteConfirmationDialog(itemName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Hapus") },
        text = { Text("Apakah Anda yakin ingin menghapus data \"$itemName\"?") },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
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
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Bukti Transaksi",
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentScale = ContentScale.Fit
                )
                Button(onClick = onDismiss, modifier = Modifier.padding(16.dp)) {
                    Text("Tutup")
                }
            }
        }
    }
}

@Composable
fun KeuanganBottomAppBar(onNavigate: (String) -> Unit, currentRoute: String?) {
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
                onClick = { if (!isSelected) onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = when (route) {
                            HOME_GRAPH_ROUTE -> Icons.Default.Home
                            INVENTARIS_GRAPH_ROUTE -> Icons.Outlined.Inventory
                            KEGIATAN_GRAPH_ROUTE -> Icons.Default.List
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
