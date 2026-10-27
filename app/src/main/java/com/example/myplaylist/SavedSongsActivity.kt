package com.example.myplaylist

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SavedSongsActivity : Fragment(R.layout.activity_saved_songs) {
    private lateinit var dao: SongDao
    private lateinit var adapter: SongAdapter
    private lateinit var status: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dao = AppDatabase.getDatabase(requireContext()).songDao()
        status = view.findViewById(R.id.textSavedStatus)

        adapter = SongAdapter(
            context = requireContext(),
            actionText = "-",
            showAction = true,
            onAction = { song -> deleteSong(song) }
        )
        view.findViewById<ListView>(R.id.listSaved).adapter = adapter

        view.findViewById<Button>(R.id.buttonBack).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame, SearchActivity())
                .commit()
        }
        loadSongs()
    }

    override fun onResume() {
        super.onResume()
        if (::dao.isInitialized) loadSongs()
    }

    private fun loadSongs() {
        lifecycleScope.launch {
            val songs = withContext(Dispatchers.IO) { dao.getAll() }
            adapter.replaceData(
                songs.map {
                    Song(it.title, it.artist, it.album, it.artworkUrl, it.id.toLong())
                }
            )
            status.text = if (songs.isEmpty()) "Пока нет сохранённых песен"
            else "Песен в коллекции: ${songs.size}"
        }
    }

    private fun deleteSong(song: Song) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { dao.deleteById(song.trackId.toInt()) }
            loadSongs()
        }
    }
}