package com.example.storyapp.view.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.model.UserPreference
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class StoryAppWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        return StoryRemoteViewsFactory(this.applicationContext, appWidgetId)
    }
}

class StoryRemoteViewsFactory(private val context: Context, private val appWidgetId: Int) :
    RemoteViewsService.RemoteViewsFactory {

    private val stories = ArrayList<Story>()
    private val storyBitmaps = HashMap<Int, Bitmap>()
    private var job: Job? = null
    private val pref: UserPreference by lazy {
        UserPreference.getInstance(context.dataStore)
    }


    override fun onCreate() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            val token = pref.getToken().first()
            val newStories = fetchData(token)
            for (i in newStories.indices) {
                val imageUrl = newStories[i].photoUrl
                val conn = withContext(Dispatchers.IO) {
                    URL(imageUrl).openConnection()
                } as HttpURLConnection
                conn.readTimeout = 10000 // milliseconds
                conn.connectTimeout = 15000 // milliseconds
                conn.requestMethod = "GET"
                conn.doInput = true

                val responseCode = conn.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: $responseCode")
                }

                val bitmap: Bitmap? = try {
                    val inputStream = conn.inputStream
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    Log.e("StoryAppWidget", "Error reading image stream", e)
                    null
                } finally {
                    conn.disconnect()
                }

                if (bitmap != null) {
                    storyBitmaps[i] = bitmap
                    withContext(Dispatchers.Main) {
                        stories.clear()
                        stories.addAll(newStories)
                        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(
                            appWidgetId,
                            R.id.stackWidget
                        )
                    }
                }
            }
        }
    }

    private fun fetchData(token: String): List<Story> {
        val response = ApiConfig.getApiService().getAllWidgetStory("Bearer $token" ).execute()
        val stories = if (response.isSuccessful) {
            response.body()?.listStory ?: emptyList()
        } else {
            emptyList()
        }

        if (stories.isEmpty()) {
            Log.d("StoryAppWidget", "fetchData() returned an empty list")
        } else {
            Log.d("StoryAppWidget", "fetchData() returned a list with ${stories.size} items")
        }

        return stories
    }

    override fun onDataSetChanged() {

    }

    override fun onDestroy() {
        job?.cancel()
        runBlocking {
            job?.join()
        }
    }

    override fun getCount(): Int = stories.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)
        rv.setTextViewText(R.id.tv_widget_name, stories[position].name)
        rv.setTextViewText(R.id.tv_widget_description, stories[position].description)
        storyBitmaps[position]?.let {
            rv.setImageViewBitmap(R.id.iv_widget_photo, it)
        }
        return rv
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_loading_item)
    }

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}