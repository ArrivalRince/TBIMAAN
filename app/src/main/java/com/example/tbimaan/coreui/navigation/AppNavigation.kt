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
import com.example.tbimaan.coreui.screen.Home.LandingPageScreen
import com.example.tbimaan.coreui.screen.Home.LandingPage2Screen // <<< IMPORT BARU

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Aplikasi dimulai dari halaman landing pertama
    NavHost(navController = navController, startDestination = "landing") {

        // Rute untuk halaman landing pertama
        composable("landing") {
            LandingPageScreen(
                onGetStartedClick = {

                    navController.navigate("landing2")
                },
                onLoginClick = {

                    navController.navigate("home") {
                        popUpTo("landing") { inclusive = true }
                    }
                }
            )
        }

        // RUTE BARU: Untuk halaman landing kedua
        composable("landing2") {
            LandingPage2Screen(
                onNextClick = {

                    navController.navigate("home") {

                        popUpTo("landing") { inclusive = true }
                    }
                }
            )
        }

        // Rute untuk Halaman Utama (Home)
        composable("home") {
            HomeScreen(
                onInventarisClick = { navController.navigate(INVENTARIS_GRAPH_ROUTE) },
                onKeuanganClick = { navController.navigate(KEUANGAN_GRAPH_ROUTE) },
                onKegiatanClick = { navController.navigate("kegiatan_graph") }
            )
        }

        // --- Grup navigasi modul (tidak ada perubahan) ---
        keuanganNavGraph(navController)
        inventarisNavGraph(navController)
        navigation(startDestination = "read_kegiatan", route = "kegiatan_graph") {
            composable("read_kegiatan") {
                DummyScreen(screenName = "Halaman Kegiatan")
            }
        }
    }
}

// Fungsi dummy sementara (tidak ada perubahan)
@Composable
private fun DummyScreen(screenName: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = screenName)
    }
}
