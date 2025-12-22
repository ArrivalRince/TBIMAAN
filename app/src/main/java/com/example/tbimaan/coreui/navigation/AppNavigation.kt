package com.example.tbimaan.coreui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tbimaan.coreui.screen.Home.EditProfileScreen
import com.example.tbimaan.coreui.screen.Home.HomeScreen
import com.example.tbimaan.coreui.screen.Home.InfoAplikasiScreen
import com.example.tbimaan.coreui.screen.Home.KeamananAkunScreen
import com.example.tbimaan.coreui.screen.Home.LandingPage2Screen
import com.example.tbimaan.coreui.screen.Home.LandingPageScreen
import com.example.tbimaan.coreui.screen.Home.ProfileScreen
import com.example.tbimaan.coreui.screen.Home.SettingScreen
import com.example.tbimaan.coreui.screen.Home.SignInScreen
import com.example.tbimaan.coreui.screen.Home.SignUpScreen
import com.example.tbimaan.coreui.viewmodel.InventarisViewModel
import com.example.tbimaan.coreui.viewmodel.KeuanganViewModel
import com.example.tbimaan.model.SessionManager

// ================= ROUTES =================
const val HOME_GRAPH_ROUTE = "home"
const val INVENTARIS_GRAPH_ROUTE = "inventaris_graph"
const val KEUANGAN_GRAPH_ROUTE = "keuangan_graph"
const val KEGIATAN_GRAPH_ROUTE = "kegiatan_graph"

// ================= NAVIGATION ROOT =================
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // 👉 Untuk highlight navbar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Session
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Start destination dinamis
    val startDestination = if (sessionManager.isLoggedIn) {
        HOME_GRAPH_ROUTE
    } else {
        "landing"
    }

    // Shared ViewModel
    val keuanganViewModel: KeuanganViewModel = viewModel()
    val inventarisViewModel: InventarisViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ================= LANDING & AUTH =================
        composable("landing") {
            LandingPageScreen(
                onGetStartedClick = { navController.navigate("landing2") },
                onLoginClick = { navController.navigate("signin") }
            )
        }

        composable("landing2") {
            LandingPage2Screen(
                onNextClick = { navController.navigate("signin") }
            )
        }

        composable("signin") {
            SignInScreen(
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

        // ================= HOME =================
        composable(HOME_GRAPH_ROUTE) {
            HomeScreen(
                navController = navController,
                currentRoute = currentRoute,
                onInventarisClick = {
                    navController.navigate(INVENTARIS_GRAPH_ROUTE)
                },
                onKeuanganClick = {
                    navController.navigate(KEUANGAN_GRAPH_ROUTE)
                },
                onKegiatanClick = {
                    navController.navigate(KEGIATAN_GRAPH_ROUTE)
                },
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }

        // ================= SETTINGS =================
        composable("settings") { SettingScreen(navController = navController) }
        composable("profile") { ProfileScreen(navController = navController) }
        composable("edit_profile") { EditProfileScreen(navController = navController) }
        composable("info_aplikasi") { InfoAplikasiScreen(navController = navController) }
        composable("keamanan_akun") { KeamananAkunScreen(navController = navController) }

        // ================= MODULE NAV GRAPH =================
        inventarisNavGraph(
            navController = navController,
            viewModel = inventarisViewModel
        )

        kegiatanNavGraph(navController)

        keuanganNavGraph(
            navController = navController
        )
    }
}