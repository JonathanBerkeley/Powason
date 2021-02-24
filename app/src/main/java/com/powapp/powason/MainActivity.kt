package com.powapp.powason

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("dataLogging", "Hello world")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}