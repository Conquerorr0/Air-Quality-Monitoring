package com.fatihaltuntas.airqualityindex.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.fatihaltuntas.airqualityindex.R
import com.fatihaltuntas.airqualityindex.DAO.FirebaseHelper
import com.fatihaltuntas.airqualityindex.model.SensorData
import com.fatihaltuntas.airqualityindex.util.PreferencesManager
import com.fatihaltuntas.airqualityindex.view.MainActivity
import kotlinx.coroutines.*

class NotificationService : Service() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var firebaseHelper: FirebaseHelper
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    companion object {
        private const val CHANNEL_ID = "air_quality_monitoring_channel"
        private var notificationId = 1
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        preferencesManager = PreferencesManager(this)
        firebaseHelper = FirebaseHelper()
        createNotificationChannel()
        startForeground()
        startMonitoring()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Hava Kalitesi İzleme"
            val descriptionText = "Hava kalitesi ve sensör verileri bildirimleri"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startForeground() {
        val notification = createNotification(
            "Hava Kalitesi İzleme",
            "Hava kalitesi ve sensör verileri izleniyor"
        )
        startForeground(1, notification)
    }

    private fun startMonitoring() {
        job = scope.launch {
            while (isActive) {
                try {
                    checkSensorData()
                    val frequency = preferencesManager.getNotificationFrequency()
                    val delay = when (frequency) {
                        "hourly" -> 60L * 60L * 1000L // 1 saat
                        "daily" -> 24L * 60L * 60L * 1000L // 24 saat
                        "immediate" -> 5L * 60L * 1000L // 5 dakika
                        else -> 5L * 60L * 1000L // varsayılan 5 dakika
                    }
                    delay(delay)
                } catch (e: Exception) {
                    e.printStackTrace()
                    delay(5L * 60L * 1000L) // Hata durumunda 5 dakika bekle
                }
            }
        }
    }

    private fun checkSensorData() {
        firebaseHelper.getLatestData { data ->
            val frequency = preferencesManager.getNotificationFrequency()
            if (frequency == "immediate" || shouldCheckPeriodically()) {
                checkAirQuality(data)
                checkTemperature(data)
                checkGasLevels(data)
            }
        }
    }

    private var lastCheckTime = 0L

    private fun shouldCheckPeriodically(): Boolean {
        val currentTime = System.currentTimeMillis()
        val frequency = preferencesManager.getNotificationFrequency()
        val interval = when (frequency) {
            "hourly" -> 60L * 60L * 1000L // 1 saat
            "daily" -> 24L * 60L * 60L * 1000L // 24 saat
            else -> 5L * 60L * 1000L // 5 dakika
        }

        if (currentTime - lastCheckTime >= interval) {
            lastCheckTime = currentTime
            return true
        }
        return false
    }

    private fun checkAirQuality(data: SensorData) {
        if (!preferencesManager.isNotificationEnabled("air_quality")) return

        val airQuality = data.air_quality.ppm
        val status = data.air_quality.status
        if (status != "iyi") {
            showNotification(
                "Hava Kalitesi Uyarısı",
                "Hava kalitesi $status seviyesinde (${airQuality} ppm)"
            )
        }
    }

    private fun checkTemperature(data: SensorData) {
        if (!preferencesManager.isNotificationEnabled("temperature")) return

        val temp = data.climate.temperature
        if (temp > 30 || temp < 10) {
            showNotification(
                "Sıcaklık Uyarısı",
                "Sıcaklık ${temp}°C seviyesinde"
            )
        }
    }

    private fun checkGasLevels(data: SensorData) {
        if (!preferencesManager.isNotificationEnabled("gas")) return

        val lpg = data.gases.lpg_ppm
        val co = data.gases.co_ppm
        val smoke = data.gases.smoke_ppm

        if (lpg > 1000) {
            showNotification(
                "LPG Seviyesi Uyarısı",
                "LPG seviyesi yüksek: ${lpg} ppm"
            )
        }

        if (co > 50) {
            showNotification(
                "CO Seviyesi Uyarısı",
                "Karbon monoksit seviyesi tehlikeli: ${co} ppm"
            )
        }

        if (smoke > 100) {
            showNotification(
                "Duman Yoğunluğu Uyarısı",
                "Duman yoğunluğu yüksek: ${smoke} ppm"
            )
        }
    }

    private fun createNotification(title: String, message: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun showNotification(title: String, message: String) {
        val notification = createNotification(title, message)
        notificationManager.notify(notificationId++, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        scope.cancel()
        stopForeground(true)
    }
} 