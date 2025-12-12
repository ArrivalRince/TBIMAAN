package com.example.tbimaan.coreui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.tbimaan.coreui.screen.Inventaris.CreateInventarisScreen
import com.example.tbimaan.coreui.screen.Inventaris.ReadInventarisScreen
import com.example.tbimaan.coreui.screen.Inventaris.UpdateInventarisScreen
import com.example.tbimaan.coreui.viewmodel.InventarisViewModel



// File ini sudah benar, asalkan screen-screen yang dipanggilnya juga sudah benar.
fun NavGraphBuilder.inventarisNavGraph(navController: NavController, viewModel: InventarisViewModel) {
    navigation(
        startDestination = "read_inventaris",
        route = INVENTARIS_GRAPH_ROUTE
    ) {
        // Pemanggilan ini SEKARANG sudah cocok dengan ReadInventarisScreen dari Langkah 1
        composable("read_inventaris") {
            ReadInventarisScreen(navController = navController, viewModel = viewModel)
        }

        // Pemanggilan ini sudah cocok dengan CreateInventarisScreen yang sudah kita perbaiki
        composable("create_inventaris") {
            CreateInventarisScreen(navController = navController, viewModel = viewModel)
        }

        // Pemanggilan ini sudah cocok dengan UpdateInventarisScreen yang sudah kita perbaiki
        composable(
            route = "update_inventaris/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            if (id != null) {
                UpdateInventarisScreen(navController = navController, id = id, viewModel = viewModel)
            } else {
                navController.popBackStack()
            }
        }
    }
}
