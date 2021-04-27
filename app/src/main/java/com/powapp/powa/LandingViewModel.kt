package com.powapp.powa

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powapp.powa.data.DataEntity
import com.powapp.powa.data.InternalDatabase
import com.powapp.powa.data.SampleDataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LandingViewModel(app: Application) : AndroidViewModel(app) {


    //Creates database instance (if it doesn't currently exist) with the current application context
    private val database = InternalDatabase.getInstance(app)

    //Fetches live data from the database
    val loginList = database?.loginDao()?.getAll()

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
}