package com.example.myplaylist

import androidx.room.*

@Dao
interface SongDao {
    @Query("SELECT * FROM saved_songs ORDER BY id DESC")
    suspend fun getAll(): List<SavedSong>

    @Insert
    suspend fun insert(song: SavedSong)

    @Delete
    suspend fun delete(song: SavedSong)

}