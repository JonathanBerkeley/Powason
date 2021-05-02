package com.powapp.powason.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.powapp.powason.data.InternalDatabase
import com.powapp.powason.data.LoginDataRepository
import com.powapp.powason.data.SampleDataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LandingViewModel(app: Application) : AndroidViewModel(app) {
    //Creates database instance (if it doesn't currently exist) with the current application context
    private val database = InternalDatabase.getInstance(app)

    //Fetches live data from the database
    val loginList = database?.loginDao()?.getAll()

    private val dataRepository = LoginDataRepository(app)
    val loginData = dataRepository.loginData
    val breachData = dataRepository.breachData

    fun addSampleData() {
        //Start a coroutine
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //Puts the sample data into the database
                database?.loginDao()?.insertAll(SampleDataProvider.getSampleLogins())
            }
        }
    }

    fun deleteAllListings() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //Delete all entries in the database and reset the primary key
                database?.loginDao()?.emptyDatabase()
                database?.loginDao()?.resetDatabasePK()
            }
        }
    }

    fun checkAccountSecurity() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

            }
        }
    }
}