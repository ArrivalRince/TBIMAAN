package com.example.tbimaan.coreui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.tbimaan.coreui.screen.Keuangan.CreateKeuanganScreen
import com.example.tbimaan.coreui.screen.Keuangan.ReadKeuanganScreen

const val KEUANGAN_GRAPH_ROUTE = "keuangan_graph"

fun NavGraphBuilder.keuanganNavGraph(navController: NavController) {
    navigation(
        startDestination = "read_keuangan",
        route = KEUANGAN_GRAPH_ROUTE
    ) {
        // Fungsi navigasi yang bisa dipakai ulang di dalam graph ini
        val onNavigate: (String) -> Unit = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }

        composable("read_keuangan") {
            ReadKeuanganScreen(
                navController = navController,
                onNavigate = onNavigate, // <<< PERBARUI DI SINI
                onAddClick = { navController.navigate("create_keuangan") },
                onUploadClick = { /* ... */ }
            )
        }

        composable("create_keuangan") {
            CreateKeuanganScreen(
                navController = navController,
                onNavigate = onNavigate, // <<< PERBARUI DI SINI
                onSave = { navController.popBackStack() }
            )
        }
    }
}