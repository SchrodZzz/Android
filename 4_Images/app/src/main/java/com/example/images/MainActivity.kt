package com.example.images

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.images.data.ImageInfo
import com.example.images.data.SmallImage
import com.example.images.data.convertToSmallImageList
import com.example.images.FullscreenActivity
import com.example.images.State
import com.example.images.showOkDialog
import com.example.images.services.InternetService
import kotlinx.android.synthetic.main.activity_main.*
import com.example.images.imageListView.ImageAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var receiver: BroadcastReceiver
    private lateinit var adapter: ImageAdapter

    private var state = State.WAIT

    private fun setOnWait() {
        state = State.WAIT

        main_progress.visibility = View.VISIBLE
        main_refresh.visibility = View.INVISIBLE
        main_imagePreviewList.visibility = View.VISIBLE
    }

    private fun setOnShow() {
        state = State.SHOW

        main_progress.visibility = View.INVISIBLE
        main_refresh.visibility = View.VISIBLE
        main_imagePreviewList.visibility = View.VISIBLE
    }

    private fun setOnFail() {
        state = State.FAIL

        main_progress.visibility = View.INVISIBLE
        main_refresh.visibility = View.VISIBLE
        main_imagePreviewList.visibility = View.INVISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(LOG_KEY, "onCreate..")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.getParcelable<State>(STATE_KEY)?.let { state = it }

        main_refresh.setOnRefreshListener(::onRefresh)

        val viewManager = LinearLayoutManager(this)
        adapter = ImageAdapter(::openFullscreen)
        main_imagePreviewList.apply {
            layoutManager = viewManager
            adapter = this@MainActivity.adapter
        }

        if (state != State.SHOW) {
            startDownloadingImagePreviewList()
        } else {
            val imageInfoList =
                savedInstanceState?.getParcelableArrayList<ImageInfo>(PREVIEW_LIST_KEY)

            if (imageInfoList == null) {
                Log.e(LOG_KEY, "State is SHOW, but no saved images")
                startDownloadingImagePreviewList()
                return
            }

            setOnShow()

            adapter.setImages(
                imageInfoList.convertToSmallImageList(LOG_KEY)
            )
        }
    }

    private fun onRefresh() {
        if (state != State.WAIT) {
            startDownloadingImagePreviewList()
        }
        main_refresh.isRefreshing = false
    }

    override fun onResume() {
        Log.i(LOG_KEY, "onStart..")
        receiver = MainReceiver()

        val intentFilter = IntentFilter().apply {
            addAction(InternetService.DOWNLOAD_PREVIEW_LIST)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)

        super.onResume()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onPause()
    }

    private fun startDownloadingImagePreviewList() {
        Log.i(LOG_KEY, "startDownloadingImagePreviewList..")
        setOnWait()
        InternetService.downloadPreviewList(this@MainActivity)
    }

    private fun openFullscreen(image: SmallImage) {
        val intent = Intent(this@MainActivity, FullscreenActivity::class.java).apply {
            putExtra(FullscreenActivity.IMAGE_INFO_KEY, image.getImageInfo())
        }
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(STATE_KEY, state)
        val images = adapter.getImages().map { it.getImageInfo() }
        outState.putParcelableArrayList(PREVIEW_LIST_KEY, ArrayList(images))
    }

    private inner class MainReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (state != State.WAIT) {
                return
            }

            Log.i(LOG_KEY, "onReceive..")
            if (intent == null) {
                Log.e(LOG_KEY, "Intent is null")
                return
            }

            if (intent.getSerializableExtra(InternetService.FAIL_KEY) != null) {
                setOnFail()
                showOkDialog(
                    R.string.no_internet_connection_title,
                    R.string.no_internet_connection_text
                )
                return
            }

            setOnShow()

            val images: List<ImageInfo>? =
                intent.getParcelableArrayListExtra(InternetService.RESULT_KEY)

            if (images == null) {
                Log.e(LOG_KEY, "result is null or has undefined value")
                return
            }

            adapter.setImages(
                images.convertToSmallImageList(LOG_KEY)
            )
        }

    }

    companion object {
        private const val LOG_KEY = "MainActivity"

        private const val STATE_KEY = "MainActivity_State"
        private const val PREVIEW_LIST_KEY = "MainActivity_PreviewList"
    }
}
