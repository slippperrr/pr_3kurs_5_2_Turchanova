package com.example.myplaylist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class SearchActivity : Fragment(R.layout.activity_search) {
    private lateinit var prefs: PrefsManager
    private lateinit var adapter: SongAdapter
    private lateinit var search: EditText
    private lateinit var status: TextView
    private lateinit var progress: ProgressBar
    private lateinit var buttonSearch: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrefsManager(requireContext())
        search = view.findViewById(R.id.editSearch)
        status = view.findViewById(R.id.textStatus)
        progress = view.findViewById(R.id.progressBar)
        buttonSearch = view.findViewById(R.id.buttonSearch)

        adapter = SongAdapter(
            context = requireContext(),
            actionText = "＋",
            showAction = true,
            onAction = { song -> saveSong(song) }
        )
        view.findViewById<ListView>(R.id.listSongs).adapter = adapter

        buttonSearch.setOnClickListener {
            val query = search.text.toString().trim()
            if (query.isEmpty()) {
                status.text = "Введи название песни или исполнителя"
            } else {
                searchSongs(query)
            }
        }

        view.findViewById<Button>(R.id.buttonSaved).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame, SavedSongsActivity())
                .commit()
        }

        search.setText("The Weeknd")
        searchSongs("The Weeknd")
    }

    private fun searchSongs(query: String) {
        progress.visibility = View.VISIBLE
        buttonSearch.isEnabled = false
        status.text = "Загрузка..."
        lifecycleScope.launch {
            try {
                val songs = withContext(Dispatchers.IO) { fetch(query) }
                prefs.saveCatalogJson(songs)
                adapter.replaceData(songs)
                status.text = if (songs.isEmpty()) "Ничего не найдено"
                else "Найдено песен: ${songs.size}. Нажми ＋, чтобы сохранить."
            } catch (e: Exception) {
                status.text = "Ошибка загрузки: ${e.localizedMessage ?: ""}"
            } finally {
                progress.visibility = View.GONE
                buttonSearch.isEnabled = true
            }
        }
    }

    private fun fetch(query: String): List<Song> {
        val term = URLEncoder.encode(query, "UTF-8")
        val url = URL("https://itunes.apple.com/search?term=$term&entity=song&limit=25")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 10000
            readTimeout = 10000
            requestMethod = "GET"
        }
        try {
            if (conn.responseCode != 200) throw Exception("HTTP ${conn.responseCode}")
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val results = JSONObject(body).getJSONArray("results")
            val list = mutableListOf<Song>()
            for (i in 0 until results.length()) {
                val item = results.getJSONObject(i)
                list.add(
                    Song(
                        title = item.optString("trackName", "Без названия"),
                        artist = item.optString("artistName", "—"),
                        album = item.optString("collectionName", ""),
                        artworkUrl = item.optString("artworkUrl100", "")
                            .replace("http://", "https://"),
                        trackId = item.optLong("trackId", 0L)
                    )
                )
            }
            return list
        } finally {
            conn.disconnect()
        }
    }

    private fun saveSong(song: Song) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(requireContext()).songDao().insert(
                    SavedSong(
                        title = song.title,
                        artist = song.artist,
                        album = song.album,
                        artworkUrl = song.artworkUrl
                    )
                )
            }
            status.text = "Сохранено: ${song.title}"
        }
    }
}