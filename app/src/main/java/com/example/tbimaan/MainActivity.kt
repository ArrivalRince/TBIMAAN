package com.example.tbimaan

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.tbimaan.coreui.navigation.AppNavigation
import com.example.tbimaan.ui.theme.TBIMAANTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    companion object {
        // Gunakan ID ini juga saat NotificationCompat.Builder(context, CHANNEL_ID)
        const val CHANNEL_ID = "inventory_reminder"
        const val CHANNEL_NAME = "Reminder Inventaris"
        const val CHANNEL_DESC = "Notifikasi pengingat pemeriksaan inventaris"
    }

    private val requestNotifPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            // Kalau denied di Android 13+, notif tidak akan tampil.
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        createNotificationChannel()
        ensureNotificationPermission()

        setContent {
            TBIMAANTheme {
                AppNavigation()
            }
        }

        // Optional: tetap kalau kamu pakai FCM
        createToken()
    }

    private fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            requestNotifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESC
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun createToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("FCM Token: ${task.result}")
                } else {
                    println("Failed to get FCM token: ${task.exception}")
                }
            }
    }
}
