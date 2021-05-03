package com.powapp.powason.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.powapp.powason.R
import com.powapp.powason.util.APP_VERSION

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("App init, version: ", APP_VERSION)
        setContentView(R.layout.activity_main)
    }
}