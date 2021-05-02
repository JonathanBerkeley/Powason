package com.powapp.powason.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.powapp.powason.data.LoginDataRepository

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val dataRepository = LoginDataRepository(app)
    val loginData = dataRepository.loginData
    val breachData = dataRepository.breachData
}