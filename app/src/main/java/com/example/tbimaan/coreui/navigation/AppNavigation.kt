package com.example.tbimaan.coreui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tbimaan.coreui.screen.Home.HomeScreen
import com.example.tbimaan.coreui.screen.Home.LandingPage2Screen
import com.example.tbimaan.coreui.screen.Home.LandingPageScreen
import com.example.tbimaan.coreui.screen.Home.SettingScreen
import com.example.tbimaan.coreui.screen.Home.SignInScreen // <<< PERBAIKAN 1: GANTI IMPORT
import com.example.tbimaan.coreui.screen.Home.SignUpScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "landing") {

        composable("landing") {
            LandingPageScreen(
                onGetStartedClick = { navController.navigate("landing2") },
                onLoginClick = {
                    // Di sini parameter tetap onLoginClick, tapi route-nya kita ubah
                    navController.navigate("signin") { // <<< PERBAIKAN 2: UBAH ROUTE
                        popUpTo("landing") { inclusive = true }
                    }
                }
            )
        }

        composable("landing2") {
            LandingPage2Screen(
                onNextClick = { navController.navigate("signin") } // <<< PERBAIKAN 3: UBAH ROUTE
            )
        }

        // PERBAIKAN 4: UBAH NAMA ROUTE DAN PANGGIL FUNGSI YANG BENAR
        composable("signin") {
            SignInScreen(
                onSignInClick = {
                    navController.navigate("home") {
                        popUpTo("landing") { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate("signup")
                }
            )
        }

        // ======== SIGN UP ========
        composable("signup") {
            SignUpScreen(
                onSignUpClick = {
                    // Setelah Sign Up berhasil → kembali ke Sign In
                    navController.navigate("signin") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onBackClick = {
                    // Tombol Back di halaman Sign Up → kembali ke Sign In
                    navController.navigate("signin") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        // --- PERBAIKAN PADA ROUTE "home" ---
        composable("home") {
            HomeScreen(
                onInventarisClick = { navController.navigate(INVENTARIS_GRAPH_ROUTE) },
                onKeuanganClick = { navController.navigate(KEUANGAN_GRAPH_ROUTE) },
                onKegiatanClick = { navController.navigate(KEGIATAN_GRAPH_ROUTE) },
                onSettingsClick = { navController.navigate("settings") } // <<< 2. SAMBUNGKAN onSettingsClick
            )
        }

        // --- 3. TAMBAHKAN ROUTE BARU UNTUK SETTINGS ---
        composable("settings") {
            SettingScreen(navController = navController)
        }
        // ===========================================

        // --- GRUP NAVIGASI MODUL (Tidak berubah) ---
        keuanganNavGraph(navController)
        inventarisNavGraph(navController)
        kegiatanNavGraph(navController)
        }

}

