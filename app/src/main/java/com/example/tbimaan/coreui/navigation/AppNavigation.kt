package com.example.tbimaan.coreui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.tbimaan.coreui.screen.Home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        // Rute untuk Halaman Utama (sudah benar)
        composable("home") {
            HomeScreen(
                onInventarisClick = {
                    // PERINTAH: Saat kartu inventaris diklik, buka grup rute 'INVENTARIS_GRAPH_ROUTE'
                    navController.navigate(INVENTARIS_GRAPH_ROUTE)
                },
                onKeuanganClick = {
                    navController.navigate(KEUANGAN_GRAPH_ROUTE)
                },
                onKegiatanClick = {
                    navController.navigate("kegiatan_graph")
                }
            )
        }

        // --- Memanggil semua grup navigasi (nested graph) dari sini ---

        // Memanggil grafik navigasi untuk modul Keuangan
        keuanganNavGraph(navController)

        // Memanggil grafik navigasi untuk modul Inventaris
        inventarisNavGraph(navController)

        // Placeholder untuk modul Kegiatan (biarkan untuk nanti)
        navigation(startDestination = "read_kegiatan", route = "kegiatan_graph") {
            composable("read_kegiatan") {
                DummyScreen(screenName = "Halaman Kegiatan")
            }
        }
    }
}

// Fungsi dummy sementara (biarkan seperti ini)
@Composable
private fun DummyScreen(screenName: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = screenName)
    }
}
