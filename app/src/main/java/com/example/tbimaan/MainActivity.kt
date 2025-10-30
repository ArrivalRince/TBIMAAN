package com.example.tbimaan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.tbimaan.coreui.navigation.AppNavigation // <<< IMPORT YANG BARU
import com.example.tbimaan.ui.theme.TBIMAANTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Membuat aplikasi bisa menggunakan seluruh layar (status bar jadi transparan)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            TBIMAANTheme {
                // MainActivity sekarang hanya bertanggung jawab untuk memanggil AppNavigation
                AppNavigation()
            }
        }
    }
}
