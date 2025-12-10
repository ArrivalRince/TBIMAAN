package com.example.tbimaan.coreui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.tbimaan.coreui.screen.Keuangan.CreateKeuanganScreen
import com.example.tbimaan.coreui.screen.Keuangan.ReadKeuanganScreen
import com.example.tbimaan.coreui.screen.Keuangan.UpdateKeuanganScreen

const val KEUANGAN_GRAPH_ROUTE = "keuangan_graph"

/**
 * Fungsi ini hanya mendefinisikan rute navigasi untuk modul Keuangan.
 * Semua logika klik dan data dummy sudah dihapus karena sekarang dikelola
 * di dalam masing-masing layar (Screen) atau ViewModel.
 */
fun NavGraphBuilder.keuanganNavGraph(navController: NavController) {
    navigation(
        startDestination = "read_keuangan",
        route = KEUANGAN_GRAPH_ROUTE
    ) {

        // 🔹 Rute untuk halaman utama modul keuangan (Read)
        composable("read_keuangan") {
            // =================== PERBAIKAN #1 ===================
            // Panggil ReadKeuanganScreen hanya dengan parameter yang ada.
            // Semua parameter on...Click sudah dihapus dari definisi fungsi.
            ReadKeuanganScreen(
                navController = navController,
                viewModel = viewModel() // Mengambil instance KeuanganViewModel
            )
            // ====================================================
        }

        // 🔹 Rute untuk halaman tambah data (Create)
        composable("create_keuangan") {
            // =================== PERBAIKAN #2 ===================
            // Panggil CreateKeuanganScreen hanya dengan NavController.
            // onNavigate dan onSave sudah tidak ada.
            CreateKeuanganScreen(
                navController = navController
            )
            // ====================================================
        }

        // 🔹 Rute untuk halaman perbarui data (Update)
        composable(
            route = "update_keuangan/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "ID_TIDAK_DIKETAHUI"

            // =================== PERBAIKAN #3 ===================
            // Panggil UpdateKeuanganScreen hanya dengan NavController dan id.
            // onNavigate dan onUpdateClick sudah tidak ada.
            UpdateKeuanganScreen(
                navController = navController,
                id = id
            )
            // ====================================================
        }
    }
}
