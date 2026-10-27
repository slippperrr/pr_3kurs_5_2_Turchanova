package com.example.myplaylist

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class MainActivity : Fragment(R.layout.activity_main) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PrefsManager(requireContext())
        if (prefs.isLoggedIn()) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame, SearchActivity())
                .commit()
            return
        }

        val login = view.findViewById<EditText>(R.id.editLogin)
        val password = view.findViewById<EditText>(R.id.editPassword)


        view.findViewById<Button>(R.id.buttonLogin).setOnClickListener {
            val username = login.text.toString().trim()
            val pass = password.text.toString()

            if (username.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Заполни логин и пароль", Toast.LENGTH_SHORT).show()
            } else {
                prefs.saveUser(username, pass)
                Toast.makeText(requireContext(), "Добро пожаловать, $username!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame, SearchActivity())
                    .commit()
            }
        }
    }
}