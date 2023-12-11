package com.example.tp4

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        // On s'assure que l'API est suffisamment récente
        // pour supporter les canaux de notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel1 = NotificationChannel(
                CHANNEL_1_ID,
                "Channel 1",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "This is Channel 1"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)




        }
    }

    companion object {
        const val CHANNEL_1_ID = "channel1"

    }
}