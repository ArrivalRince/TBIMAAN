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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.example.tbimaan.coreui.components.EditButton
import com.example.tbimaan.coreui.navigation.KEGIATAN_GRAPH_ROUTE
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
    val penceramah: String,
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
    var list by remember { mutableStateOf<List<KegiatanUi>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // ================= LOAD DATA =================
    LaunchedEffect(Unit) {
        ApiClient.instance.getKegiatan()
            .enqueue(object : Callback<List<KegiatanDto>> {
                override fun onResponse(
                    call: Call<List<KegiatanDto>>,
                    response: Response<List<KegiatanDto>>
                ) {
                    if (response.isSuccessful) {
                        list = response.body().orEmpty().mapNotNull { dto ->
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
                                penceramah = dto.penceramah ?: "",
                                deskripsi = dto.deskripsi ?: "",
                                status = dto.status_kegiatan ?: "Akan Datang",
                                fotoUrl = foto
                            )
                        }
                        isLoading = false
                    } else {
                        error = "Gagal memuat data"
                        isLoading = false
                    }
                }

                override fun onFailure(call: Call<List<KegiatanDto>>, t: Throwable) {
                    Log.e("ReadKegiatan", t.message ?: "error")
                    error = "Koneksi gagal"
                    isLoading = false
                }
            })
    }

    // ================= SEARCH =================
    var query by remember { mutableStateOf("") }
    val filtered = list.filter { it.nama.contains(query, ignoreCase = true) }

    // ================= UI =================
    Scaffold(
        floatingActionButton = { AddFAB(onClick = onAddClick) },
        bottomBar = {
            BottomBar(onNavigate, KEGIATAN_GRAPH_ROUTE)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9F9F9))
        ) {

            // HEADER
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
                isLoading -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text(error!!, color = Color.Red)
                    }
                }

                filtered.isEmpty() -> {
                    Text(
                        "Tidak ada kegiatan",
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        textAlign = TextAlign.Center
                    )
                }

                else -> {
                    LazyColumn {
                        items(filtered) { kegiatan ->
                            KegiatanCard(kegiatan) {

                                val route =
                                    "update_kegiatan/" +
                                            kegiatan.id + "/" +
                                            Uri.encode(kegiatan.nama) + "/" +
                                            Uri.encode(kegiatan.tanggal) + "/" +
                                            Uri.encode(kegiatan.lokasi) + "/" +
                                            Uri.encode(kegiatan.penceramah) + "/" +
                                            Uri.encode(kegiatan.deskripsi) + "/" +
                                            Uri.encode(kegiatan.status) + "/" +
                                            Uri.encode(kegiatan.fotoUrl ?: "")

                                navController.navigate(route)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= CARD =================
@Composable
fun KegiatanCard(
    kegiatan: KegiatanUi,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {

            // ===== FOTO (lebih kecil) =====
            if (!kegiatan.fotoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = kegiatan.fotoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.kajianbulanan),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.padding(14.dp)
            ) {

                // ===== NAMA + STATUS =====
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = kegiatan.nama,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF1E1E1E)
                    )

                    Spacer(Modifier.width(8.dp))

                    val isSelesai = kegiatan.status == "Selesai"

                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelesai)
                                    Color(0xFFE8F5E9)
                                else
                                    Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = kegiatan.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelesai)
                                Color(0xFF2E7D32)
                            else
                                Color(0xFFC62828)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // ===== DETAIL TANPA EMOJI =====
                Text(
                    text = "Tanggal: ${kegiatan.tanggal}",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "Tempat: ${kegiatan.lokasi}",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "Deskripsi: ${kegiatan.deskripsi}",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 2
                )

                Spacer(Modifier.height(12.dp))

                EditButton(onClick = onEdit)
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(icon, null, tint = color)
        Text(label, fontSize = 11.sp, color = color)
    }
}
