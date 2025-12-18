package com.example.tbimaan.coreui.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.tbimaan.R
import java.text.NumberFormat
import java.util.Locale

// ID unik untuk channel dan notifikasi
private const val CHANNEL_ID = "keuangan_warning_channel"
private const val NOTIFICATION_ID = 101

object KeuanganNotification {

    fun showBudgetWarningNotification(
        context: Context,
        totalPemasukan: Double,
        totalPengeluaran: Double
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Buat channel notifikasi (wajib untuk Android 8.0 Oreo ke atas)
        createNotificationChannel(notificationManager)

        // Format angka ke format mata uang Rupiah
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
            maximumFractionDigits = 0
        }
        val selisih = totalPengeluaran - totalPemasukan

        // Buat konten notifikasi
        val title = "Peringatan Keuangan Masjid"
        val content = "Pengeluaran telah melebihi pemasukan sebesar ${formatter.format(selisih)}. Mohon lakukan evaluasi."

        // Bangun notifikasi
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_imaan) // Ganti dengan ikon notifikasi Anda
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content)) // Agar teks panjang bisa dibaca
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true) // Membuat notifikasi tidak bisa di-swipe (seperti di gambar)
            .build()

        // Tampilkan notifikasi
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Fungsi untuk menghapus notifikasi jika saldo sudah kembali normal.
     * @param context Context aplikasi.
     */
    fun cancelBudgetWarningNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        // Hanya perlu untuk Android 8.0 (API 26) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Peringatan Keuangan"
            val descriptionText = "Channel untuk notifikasi peringatan budget keuangan."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Daftarkan channel ke sistem
            notificationManager.createNotificationChannel(channel)
        }
    }
}
