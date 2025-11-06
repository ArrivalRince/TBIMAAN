package com.example.tbimaan.coreui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.tbimaan.coreui.screen.Inventaris.CreateInventarisScreen
import com.example.tbimaan.coreui.screen.Inventaris.ReadInventarisScreen
import com.example.tbimaan.coreui.screen.Inventaris.UpdateInventarisScreen

const val INVENTARIS_GRAPH_ROUTE = "inventaris_graph"

fun NavGraphBuilder.inventarisNavGraph(navController: NavController) {
    navigation(
        startDestination = "read_inventaris",
        route = INVENTARIS_GRAPH_ROUTE
    ) {
        // Fungsi navigasi ini sudah benar dan akan kita teruskan ke halaman Update
        val onNavigate: (String) -> Unit = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        composable("read_inventaris") {
            ReadInventarisScreen(
                navController = navController,
                onNavigate = onNavigate,
                onAddClick = { navController.navigate("create_inventaris") },
                onEditClick = { itemId ->
                    navController.navigate("update_inventaris/$itemId")
                }
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

        composable("update_inventaris/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")

            UpdateInventarisScreen(
                navController = navController,
                itemId = itemId ?: "ID_TIDAK_DITEMUKAN",
                onBackClick = { navController.popBackStack() },
                onUpdateClick = { navController.popBackStack() },
                onDeleteClick = { navController.popBackStack() },
                onNavigate = onNavigate // Fungsi onNavigate sudah tersambung dengan benar
            )
        }
    }
}
