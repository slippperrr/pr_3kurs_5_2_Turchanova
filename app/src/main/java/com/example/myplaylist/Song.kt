package com.example.myplaylist

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Song(
    val title: String,
    val artist: String,
    val album: String = "",
    val artworkUrl: String = "",
    val trackId: Long = 0L
)

@Entity(tableName = "saved_songs")
data class SavedSong(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val artist: String,
    val album: String = "",
    val artworkUrl: String = ""
)