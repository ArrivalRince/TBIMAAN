package com.example.tbimaan.coreui.screen.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tbimaan.R
import com.example.tbimaan.coreui.screen.Inventaris.ConsistentBottomNavBar
import com.example.tbimaan.coreui.viewmodel.InventarisViewModel
import com.example.tbimaan.coreui.viewmodel.KegiatanViewModel
import com.example.tbimaan.coreui.viewmodel.KeuanganViewModel
import com.example.tbimaan.model.SessionManager
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    currentRoute: String?,
    onInventarisClick: () -> Unit,
    onKeuanganClick: () -> Unit,
    onKegiatanClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    val namaMasjidSession =
        sessionManager.namaMasjid
            ?.takeIf { it.isNotBlank() }
            ?.let { "Masjid $it" }
            ?: "Masjid"

    val inventarisVM: InventarisViewModel = viewModel()
    val keuanganVM: KeuanganViewModel = viewModel()
    val kegiatanVM: KegiatanViewModel = viewModel()

    val inventarisList by inventarisVM.inventarisList
    val pemasukanList by keuanganVM.pemasukanList
    val pengeluaranList by keuanganVM.pengeluaranList
    val kegiatanList by kegiatanVM.kegiatanList

    LaunchedEffect(sessionManager.idUser) {
        sessionManager.idUser?.let { idUser ->
            inventarisVM.loadInventaris(idUser, context)
            keuanganVM.loadData(idUser, context)
            kegiatanVM.loadKegiatan(context, idUser)
        }
    }

    val kegiatanCount = kegiatanList.size
    val totalInventarisJenis = inventarisList.size
    val totalPemasukan = pemasukanList.sumOf { it.jumlah }
    val totalPengeluaran = pengeluaranList.sumOf { it.jumlah }

    Scaffold(
        bottomBar = {
            ConsistentBottomNavBar(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF2F4F7))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ===== HEADER IMAGE =====
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.masjid),
                        contentDescription = "Header Masjid",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(14.dp)
                            .size(44.dp)
                            .background(Color(0xCCFFFFFF), RoundedCornerShape(22.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan",
                            tint = Color(0xFF2C3E50)
                        )
                    }
                }

                Spacer(modifier = Modifier.height((-36).dp))


                MasjidInfoCard(
                    nama = namaMasjidSession,
                )


                Spacer(modifier = Modifier.height(16.dp))

                DashboardGrid(
                    inventarisValue = totalInventarisJenis,
                    pemasukanValue = totalPemasukan,
                    pengeluaranValue = totalPengeluaran,
                    kegiatanValue = kegiatanCount,
                    onInventarisClick = onInventarisClick,
                    onKeuanganClick = onKeuanganClick,
                    onKegiatanClick = onKegiatanClick
                )
            }
        }
    }
}


@Composable
private fun MasjidInfoCard(nama: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(nama, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D3B66))
            }

            Surface(
                modifier = Modifier.size(44.dp),
                color = Color(0xFFEAF6FB),
                shape = RoundedCornerShape(22.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_imaan),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardGrid(
    inventarisValue: Int,
    pemasukanValue: Double,
    pengeluaranValue: Double,
    kegiatanValue: Int,
    onInventarisClick: () -> Unit,
    onKeuanganClick: () -> Unit,
    onKegiatanClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardCard("Inventaris", inventarisValue.toString(), "Barang terdata", 190.dp, onInventarisClick)
            DashboardCard("Pengeluaran", formatRupiah(pengeluaranValue), "Dana keluar", 110.dp, onKeuanganClick)
        }

        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardCard("Pemasukan", formatRupiah(pemasukanValue), "Dana masuk", 110.dp, onKeuanganClick)
            DashboardCard("Kegiatan", kegiatanValue.toString(), "Agenda berjalan", 190.dp, onKegiatanClick)
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    value: String,
    subtitle: String,
    height: Dp,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F0FF) // biru lebih hidup
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = Color(0xFF5A7DB8)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Text(
                text = value,
                fontSize = if (height > 150.dp) 36.sp else 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0A2E5C)
            )
        }
    }
}


private fun formatRupiah(amount: Double): String =
    NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
        maximumFractionDigits = 0
    }.format(amount)