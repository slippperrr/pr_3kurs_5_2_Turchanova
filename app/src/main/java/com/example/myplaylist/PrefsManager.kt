package com.example.myplaylist

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences("music_preferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUser(login: String, password: String) {
        prefs.edit()
            .putString("login", login)
            .putString("password", password)
            .putBoolean("logged_in", true)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("logged_in", false)

    fun saveCatalogJson(songs: List<Song>)
    {
        prefs.edit().putString("catalog_json", gson.toJson(songs)).apply()
    }
}
