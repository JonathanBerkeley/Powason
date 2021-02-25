package com.powapp.powason.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.powapp.powason.util.DBG
import com.powapp.powason.util.FileHelper

class MainViewModel(app: Application) : AndroidViewModel(app) {
    init {
        val parsedText = FileHelper.parseFromRaw(app, "login_data.json")
        Log.i(DBG, parsedText)
    }
}
