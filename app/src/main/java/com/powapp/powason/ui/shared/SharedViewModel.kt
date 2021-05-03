package com.powapp.powason.ui.shared

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.powapp.powason.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SharedViewModel(app: Application) : AndroidViewModel(app) {
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

    fun modifyBreachCount(id: Int, count: Int) {
        //Start a coroutine
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database?.loginDao()?.modifyBreachCount(id, count)
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
                val databaseSize: Int? = database?.loginDao()?.getCount()

                for (entry in 0..databaseSize!!) {
                    val acc: DataEntity? = database?.loginDao()?.getLoginById(entry)
                    if (acc != null) {
                        with(dataRepository) {
                            checkForBreaches(acc, RequestType.LOW_DATA)
                        }
                    }
                }
            }
        }
    }

    fun refreshData(swipeLayout: SwipeRefreshLayout) {
        dataRepository.refreshData(swipeLayout)
    }
}