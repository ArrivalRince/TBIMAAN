package com.example.tbimaan.coreui.screen.Keuangan

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.tbimaan.R
import com.example.tbimaan.coreui.navigation.KEUANGAN_GRAPH_ROUTE
import java.io.File

// ===== PERBAIKAN UTAMA: PINDAHKAN FUNGSI INI KE LUAR COMPOSABLE =====
// Dengan berada di sini, fungsi ini bisa diakses oleh file lain dalam package yang sama.
fun getTempUri(context: Context): Uri {
    val tempFile = File.createTempFile("temp_image_${System.currentTimeMillis()}", ".jpg", context.cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
}
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateKeuanganScreen(
    navController: NavController,
    onNavigate: (String) -> Unit,
    onSave: () -> Unit
) {
    var keterangan by remember { mutableStateOf("") }
    var jumlah by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    val options = listOf("Pemasukan", "Pengeluaran")
    var selectedTipe by remember { mutableStateOf(options[0]) }
    var isTipeExpanded by remember { mutableStateOf(false) }
    val textColorPrimary = Color(0xFF1E5B8A)

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }

    // Launcher untuk memilih gambar dari galeri
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imageUri = uri
        }
    )

    // Launcher untuk mengambil foto dari kamera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (!success) {
                imageUri = null
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* Kosong */ },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = textColorPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = { KeuanganBottomAppBar(onNavigate = onNavigate, currentRoute = KEUANGAN_GRAPH_ROUTE) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ... (Kode UI lainnya tidak perlu diubah, sudah benar)
            Image(
                painter = painterResource(id = R.drawable.masjid),
                contentDescription = "Header Masjid",
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "Tambahkan Data Keuangan Baru",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = textColorPrimary,
                modifier = Modifier.padding(top = 24.dp).align(Alignment.CenterHorizontally)
            )
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(value = keterangan, onValueChange = { keterangan = it }, label = { Text("Keterangan") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
                OutlinedTextField(value = jumlah, onValueChange = { jumlah = it }, label = { Text("Jumlah (contoh: 50000)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
                OutlinedTextField(value = tanggal, onValueChange = { tanggal = it }, label = { Text("Tanggal (DD/MM/YY)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp))
                ExposedDropdownMenuBox(expanded = isTipeExpanded, onExpandedChange = { isTipeExpanded = !isTipeExpanded }, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedTipe,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipe Transaksi") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTipeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    ExposedDropdownMenu(expanded = isTipeExpanded, onDismissRequest = { isTipeExpanded = false }) {
                        options.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = { selectedTipe = option; isTipeExpanded = false })
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF0F5F9))
                        .border(1.dp, Color(0xFFDDEEFF), RoundedCornerShape(16.dp))
                        .clickable { showImageSourceDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Upload Icon", tint = textColorPrimary.copy(alpha = 0.8f), modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Upload Bukti", color = textColorPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Bukti Terpilih",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f, fill = false))
                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BC0DE))
                ) {
                    Text("SIMPAN", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Pilih Sumber Gambar") },
            text = { Text("Pilih dari Galeri atau ambil foto baru?") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    val tempUri = getTempUri(context)
                    imageUri = tempUri
                    cameraLauncher.launch(tempUri)
                }) { Text("Kamera") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    galleryLauncher.launch("image/*")
                }) { Text("Galeri") }
            }
        )
    }
}

// ... (Sisa file seperti Preview dan BottomAppBar biarkan sama)
@Composable
private fun KeuanganBottomAppBar(onNavigate: (String) -> Unit, currentRoute: String) {
    Surface(shadowElevation = 8.dp, color = Color(0xFFF8F8F8)) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 4.dp),
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

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun CreateKeuanganScreenPreview() {
    CreateKeuanganScreen(navController = rememberNavController(), onNavigate = {}, onSave = {})
}
