package ash.appopen.opener

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel1 = NotificationChannel(CHANNEL_1_ID, "channel_1", 4)
            channel1.description = "download notification channel"
            (getSystemService(NotificationManager::class.java) as NotificationManager).createNotificationChannel(
                channel1
            )
        }
    }

    companion object {
        const val CHANNEL_1_ID = "1"
    }
}