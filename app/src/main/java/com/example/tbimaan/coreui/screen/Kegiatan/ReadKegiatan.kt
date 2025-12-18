package com.example.tbimaan.coreui.screen.Kegiatan

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.tbimaan.coreui.navigation.KEGIATAN_GRAPH_ROUTE
import com.example.tbimaan.model.SessionManager
import com.example.tbimaan.network.ApiClient
import com.example.tbimaan.network.KegiatanDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// ================= UI MODEL =================
data class KegiatanUi(
    val id: Int,
    val nama: String,
    val tanggal: String,
    val lokasi: String,
    val penanggungjawab: String,
    val deskripsi: String,
    val status: String,
    val fotoUrl: String?
)

@Composable
fun ReadKegiatanScreen(
    navController: NavController,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    var list by remember { mutableStateOf<List<KegiatanUi>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var kegiatanToDelete by remember { mutableStateOf<KegiatanUi?>(null) }

    // ================= LOAD DATA =================
    LaunchedEffect(sessionManager.idUser) {
        val userId = sessionManager.idUser
        if (userId == null) {
            isLoading = false
            error = "Sesi pengguna tidak valid"
            return@LaunchedEffect
        }

        isLoading = true
        ApiClient.instance.getKegiatan()
            .enqueue(object : Callback<List<KegiatanDto>> {
                override fun onResponse(
                    call: Call<List<KegiatanDto>>,
                    response: Response<List<KegiatanDto>>
                ) {
                    isLoading = false
                    if (response.isSuccessful) {
                        list = response.body()
                            .orEmpty()
                            .filter { it.id_user == userId }
                            .mapNotNull { dto ->
                                val id = dto.id_kegiatan ?: return@mapNotNull null

                                val foto = dto.foto_kegiatan?.let {
                                    when {
                                        it.startsWith("http", true) -> it
                                        it.startsWith("/") ->
                                            ApiClient.BASE_URL.removeSuffix("/") + it
                                        else ->
                                            ApiClient.BASE_URL.removeSuffix("/") + "/uploads/$it"
                                    }
                                }

                                KegiatanUi(
                                    id = id,
                                    nama = dto.nama_kegiatan ?: "",
                                    tanggal = dto.tanggal_kegiatan ?: "",
                                    lokasi = dto.lokasi ?: "",
                                    penanggungjawab = dto.penanggungjawab ?: "",
                                    deskripsi = dto.deskripsi ?: "",
                                    status = dto.status_kegiatan ?: "Akan Datang",
                                    fotoUrl = foto
                                )
                            }
                    } else error = "Gagal memuat data"
                }

                override fun onFailure(call: Call<List<KegiatanDto>>, t: Throwable) {
                    isLoading = false
                    error = "Koneksi gagal"
                    Log.e("ReadKegiatan", t.message ?: "error")
                }
            })
    }

    // ================= SEARCH =================
    var query by remember { mutableStateOf("") }
    val filtered = list.filter { it.nama.contains(query, ignoreCase = true) }

    // ================= UI =================
    Scaffold(
        floatingActionButton = { AddFAB(onClick = onAddClick) },
        bottomBar = { BottomBar(onNavigate, KEGIATAN_GRAPH_ROUTE) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9F9F9))
        ) {

            Box(Modifier.fillMaxWidth().height(180.dp)) {
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
                modifier = Modifier.fillMaxWidth().padding(16.dp),
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

                error != null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(error!!, color = Color.Red)
                }

                filtered.isEmpty() -> Text(
                    "Tidak ada kegiatan",
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    textAlign = TextAlign.Center
                )

                else -> LazyColumn {
                    items(filtered) { kegiatan ->
                        KegiatanCard(
                            kegiatan = kegiatan,
                            onEdit = {
                                val route =
                                    "update_kegiatan/" +
                                            kegiatan.id + "/" +
                                            Uri.encode(kegiatan.nama) + "/" +
                                            Uri.encode(kegiatan.tanggal) + "/" +
                                            Uri.encode(kegiatan.lokasi) + "/" +
                                            Uri.encode(kegiatan.penanggungjawab) + "/" +
                                            Uri.encode(kegiatan.deskripsi) + "/" +
                                            Uri.encode(kegiatan.status) + "/" +
                                            Uri.encode(kegiatan.fotoUrl ?: "")
                                navController.navigate(route)
                            },
                            onDelete = {
                                kegiatanToDelete = kegiatan
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // ================= DELETE DIALOG =================
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Yakin ingin menghapus \"${kegiatanToDelete?.nama}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        kegiatanToDelete?.let { k ->
                            ApiClient.instance.deleteKegiatan(k.id.toString())
                                .enqueue(object : Callback<Void> {
                                    override fun onResponse(
                                        call: Call<Void>,
                                        response: Response<Void>
                                    ) {
                                        if (response.isSuccessful) {
                                            list = list.filterNot { it.id == k.id }
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {}
                                })
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Hapus") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

// ================= CARD =================
@Composable
fun KegiatanCard(
    kegiatan: KegiatanUi,
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

            // ===== FOTO =====
            AsyncImage(
                model = kegiatan.fotoUrl ?: R.drawable.kajianbulanan,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                // ===== JUDUL + STATUS =====
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = kegiatan.nama,
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
                        Text(
                            text = kegiatan.status,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                // ===== INFO =====
                Text(
                    text = "Tanggal: ${kegiatan.tanggal}",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "Tempat: ${kegiatan.lokasi}",
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    maxLines = 1
                )
                Text(
                    text = "Penanggungjawab: ${kegiatan.penanggungjawab}",
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    maxLines = 1
                )

                Spacer(Modifier.height(6.dp))

                // ===== DESKRIPSI + AKSI (SEJAJAR) =====
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = kegiatan.deskripsi,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(8.dp))

                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ================= BOTTOM BAR =================
@Composable
fun BottomBar(onNavigate: (String) -> Unit, currentRoute: String) {
    Surface {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomItem("Home", Icons.Default.Home, false) { onNavigate("home") }
            BottomItem("Inventaris", Icons.Outlined.Inventory, false) { onNavigate("inventaris_graph") }
            BottomItem("Kegiatan", Icons.Default.List, true) {}
            BottomItem("Keuangan", Icons.Outlined.Paid, false) { onNavigate("keuangan_graph") }
        }
    }
}

@Composable
fun BottomItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) Color(0xFF1E5B8A) else Color.Gray
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Icon(icon, null, tint = color)
        Text(label, fontSize = 11.sp, color = color)
    }
}