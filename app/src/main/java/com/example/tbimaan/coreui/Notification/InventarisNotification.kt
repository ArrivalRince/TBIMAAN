package com.example.tbimaan.coreui.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.tbimaan.R
import com.example.tbimaan.coreui.viewmodel.InventarisEntry

// ID unik untuk channel dan notifikasi inventaris
private const val INVENTORY_CHANNEL_ID = "inventaris_check_channel"
private const val INVENTORY_NOTIFICATION_ID = 102 // Gunakan ID yang berbeda dari notifikasi keuangan

object InventarisNotification {

    /**
     * Fungsi utama untuk menampilkan notifikasi pengecekan inventaris.
     * @param context Context aplikasi.
     * @param oldItems Daftar barang yang usianya lebih dari 3 bulan.
     */
    fun showInventoryCheckNotification(
        context: Context,
        oldItems: List<InventarisEntry>
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Buat channel notifikasi (wajib untuk Android 8.0 Oreo ke atas)
        createNotificationChannel(notificationManager)

        // Buat konten notifikasi
        val title = "Pengecekan Inventaris Berkala"
        val content: String
        val notificationStyle: NotificationCompat.Style

        if (oldItems.size == 1) {
            // Jika hanya satu barang
            content = "Barang \"${oldItems.first().namaBarang}\" sudah lebih dari 3 bulan. Saatnya melakukan pengecekan."
            notificationStyle = NotificationCompat.BigTextStyle().bigText(content)
        } else {
            // Jika lebih dari satu barang, buat ringkasan
            content = "${oldItems.size} barang sudah lebih dari 3 bulan. Saatnya diperiksa."
            val inboxStyle = NotificationCompat.InboxStyle()
                .setBigContentTitle(title)
                .setSummaryText("Total ${oldItems.size} barang perlu diperiksa")

            // Tampilkan beberapa nama barang di notifikasi
            oldItems.take(5).forEach { item -> // Batasi hingga 5 item agar tidak terlalu panjang
                inboxStyle.addLine("• ${item.namaBarang}")
            }
            notificationStyle = inboxStyle
        }

        // Bangun notifikasi
        val notification = NotificationCompat.Builder(context, INVENTORY_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_imaan) // Ganti dengan ikon notifikasi Anda
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(notificationStyle) // Gunakan style yang sudah dibuat (BigText atau Inbox)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Notifikasi akan hilang saat di-tap
            .build()

        // Tampilkan notifikasi
        notificationManager.notify(INVENTORY_NOTIFICATION_ID, notification)
    }

    /**
     * Fungsi untuk menghapus notifikasi jika tidak ada barang yang perlu diperiksa.
     * @param context Context aplikasi.
     */
    fun cancelInventoryCheckNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(INVENTORY_NOTIFICATION_ID)
    }

    /**
     * Membuat channel notifikasi. Ini aman untuk dipanggil berulang kali.
     */
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Peringatan Inventaris"
            val descriptionText = "Channel untuk notifikasi pengecekan inventaris berkala."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(INVENTORY_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}