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

fun NavGraphBuilder.kegiatanNavGraph(
    navController: NavController
) {

    navigation(
        startDestination = "read_kegiatan",
        route = KEGIATAN_GRAPH_ROUTE
    ) {

        val onNavigate: (String) -> Unit = { route ->
            navController.navigate(route) {
                launchSingleTop = true
            }
        }

        // =====================
        // READ KEGIATAN
        // =====================
        composable("read_kegiatan") {
            val kegiatanViewModel: KegiatanViewModel = viewModel()

            ReadKegiatanScreen(
                navController = navController,
                viewModel = kegiatanViewModel,
                onAddClick = {
                    navController.navigate("create_kegiatan")
                }
            )
        }

        // =====================
        // CREATE KEGIATAN
        // =====================
        composable("create_kegiatan") {
            val kegiatanViewModel: KegiatanViewModel = viewModel()

            CreateKegiatanScreen(
                navController = navController,
                viewModel = kegiatanViewModel
            )
        }

        // =====================
        // UPDATE KEGIATAN
        // =====================
        composable(
            route = "update_kegiatan/{id}/{nama}/{tanggal}/{lokasi}/{penanggungjawab}/{deskripsi}/{status}/{foto}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
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
        ) { entry ->

            val kegiatanViewModel: KegiatanViewModel = viewModel()
            val args = entry.arguments!!

            UpdateKegiatanScreen(
                navController = navController,
                viewModel = kegiatanViewModel,
                idKegiatan = args.getString("id").orEmpty(),
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