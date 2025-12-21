package com.example.tbimaan.coreui.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.tbimaan.R
import java.text.NumberFormat
import java.util.Locale

private const val KEUANGAN_CHANNEL_ID = "keuangan_warning_channel_v2"
private const val KEUANGAN_NOTIFICATION_ID = 101

object KeuanganNotification {

    fun showBudgetWarningNotification(
        context: Context,
        totalPemasukan: Double,
        totalPengeluaran: Double
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(notificationManager)

        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
            maximumFractionDigits = 0
        }

        val selisih = totalPengeluaran - totalPemasukan

        // Buat konten notifikasi
        val title = "Peringatan Keuangan Masjid"
        val content =
            "Pengeluaran telah melebihi pemasukan sebesar ${formatter.format(selisih)}. Mohon lakukan evaluasi."

        val defaultSoundUri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, KEUANGAN_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_keuangan_warning) // ikon vektor WAJIB
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))

            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(defaultSoundUri)

            .setAutoCancel(true)
            .build()

        notificationManager.notify(KEUANGAN_NOTIFICATION_ID, notification)
    }

    fun cancelBudgetWarningNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(KEUANGAN_NOTIFICATION_ID)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Peringatan Keuangan"
            val descriptionText =
                "Channel untuk notifikasi peringatan defisit keuangan masjid."
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(
                KEUANGAN_CHANNEL_ID,
                name,
                importance
            ).apply {
                description = descriptionText
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }
}
