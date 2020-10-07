package com.example.images.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.images.MainActivity
import com.example.images.cache.Cache
import com.example.images.data.ImageInfo
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL


class InternetService : IntentService("ru.ifmo.nefedov.task4.imageslist.services.InternetService") {

    private val apiUrl: String
        get() = "https://pixabay.com/api/?key=${TOKEN}&per_page=${COUNT}&safesearch=true&order=popular&category=${QUERY}"


    override fun onHandleIntent(intent: Intent?) {
        Log.i(LOG_KEY, "onHandleIntent..")

        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText("Loading image")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        Thread.sleep(5000)

        if (intent == null) {
            Log.e(LOG_KEY, "Intent is null")
            return
        }

        val mode = intent.action
        when (mode) {
            DOWNLOAD_PREVIEW_LIST -> downloadPreviewList(intent)
            DOWNLOAD_FULLSCREEN -> {
                val url = intent.getStringExtra(DOWNLOAD_EXTRA_KEY)
                if (url == null) {
                    Log.e(LOG_KEY, "Url fo downloading fullscreen is null")
                    return
                }
                downloadFullscreen(intent, url)
            }
            else -> Log.e(LOG_KEY, "Mode is null or has undefined value")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )

            if (manager == null) {
                Log.e(LOG_KEY, "notification manager is null")
                return
            }

            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun downloadSingleImage(url: String) {
        Cache.simpleCathe.getOrPut(url) {
            val imageBytes = URL(url).openStream().readBytes()
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }
    }

    private fun downloadInfoList(): List<ImageInfo> {
        val result = URL(apiUrl)
            .openConnection()
            .getInputStream()
            .reader()
            .readText()
        leafPage()
        Log.i(">> link", apiUrl)

        val jsonTmp = JSONObject(result)
        val jsonResult = jsonTmp["hits"] as JSONArray
        return List(jsonResult.length()) {
            val jsonImage = jsonResult.getJSONObject(it)
            ImageInfo(
                bigUrl = jsonImage.getString("largeImageURL"),
                smallUrl = jsonImage.getString("webformatURL"),
                description = jsonImage.getString("tags").nullIfNull()
            )
        }
    }

    private fun String.nullIfNull(): String? = if (this == "null") null else this

    private fun Intent.sendResult(filler: Intent.() -> Unit) {
        filler()
        Log.i(LOG_KEY, "sendBroadcast..")
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(this)

        stopForeground(Service.STOP_FOREGROUND_REMOVE)
    }

    private fun downloadFullscreen(intent: Intent, url: String) {
        try {
            downloadSingleImage(url)
            intent.sendResult {
                putExtra(RESULT_KEY, url)
            }
        } catch (e: IOException) {
            intent.sendResult {
                putExtra(FAIL_KEY, e)
            }
        }
    }

    private fun downloadPreviewList(intent: Intent) {
        try {
            val imageInfoList = downloadInfoList()
            imageInfoList.forEach { downloadSingleImage(it.smallUrl) }
            intent.sendResult {
                putExtra(RESULT_KEY, ArrayList(imageInfoList))
            }
        } catch (e: IOException) {
            intent.sendResult {
                putExtra(FAIL_KEY, e)
            }
        }
    }

    companion object {
        private const val LOG_KEY = "InternetService"

        private const val CHANNEL_ID = "InternetService.Chanel"
        private const val NOTIFICATION_ID = 1119191929


        const val DOWNLOAD_PREVIEW_LIST = "download_list"
        const val DOWNLOAD_FULLSCREEN = "download_fullscreen"

        private const val DOWNLOAD_EXTRA_KEY = "download_extra"

        private fun runDownloading(context: Context, mod: String, extra: String? = null) {
            Log.i(LOG_KEY, "runDownloading..")
            val intent = Intent(context, InternetService::class.java).apply {
                action = mod
                extra?.let { putExtra(DOWNLOAD_EXTRA_KEY, it) }
            }

            context.startService(intent)
        }

        fun downloadPreviewList(context: Context) = runDownloading(context, DOWNLOAD_PREVIEW_LIST)

        fun downloadFullscreen(context: Context, url: String) =
            runDownloading(context, DOWNLOAD_FULLSCREEN, url)


        const val RESULT_KEY = "result_value"
        const val FAIL_KEY = "fail_value"


        private const val MAX_PAGE_NUMBER = 100
        private var pageNumber = 1
        private const val TOKEN = "16117721-2e67b66d5a0bbb325595eab42"
        private const val QUERY = "science"
        private const val COUNT = 100

        private fun leafPage(): Unit =
            if (pageNumber == MAX_PAGE_NUMBER) pageNumber = 1 else pageNumber += 1
    }
}