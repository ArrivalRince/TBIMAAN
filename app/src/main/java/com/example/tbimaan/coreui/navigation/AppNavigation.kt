package com.example.tbimaan.coreui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tbimaan.coreui.screen.Home.*
import com.example.tbimaan.coreui.viewmodel.InventarisViewModel
import com.example.tbimaan.coreui.viewmodel.KeuanganViewModel
import com.example.tbimaan.model.SessionManager // <-- IMPORT PENTING

// Const val untuk route tidak berubah
const val HOME_GRAPH_ROUTE = "home"
const val INVENTARIS_GRAPH_ROUTE = "inventaris_graph"
const val KEUANGAN_GRAPH_ROUTE = "keuangan_graph"
const val KEGIATAN_GRAPH_ROUTE = "kegiatan_graph"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // =======================================================================
    // ===                 PERBAIKAN UTAMA DAN FINAL ADA DI SINI           ===
    // =======================================================================

    // 1. Buat instance SessionManager menggunakan Context dari Composable
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // 2. Tentukan halaman awal aplikasi berdasarkan status login dari SessionManager
    val startDestination = if (sessionManager.isLoggedIn) {
        HOME_GRAPH_ROUTE // Jika sudah login, langsung ke Home
    } else {
        "landing" // Jika belum, mulai dari Landing Page
    }

    // 3. Ambil instance ViewModel untuk dibagikan ke NavGraph
    val keuanganViewModel: KeuanganViewModel = viewModel()
    val inventarisViewModel: InventarisViewModel = viewModel()

    // 4. Gunakan 'startDestination' yang sudah dinamis
    NavHost(navController = navController, startDestination = startDestination) {

        // Halaman Awal & Autentikasi
        composable("landing") {
            LandingPageScreen(
                onGetStartedClick = { navController.navigate("landing2") },
                onLoginClick = { navController.navigate("signin") }
            )
        }
        composable("landing2") {
            LandingPage2Screen(onNextClick = { navController.navigate("signin") })
        }
        composable("signin") {
            SignInScreen(
                // Setelah sign-in berhasil, hapus semua halaman sebelumnya dan jadikan Home sebagai root
                onSignInClick = {
                    navController.navigate(HOME_GRAPH_ROUTE) {
                        popUpTo(0)
                    }
                },
                onSignUpClick = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignUpClick = {
                    navController.navigate("signin") {
                        popUpTo("landing")
                    }
                },
                onSignInClick = { navController.navigate("signin") }
            )
        }

        // Halaman Utama
        composable(HOME_GRAPH_ROUTE) {
            HomeScreen(
                onInventarisClick = { navController.navigate(INVENTARIS_GRAPH_ROUTE) },
                onKeuanganClick = { navController.navigate(KEUANGAN_GRAPH_ROUTE) },
                onKegiatanClick = { navController.navigate(KEGIATAN_GRAPH_ROUTE) },
                onSettingsClick = { navController.navigate("settings") }
            )
        }

        // Grup Halaman Pengaturan
        composable("settings") { SettingScreen(navController = navController) }
        composable("profile") { ProfileScreen(navController = navController) }
        composable("edit_profile") { EditProfileScreen(navController = navController) }
        composable("info_aplikasi") { InfoAplikasiScreen(navController = navController) }
        composable("keamanan_akun") { KeamananAkunScreen(navController = navController) }

        // Grup Navigasi Modul
        inventarisNavGraph(navController = navController, viewModel = inventarisViewModel)
        kegiatanNavGraph(navController)
        keuanganNavGraph(navController)
    }
}
