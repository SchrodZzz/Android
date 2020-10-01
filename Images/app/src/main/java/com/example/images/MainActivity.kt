package com.example.images

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.IOException
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.URL

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TOKEN = "16117721-2e67b66d5a0bbb325595eab42"
        private const val QUERY = "science"
        private const val COUNT = 100
    }

    private var imagesList: List<Image>? = null

    private class ImagePreviewLoader(activity: MainActivity) :
        AsyncTask<String, Void, List<Image>>() {

        private val activityRef = WeakReference(activity)

        override fun doInBackground(vararg params: String): List<Image> {
            val imagesList = mutableListOf<Image>()
            val url = "https://pixabay.com/api/?key=${TOKEN}&per_page=${COUNT}&safesearch=true&order=popular&category=${QUERY}"
            Log.i("123", url)
            try {
                InputStreamReader(
                    URL(url)
                        .openConnection()
                        .getInputStream()
                ).use {
                    val parser = JSONParser()
                    val root = parser.parse(it.readText()) as JSONObject
                    val items = root["hits"] as JSONArray
                    for (item in items) {
                        item as JSONObject
                        val text = item["tags"] as String
                        val imageUrl = item["largeImageURL"] as String
                        imagesList.add(Image(text, imageUrl))
                    }
                }
            } catch (e: IOException) {
                Log.e("connect", "Connection failed: ${e.message}", e)
                e.printStackTrace()
            }
            return imagesList
        }

        override fun onPostExecute(result: List<Image>) {
            val activity = activityRef.get()
            activity?.onLoadCompleted(result)
        }
    }

    internal fun onLoadCompleted(result: List<Image>) {
        imagesList = result
        val viewManager = LinearLayoutManager(this@MainActivity)
        val myAdapter = ImageAdapter(result) {
            val intent = Intent(this@MainActivity, ImageActivity::class.java)
            intent.putExtra("url", it.url)
            startActivity(intent)
        }
        if (myAdapter.itemCount == 0) {
            Toast.makeText(this, "No results", Toast.LENGTH_LONG).show()
        }
        my_recycler_view.apply {
            layoutManager = viewManager
            adapter = myAdapter
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        if (imagesList == null) {
            ImagePreviewLoader(this).execute(QUERY, TOKEN)
        }
    }

}
