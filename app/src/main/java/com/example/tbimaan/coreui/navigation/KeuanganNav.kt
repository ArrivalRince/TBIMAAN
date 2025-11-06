package com.example.tbimaan.coreui.navigation

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.tbimaan.coreui.screen.Keuangan.CreateKeuanganScreen
import com.example.tbimaan.coreui.screen.Keuangan.PemasukanEntry // <<< IMPORT DATA CLASS
import com.example.tbimaan.coreui.screen.Keuangan.ReadKeuanganScreen
import com.example.tbimaan.coreui.screen.Keuangan.UpdateKeuanganScreen // <<< IMPORT LAYAR UPDATE

const val KEUANGAN_GRAPH_ROUTE = "keuangan_graph"

// Objek untuk mengelola data dummy agar bisa diakses dari sini
object KeuanganDummyData {
    private val pemasukan = listOf(
        PemasukanEntry("P001", "Donasi", 250000.0, "18 Agustus 2025"),
        PemasukanEntry("P002", "Infaq", 150000.0, "24 Agustus 2025"),
        PemasukanEntry("P003", "Infaq", 180000.0, "26 Agustus 2025"),
        PemasukanEntry("P004", "Donasi", 200000.0, "30 Agustus 2025")
    )
    private val pengeluaran = listOf(
        PemasukanEntry("K001", "Biaya Listrik Bulanan", 350000.0, "20 Agustus 2025"),
        PemasukanEntry("K002", "Perbaikan Sound System", 750000.0, "22 Agustus 2025"),
        PemasukanEntry("K003", "Konsumsi Acara Maulid", 450000.0, "25 Agustus 2025")
    )
    private val allData = pemasukan + pengeluaran

    fun getById(id: String): PemasukanEntry? {
        return allData.find { it.id == id }
    }
}


fun NavGraphBuilder.keuanganNavGraph(navController: NavController) {
    navigation(
        startDestination = "read_keuangan",
        route = KEUANGAN_GRAPH_ROUTE
    ) {
        val onNavigate: (String) -> Unit = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }

        composable("read_keuangan") {
            val context = LocalContext.current
            ReadKeuanganScreen(
                navController = navController,
                onNavigate = onNavigate,
                onAddClick = { navController.navigate("create_keuangan") },
                onUploadClick = {
                    Toast.makeText(context, "Upload clicked!", Toast.LENGTH_SHORT).show()
                },
                // ===== PERBAIKAN UTAMA DI SINI =====
                onEditClick = { id ->
                    // 1. Cari data dummy berdasarkan ID yang diklik
                    val entryToEdit = KeuanganDummyData.getById(id)

                    // 2. Jika data ditemukan, lakukan navigasi
                    if (entryToEdit != null) {
                        navController.navigate(
                            // 3. Bangun route dengan data yang sesuai
                            "update_keuangan/${entryToEdit.keterangan}/${entryToEdit.jumlah}/${entryToEdit.waktu}"
                        )
                    } else {
                        // Jika data tidak ditemukan (seharusnya tidak terjadi dengan data dummy)
                        Toast.makeText(context, "Data dengan ID $id tidak ditemukan!", Toast.LENGTH_SHORT).show()
                    }
                },
                onDeleteClick = { id ->
                    Toast.makeText(context, "Hapus item ID: $id", Toast.LENGTH_SHORT).show()
                }
            )
        }

        composable("create_keuangan") {
            CreateKeuanganScreen(
                navController = navController,
                onNavigate = onNavigate,
                onSave = { navController.popBackStack() }
            )
        }

        // ===== ROUTE BARU UNTUK HALAMAN UPDATE =====
        composable(
            // Definisikan route dan argumennya
            route = "update_keuangan/{keterangan}/{jumlah}/{waktu}",
            arguments = listOf(
                navArgument("keterangan") { type = NavType.StringType },
                navArgument("jumlah") { type = NavType.StringType },
                navArgument("waktu") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Ambil argumen dari route
            val keterangan = backStackEntry.arguments?.getString("keterangan") ?: ""
            val jumlah = backStackEntry.arguments?.getString("jumlah") ?: ""
            val waktu = backStackEntry.arguments?.getString("waktu") ?: ""

            // Tampilkan layar Update dengan data yang sudah diambil
            UpdateKeuanganScreen(
                navController = navController,
                onNavigate = onNavigate,
                onUpdateClick = {
                    // Logika untuk menyimpan perubahan bisa ditambahkan di sini
                    navController.popBackStack() // Kembali ke halaman read setelah "Simpan"
                },
                keteranganAwal = keterangan,
                jumlahAwal = jumlah,
                waktuAwal = waktu
            )
        }
    }
}
