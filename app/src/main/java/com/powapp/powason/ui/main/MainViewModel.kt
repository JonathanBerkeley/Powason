package com.powapp.powason.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.powapp.powason.data.Login
import com.powapp.powason.data.LoginDataRepository
import com.powapp.powason.util.DBG
import com.powapp.powason.util.FileHelper
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val dataRepository = LoginDataRepository(app)
    val loginData = dataRepository.loginData

}
