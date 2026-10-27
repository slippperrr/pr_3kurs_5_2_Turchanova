package com.example.myplaylist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FrameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame)

        if(savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.frame, MainActivity())
                .commit()
    }
}}