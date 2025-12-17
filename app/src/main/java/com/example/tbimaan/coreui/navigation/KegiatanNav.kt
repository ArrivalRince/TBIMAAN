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
                launchSingleTop = true
            }
        }

        // =======================
        // READ KEGIATAN
        // =======================
        composable("read_kegiatan") {
            ReadKegiatanScreen(
                navController = navController,
                onNavigate = onNavigate,
                onAddClick = {
                    navController.navigate("create_kegiatan")
                },
                onEditClick = { route ->
                    navController.navigate(route)
                }
            )
        }

        // =======================
        // CREATE KEGIATAN
        // =======================
        composable("create_kegiatan") {
            val kegiatanViewModel: KegiatanViewModel = viewModel()

            CreateKegiatanScreen(
                navController = navController,
                viewModel = kegiatanViewModel
            )
        }

        // =======================
        // UPDATE KEGIATAN
        // =======================
        composable(
            route = "update_kegiatan/{idKegiatan}/{nama}/{tanggal}/{lokasi}/{penanggungjawab}/{deskripsi}/{status}/{foto}",
            arguments = listOf(
                navArgument("idKegiatan") { type = NavType.IntType },
                navArgument("nama") { type = NavType.StringType },
                navArgument("tanggal") { type = NavType.StringType },
                navArgument("lokasi") { type = NavType.StringType },
                navArgument("penanggungjawab") { type = NavType.StringType },
                navArgument("deskripsi") { type = NavType.StringType },
                navArgument("status") { type = NavType.StringType },
                navArgument("foto") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->

            val args = backStackEntry.arguments!!

            UpdateKegiatanScreen(
                navController = navController,
                idKegiatan = args.getInt("idKegiatan"),
                onNavigate = onNavigate,
                namaAwal = args.getString("nama").orEmpty(),
                tanggalAwal = args.getString("tanggal").orEmpty(),
                lokasiAwal = args.getString("lokasi").orEmpty(),
                penanggungjawabAwal = args.getString("penanggungjawab").orEmpty(),
                deskripsiAwal = args.getString("deskripsi").orEmpty(),
                statusAwal = args.getString("status").orEmpty(),
                fotoAwal = args.getString("foto")
            )
        }
    }
}