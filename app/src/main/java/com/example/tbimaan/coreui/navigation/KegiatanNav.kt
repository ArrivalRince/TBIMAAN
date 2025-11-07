package com.example.tbimaan.coreui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.tbimaan.coreui.screen.Kegiatan.CreateKegiatanScreen
import com.example.tbimaan.coreui.screen.Kegiatan.ReadKegiatanScreen
import com.example.tbimaan.coreui.screen.Kegiatan.UpdateKegiatanScreen

const val KEGIATAN_GRAPH_ROUTE = "kegiatan_graph"

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
                onEditClick = { route -> navController.navigate(route) } // ✅ ubah ini
            )
        }

        // 🔹 Halaman Tambah Kegiatan Baru
        composable("create_kegiatan") {
            CreateKegiatanScreen(
                navController = navController,
                onNavigate = onNavigate,
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            "update_kegiatan/{nama}/{tanggal}/{waktu}/{lokasi}/{penceramah}/{deskripsi}/{status}",
            arguments = listOf(
                navArgument("nama") { type = NavType.StringType },
                navArgument("tanggal") { type = NavType.StringType },
                navArgument("waktu") { type = NavType.StringType },
                navArgument("lokasi") { type = NavType.StringType },
                navArgument("penceramah") { type = NavType.StringType },
                navArgument("deskripsi") { type = NavType.StringType },
                navArgument("status") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            UpdateKegiatanScreen(
                navController = navController,
                namaAwal = backStackEntry.arguments?.getString("nama") ?: "",
                tanggalAwal = backStackEntry.arguments?.getString("tanggal") ?: "",
                waktuAwal = backStackEntry.arguments?.getString("waktu") ?: "",
                lokasiAwal = backStackEntry.arguments?.getString("lokasi") ?: "",
                penceramahAwal = backStackEntry.arguments?.getString("penceramah") ?: "",
                deskripsiAwal = backStackEntry.arguments?.getString("deskripsi") ?: "",
                statusAwal = backStackEntry.arguments?.getString("status") ?: "",
                onBackClick = { navController.popBackStack() },
                onUpdateClick = { navController.popBackStack() },
                onDeleteClick = { navController.popBackStack() }
            )
        }
    }
}