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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tbimaan.R
import com.example.tbimaan.coreui.components.BackButtonOnImage
import com.example.tbimaan.coreui.navigation.KEUANGAN_GRAPH_ROUTE
import java.text.NumberFormat
import java.util.Locale

// Data class ini bisa digunakan untuk pemasukan dan pengeluaran
data class PemasukanEntry(
    val id: String,
    val keterangan: String,
    val jumlah: Double,
    val waktu: String,
    val namaBukti: String = "LihatBukti"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadKeuanganScreen(
    navController: NavController,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit,
    onUploadClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    // --- Data Dummy Pemasukan ---
    val dataPemasukan = listOf(
        PemasukanEntry("P001", "Donasi", 250000.0, "18 Agustus 2025"),
        PemasukanEntry("P002", "Infaq", 150000.0, "24 Agustus 2025"),
        PemasukanEntry("P003", "Infaq", 180000.0, "26 Agustus 2025"),
        PemasukanEntry("P004", "Donasi", 200000.0, "30 Agustus 2025")
    )

    // --- Data Dummy Pengeluaran ---
    val dataPengeluaran = listOf(
        PemasukanEntry("K001", "Biaya Listrik Bulanan", 350000.0, "20 Agustus 2025"),
        PemasukanEntry("K002", "Perbaikan Sound System", 750000.0, "22 Agustus 2025"),
        PemasukanEntry("K003", "Konsumsi Acara Maulid", 450000.0, "25 Agustus 2025")
    )

    Scaffold(
        floatingActionButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(onClick = onUploadClick, shape = RoundedCornerShape(16.dp)) {
                    Icon(Icons.Default.Upload, contentDescription = "Upload Data")
                }
                FloatingActionButton(onClick = onAddClick, shape = RoundedCornerShape(16.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Data")
                }
            }
        },
        bottomBar = {
            KeuanganBottomAppBar(
                onNavigate = onNavigate,
                currentRoute = KEUANGAN_GRAPH_ROUTE
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            item {
                HeaderContent(navController)
            }

            // --- BAGIAN TABEL PEMASUKAN ---
            item {
                SectionTitle(title = "Data Pemasukan", color = Color(0xFF1E5B8A))
                KeuanganTable(
                    data = dataPemasukan,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    isPemasukan = true // Tandai sebagai tabel pemasukan
                )
            }

            // --- BAGIAN TABEL PENGELUARAN BARU ---
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionTitle(title = "Data Pengeluaran", color = Color(0xFFD9534F))
                KeuanganTable(
                    data = dataPengeluaran,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    isPemasukan = false // Tandai sebagai tabel pengeluaran
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Composable untuk judul setiap seksi tabel
@Composable
fun SectionTitle(title: String, color: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
    )
}


// Composable untuk Header (Gambar, Judul, Tombol Pemasukan/Pengeluaran)
@Composable
fun HeaderContent(navController: NavController) {
    // ... (Kode HeaderContent tidak berubah)
    val textColorPrimary = Color(0xFF1E5B8A)
    val pemasukanColor = Color(0xFFB0D4E2)

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
            onClick = { navController.navigate("home") },
            modifier = Modifier.align(Alignment.TopStart)
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "Data Keuangan Masjid",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = textColorPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))


}

// PERBAIKAN: Ubah KeuanganTable agar bisa dipakai untuk Pemasukan dan Pengeluaran
@Composable
fun KeuanganTable(
    data: List<PemasukanEntry>,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    isPemasukan: Boolean // Parameter baru untuk menentukan warna
) {
    val context = LocalContext.current

    // Tentukan warna berdasarkan tipe tabel
    val tableHeaderColor = if (isPemasukan) Color(0xFFCDEEFE) else Color(0xFFFFDDE0)
    val borderColor = if (isPemasukan) Color(0xFFB0D4E2) else Color(0xFFE5B0B0)

    val evenRowColor = Color(0xFFFFFFFF)
    val oddRowColor = Color(0xFFF8FBFF)

    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        Box(
            modifier = Modifier
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .horizontalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.width(700.dp)) { // Beri lebar minimum
                // --- Header Tabel ---
                Row(
                    Modifier
                        .background(tableHeaderColor)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell(text = "No", weight = 0.5f, title = true)
                    TableCell(text = "Keterangan", weight = 1.5f, title = true)
                    TableCell(text = "Jumlah", weight = 1.5f, title = true, alignment = TextAlign.End)
                    TableCell(text = "Waktu", weight = 2f, title = true)
                    TableCell(text = "Bukti", weight = 1f, title = true)
                    TableCell(text = "Update", weight = 1f, title = true)
                }

                // --- Baris Data ---
                data.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.background(if (index % 2 == 0) evenRowColor else oddRowColor),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableCell(text = (index + 1).toString(), weight = 0.5f)
                        TableCell(text = item.keterangan, weight = 1.5f)

                        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
                            maximumFractionDigits = 0
                        }
                        TableCell(
                            text = formatter.format(item.jumlah),
                            weight = 1.5f,
                            alignment = TextAlign.End
                        )

                        TableCell(text = item.waktu, weight = 2f)

                        TableCell(
                            text = item.namaBukti,
                            weight = 1f,
                            isLink = true,
                            onClick = {
                                Toast.makeText(context, "Lihat bukti untuk ${item.keterangan}", Toast.LENGTH_SHORT).show()
                            }
                        )

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { onEditClick(item.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                            }
                            IconButton(onClick = { onDeleteClick(item.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Composable Bantuan untuk Sel (tidak ada perubahan)
@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    alignment: TextAlign = TextAlign.Center,
    title: Boolean = false,
    isLink: Boolean = false,
    onClick: () -> Unit = {}
) {
    // ... (kode TableCell tidak berubah)
    val textColor = if (isLink) Color(0xFF0000EE) else Color.Black
    val fontWeight = if (title) FontWeight.Bold else FontWeight.Normal
    val textDecoration = if (isLink) TextDecoration.Underline else TextDecoration.None
    val modifier = if (isLink) Modifier.clickable(onClick = onClick) else Modifier

    Text(
        text = text,
        modifier = modifier
            .border(0.5.dp, Color(0xFFE0E0E0))
            .weight(weight)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        fontWeight = fontWeight,
        textAlign = alignment,
        fontSize = 14.sp,
        color = textColor,
        textDecoration = textDecoration,
        overflow = TextOverflow.Ellipsis
    )
}


// --- Preview dan Bottom App Bar (Tidak Ada Perubahan Signifikan) ---
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun ReadKeuanganScreenWithTablePreview() {
    ReadKeuanganScreen(
        navController = rememberNavController(),
        onNavigate = {}, onAddClick = {}, onUploadClick = {}, onEditClick = {}, onDeleteClick = {}
    )
}

@Composable
private fun KeuanganBottomAppBar(onNavigate: (String) -> Unit, currentRoute: String) {
    // ... (kode bottom bar tidak berubah)
    Surface(shadowElevation = 8.dp, color = Color(0xFFF8F8F8)) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(label = "Home", icon = Icons.Default.Home, onClick = { onNavigate("home") })
            BottomNavItem(label = "Inventaris", icon = Icons.Outlined.Inventory, onClick = { onNavigate("inventaris_graph") })
            BottomNavItem(label = "Kegiatan", icon = Icons.Default.List, onClick = { onNavigate("kegiatan_graph") })
            BottomNavItem(label = "Keuangan", icon = Icons.Outlined.Paid, isSelected = true, onClick = { /* Sedang di sini */ })
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(label: String, icon: ImageVector, onClick: () -> Unit, isSelected: Boolean = false) {
    // ... (kode bottom nav item tidak berubah)
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
