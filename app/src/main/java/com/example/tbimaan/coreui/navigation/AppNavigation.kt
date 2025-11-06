package com.example.tbimaan.coreui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation // Import ini mungkin tidak lagi digunakan, tapi tidak apa-apa
import androidx.navigation.compose.rememberNavController
import com.example.tbimaan.coreui.screen.Home.HomeScreen
import com.example.tbimaan.coreui.screen.Home.LandingPageScreen
import com.example.tbimaan.coreui.screen.Home.LandingPage2Screen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "landing") {

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

        composable("landing2") {
            LandingPage2Screen(
                onNextClick = {
                    navController.navigate("home") {
                        popUpTo("landing") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onInventarisClick = { navController.navigate(INVENTARIS_GRAPH_ROUTE) },
                onKeuanganClick = { navController.navigate(KEUANGAN_GRAPH_ROUTE) },
                onKegiatanClick = { navController.navigate(KEGIATAN_GRAPH_ROUTE) } // Rute ini sudah benar
            )
        }

        // --- GRUP NAVIGASI MODUL ---
        keuanganNavGraph(navController)
        inventarisNavGraph(navController)

        // ===== PERBAIKAN UTAMA DI SINI =====
        // Ganti blok navigasi dummy dengan panggilan ke graph navigasi Kegiatan yang sebenarnya.
        kegiatanNavGraph(navController)
        // ===================================
    }
}

// Fungsi dummy sementara (tidak ada perubahan)
@Composable
private fun DummyScreen(screenName: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = screenName)
    }
}
