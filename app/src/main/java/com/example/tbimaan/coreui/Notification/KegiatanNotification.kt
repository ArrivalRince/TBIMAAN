package com.example.tbimaan.coreui.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.tbimaan.R
import com.example.tbimaan.coreui.viewmodel.KegiatanEntry // Sesuaikan path jika berbeda

private const val KEGIATAN_CHANNEL_ID = "kegiatan_reminder_channel"
private const val KEGIATAN_NOTIFICATION_ID = 103 // ID unik, beda dari Inventaris (102) & Keuangan (101)

object KegiatanNotification {


    fun showEventReminderNotification(context: Context, upcomingEvents: List<KegiatanEntry>) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val title = "Pengingat Kegiatan Besok"
        val content: String
        val notificationStyle: NotificationCompat.Style

        if (upcomingEvents.size == 1) {
            val eventName = upcomingEvents.first().nama
            content = "Jangan lupa, kegiatan \"$eventName\" akan diadakan besok."
            notificationStyle = NotificationCompat.BigTextStyle().bigText(content)
        } else {
            content = "${upcomingEvents.size} kegiatan akan diadakan besok. Jangan sampai terlewat!"
            val inboxStyle = NotificationCompat.InboxStyle()
                .setBigContentTitle(title)
                .setSummaryText("Total ${upcomingEvents.size} kegiatan besok")
            upcomingEvents.take(5).forEach { event ->
                inboxStyle.addLine("• ${event.nama}")
            }
            notificationStyle = inboxStyle
        }

        // BUAT IKON VEKTOR BARU: res/drawable -> New -> Vector Asset -> cari "event" -> beri nama "ic_stat_event"
        val notification = NotificationCompat.Builder(context, KEGIATAN_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_calendar_month_24) // WAJIB ikon vektor
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(notificationStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(KEGIATAN_NOTIFICATION_ID, notification)
    }

    fun cancelEventReminderNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(KEGIATAN_NOTIFICATION_ID)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pengingat Kegiatan"
            val descriptionText = "Channel untuk notifikasi pengingat kegiatan H-1."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(KEGIATAN_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}