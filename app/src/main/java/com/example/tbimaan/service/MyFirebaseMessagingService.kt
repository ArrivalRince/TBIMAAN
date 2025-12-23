package com.example.tbimaan.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.tbimaan.MainActivity
import com.example.tbimaan.R
import com.example.tbimaan.model.SessionManager
import com.example.tbimaan.network.ApiClient
import com.example.tbimaan.network.FcmTokenRequest
import com.example.tbimaan.network.FcmTokenResponse
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"


        private const val CHANNEL_ID = "general_notification"
        private const val CHANNEL_NAME = "TBIMAAN Notification"
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Token refreshed: $token")

        val sessionManager = SessionManager(applicationContext)
        val userId = sessionManager.idUser

        if (userId != null) {
            sendTokenToBackend(userId, token)
        } else {
            Log.d(TAG, "User belum login, skip kirim token")
        }
    }


    private fun sendTokenToBackend(userId: Int, token: String) {
        val request = FcmTokenRequest(
            id_user = userId,
            fcm_token = token
        )

        ApiClient.instance.updateFcmToken(request)
            .enqueue(object : Callback<FcmTokenResponse> {
                override fun onResponse(
                    call: Call<FcmTokenResponse>,
                    response: Response<FcmTokenResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "✅ FCM token berhasil dikirim ke backend")
                    } else {
                        Log.e(TAG, "❌ Gagal kirim token: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<FcmTokenResponse>, t: Throwable) {
                    Log.e(TAG, "❌ Error kirim token: ${t.message}")
                }
            })
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "📩 Notifikasi diterima: ${message.data}")

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "Notifikasi TBIMAAN"

        val body = message.notification?.body
            ?: message.data["message"]
            ?: message.data["body"]
            ?: "Anda memiliki notifikasi baru"

        val notifType = message.data["type"] ?: "general"

        showNotification(title, body, notifType)
    }


    private fun showNotification(title: String, body: String, type: String) {
        createChannelIfNeeded()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notif_type", type)
        }

        val requestCode = System.currentTimeMillis().toInt()

        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationId = System.currentTimeMillis().toInt()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notificationId, notification)

        Log.d(TAG, "✅ Notifikasi ditampilkan (ID=$notificationId)")
    }


    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (existingChannel == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifikasi Inventaris, Kegiatan, dan Keuangan"
                }

                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
