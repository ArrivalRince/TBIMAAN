package com.example.tbimaan.coreui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.navArgument
import com.example.tbimaan.coreui.screen.Kegiatan.CreateKegiatanScreen
import com.example.tbimaan.coreui.screen.Kegiatan.ReadKegiatanScreen
import com.example.tbimaan.coreui.screen.Kegiatan.UpdateKegiatanScreen
import com.example.tbimaan.coreui.viewmodel.KegiatanViewModel

fun NavGraphBuilder.kegiatanNavGraph(navController: NavController) {
    navigation(
        startDestination = "read_kegiatan",
        route = KEGIATAN_GRAPH_ROUTE
    ) {
        val onNavigate: (String) -> Unit = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }

        // 🔹 Halaman Daftar Kegiatan
        composable("read_kegiatan") {
            ReadKegiatanScreen(
                navController = navController,
                onNavigate = onNavigate,
                onAddClick = { navController.navigate("create_kegiatan") },
                onEditClick = { route -> navController.navigate(route) }
            )
        }

        // 🔹 Halaman Tambah Kegiatan Baru
        composable("create_kegiatan") {
            // Ambil atau buat ViewModel untuk kegiatan (menggunakan Compose viewModel())
            val kegiatanViewModel: KegiatanViewModel = viewModel()

            CreateKegiatanScreen(
                navController = navController,
                viewModel = kegiatanViewModel
            )
        }

        // 🔹 Halaman Update Kegiatan
        //    route sekarang: update_kegiatan/{idKegiatan}/{nama}/{tanggal}/{waktu}/{lokasi}/{penceramah}/{deskripsi}/{status}
        composable(
            route = "update_kegiatan/{idKegiatan}/{nama}/{tanggal}/{waktu}/{lokasi}/{penceramah}/{deskripsi}/{status}",
            arguments = listOf(
                navArgument("idKegiatan") { type = NavType.IntType },
                navArgument("nama") { type = NavType.StringType },
                navArgument("tanggal") { type = NavType.StringType },
                navArgument("waktu") { type = NavType.StringType },
                navArgument("lokasi") { type = NavType.StringType },
                navArgument("penceramah") { type = NavType.StringType },
                navArgument("deskripsi") { type = NavType.StringType },
                navArgument("status") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val idKegiatan = backStackEntry.arguments?.getInt("idKegiatan") ?: 0
            val namaAwal = backStackEntry.arguments?.getString("nama") ?: ""
            val tanggalAwal = backStackEntry.arguments?.getString("tanggal") ?: ""
            val waktuAwal = backStackEntry.arguments?.getString("waktu") ?: ""
            val lokasiAwal = backStackEntry.arguments?.getString("lokasi") ?: ""
            val penceramahAwal = backStackEntry.arguments?.getString("penceramah") ?: ""
            val deskripsiAwal = backStackEntry.arguments?.getString("deskripsi") ?: ""
            val statusAwal = backStackEntry.arguments?.getString("status") ?: ""

            UpdateKegiatanScreen(
                navController = navController,
                idKegiatan = idKegiatan,                       // ✅ sekarang id dikirim
                onBackClick = { navController.popBackStack() },
                onUpdateClick = { navController.popBackStack() },
                onDeleteClick = { navController.popBackStack() },
                onNavigate = onNavigate,
                namaAwal = namaAwal,
                tanggalAwal = tanggalAwal,
                waktuAwal = waktuAwal,
                lokasiAwal = lokasiAwal,
                penceramahAwal = penceramahAwal,
                deskripsiAwal = deskripsiAwal,
                statusAwal = statusAwal
            )
        }
    }
}
