package com.example.myplaylist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class SongAdapter(
    private val context: Context,
    private var songs: List<Song> = emptyList(),
    private val actionText: String = "＋",
    private val showAction: Boolean = true,
    private val onAction: ((Song) -> Unit)? = null
) : BaseAdapter() {

    override fun getCount(): Int = songs.size
    override fun getItem(position: Int): Song = songs[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_song, parent, false)

        val song = songs[position]
        val cover = view.findViewById<ImageView>(R.id.imageCover)
        val title = view.findViewById<TextView>(R.id.textTitle)
        val artist = view.findViewById<TextView>(R.id.textArtist)
        val album = view.findViewById<TextView>(R.id.textAlbum)
        val button = view.findViewById<Button>(R.id.buttonSongAction)

        title.text = song.title
        artist.text = song.artist
        album.text = song.album

        if (song.artworkUrl.isNotBlank()) {
            Glide.with(context)
                .load(song.artworkUrl)
                .placeholder(R.drawable.ic_music_note)
                .error(R.drawable.ic_music_note)
                .into(cover)
        } else {
            cover.setImageResource(R.drawable.ic_music_note)
        }

        if (showAction && onAction != null) {
            button.visibility = View.VISIBLE
            button.text = actionText
            button.setOnClickListener { onAction.invoke(song) }
        } else {
            button.visibility = View.GONE
        }
        return view
    }

    fun replaceData(newSongs: List<Song>) {
        songs = newSongs
        notifyDataSetChanged()
    }
}