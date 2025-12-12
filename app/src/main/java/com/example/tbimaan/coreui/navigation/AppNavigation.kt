package com.example.tbimaan.coreui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tbimaan.coreui.screen.Home.EditProfileScreen
import com.example.tbimaan.coreui.screen.Home.HomeScreen
import com.example.tbimaan.coreui.screen.Home.InfoAplikasiScreen
import com.example.tbimaan.coreui.screen.Home.KeamananAkunScreen // <<< 1. IMPORT HALAMAN BARU
import com.example.tbimaan.coreui.screen.Home.LandingPage2Screen
import com.example.tbimaan.coreui.screen.Home.LandingPageScreen
import com.example.tbimaan.coreui.screen.Home.ProfileScreen
import com.example.tbimaan.coreui.screen.Home.SettingScreen
import com.example.tbimaan.coreui.screen.Home.SignInScreen
import com.example.tbimaan.coreui.screen.Home.SignUpScreen
import com.example.tbimaan.coreui.viewmodel.InventarisViewModel // <-- IMPORT ViewModel Inventaris
import com.example.tbimaan.coreui.viewmodel.KeuanganViewModel     // <-- IMPORT ViewModel Keuangan

const val HOME_GRAPH_ROUTE = "home"
const val INVENTARIS_GRAPH_ROUTE = "inventaris_graph"
const val KEUANGAN_GRAPH_ROUTE = "keuangan_graph"
const val KEGIATAN_GRAPH_ROUTE = "kegiatan_graph"


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val keuanganViewModel: KeuanganViewModel = viewModel()
    val inventarisViewModel: InventarisViewModel = viewModel()


    NavHost(navController = navController, startDestination = "landing") {

        // Halaman Awal & Autentikasi
        composable("landing") {
            LandingPageScreen(
                onGetStartedClick = { navController.navigate("landing2") },
                onLoginClick = { navController.navigate("signin") { popUpTo("landing") { inclusive = true } } }
            )
        }
        composable("landing2") { LandingPage2Screen(onNextClick = { navController.navigate("signin") }) }
        composable("signin") {
            SignInScreen(
                onSignInClick = { navController.navigate("home") { popUpTo(0) { inclusive = true } } },
                onSignUpClick = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignUpClick = { navController.navigate("signin") { popUpTo("signup") { inclusive = true } } },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Halaman Utama
        composable("home") {
            HomeScreen(
                onInventarisClick = { navController.navigate(INVENTARIS_GRAPH_ROUTE) },
                onKeuanganClick = { navController.navigate(KEUANGAN_GRAPH_ROUTE) },
                onKegiatanClick = { navController.navigate(KEGIATAN_GRAPH_ROUTE) },
                onSettingsClick = { navController.navigate("settings") }
            )
        }

        // --- GRUP HALAMAN PENGATURAN (SETTINGS) ---
        composable("settings") {
            SettingScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("edit_profile") {
            EditProfileScreen(navController = navController)
        }
        composable("info_aplikasi") {
            InfoAplikasiScreen(navController = navController)
        }
        // --- 2. DAFTARKAN ROUTE BARU UNTUK KEAMANAN AKUN ---
        composable("keamanan_akun") {
            KeamananAkunScreen(navController = navController)
        }
        // ================================================

        // Grup Navigasi Modul
        inventarisNavGraph(navController = navController, viewModel = inventarisViewModel)
        kegiatanNavGraph(navController)
        keuanganNavGraph(navController)
    }
}
