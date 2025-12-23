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


fun NavGraphBuilder.keuanganNavGraph(navController: NavController) {
    navigation(
        startDestination = "read_keuangan",
        route = KEUANGAN_GRAPH_ROUTE
    ) {


        composable("read_keuangan") {

            ReadKeuanganScreen(
                navController = navController,
                viewModel = viewModel()
            )
        }


        composable("create_keuangan") {

            CreateKeuanganScreen(
                navController = navController
            )
        }


        composable(
            route = "update_keuangan/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "ID_TIDAK_DIKETAHUI"

            UpdateKeuanganScreen(
                navController = navController,
                id = id
            )
        }
    }
}
