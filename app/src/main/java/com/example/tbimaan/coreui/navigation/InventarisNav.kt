package com.example.tbimaan.coreui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.tbimaan.coreui.screen.Inventaris.CreateInventarisScreen
import com.example.tbimaan.coreui.screen.Inventaris.ReadInventarisScreen

const val INVENTARIS_GRAPH_ROUTE = "inventaris_graph"

fun NavGraphBuilder.inventarisNavGraph(navController: NavController) {
    navigation(
        startDestination = "read_inventaris",
        route = INVENTARIS_GRAPH_ROUTE
    ) {
        // Fungsi navigasi yang bisa dipakai ulang di dalam graph ini
        val onNavigate: (String) -> Unit = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }

        composable("read_inventaris") {
            ReadInventarisScreen(
                navController = navController,
                onNavigate = onNavigate, // <<< PERBARUI DI SINI
                onAddClick = { navController.navigate("create_inventaris") },
                onEditClick = { /* ... */ }
            )
        }

        composable("create_inventaris") {
            CreateInventarisScreen(
                navController = navController,
                onNavigate = onNavigate,
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
