package com.powapp.powason.ui.shared

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.powapp.powason.data.*
import com.powapp.powason.util.OBEY_API_LIMIT
import com.powapp.powason.util.OBEY_API_STRICT
import com.powapp.powason.util.dbCount
import com.powapp.powason.util.insertBool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SharedViewModel(app: Application) : AndroidViewModel(app) {
    //Creates database instance (if it doesn't currently exist) with the current application context
    private val database = InternalDatabase.getInstance(app)

    //Fetches live data from the database
    val loginList = database?.loginDao()?.getAll()

    val dataRepository = LoginDataRepository(app)
    val loginData = dataRepository.loginData
    val breachName = dataRepository.breachName
    val crackedPWInfo = dataRepository.crackedPWInfo

    fun getEntryCount() {
        //Start a coroutine
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dbCount = database?.loginDao()?.getCount()
            }
        }
    }

    fun addSampleData() {
        //Start a coroutine
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //Puts the sample data into the database
                database?.loginDao()?.insertAll(SampleDataProvider.getSampleLogins())
            }
        }
    }

    fun insertAccount(login: DataEntity) {
        //Start a coroutine
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //Puts the sample data into the database
                database?.loginDao()?.insertLogin(login)
            }
        }
    }

    fun syncDataWithWS() {
        //Start a coroutine
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deleteAllListings()
                dataRepository.callWebService()
                insertBool = true
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

    fun checkPasswordSecurity() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val databaseSize: Int? = database?.loginDao()?.getCount()
                for (entry in databaseSize!! downTo 0) {
                    val acc: DataEntity? = database?.loginDao()?.getLoginById(entry)
                    if (acc != null) {
                        with(dataRepository) {
                            checkForPasswordLeak(acc)
                        }
                    }
                    if (OBEY_API_STRICT)
                        delay(2100) // Delay to keep the API happy
                    else if (OBEY_API_LIMIT)
                        delay(1100) // Can still induce 429 response
                }
            }
        }
    }

    fun checkEmailSecurity() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val databaseSize: Int? = database?.loginDao()?.getCount()
                for (entry in databaseSize!! downTo 0) {
                    val acc: DataEntity? = database?.loginDao()?.getLoginById(entry)
                    if (acc != null) {
                        with(dataRepository) {
                            checkForBreaches(acc, RequestType.LOW_DATA)
                        }
                    }
                    if (OBEY_API_STRICT)
                        delay(2100) // Delay to keep the API happy
                    else if (OBEY_API_LIMIT)
                        delay(1100) // Can still induce 429 response
                }
            }
        }
    }

    fun checkEmailSecurity(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val acc: DataEntity? = database?.loginDao()?.getLoginById(id)
                with(dataRepository) {
                    checkForBreaches(acc, RequestType.LOW_DATA)
                }
            }
        }
    }

    fun checkAccountSecurity() {
        checkEmailSecurity()
        checkPasswordSecurity()
    }

    fun checkAccountSecurity(id: Int) {
        checkEmailSecurity(id)
        checkPasswordSecurity()
    }

    fun refreshData() {
        dataRepository.refreshData()
    }

    fun refreshData(swipeLayout: SwipeRefreshLayout) {
        dataRepository.refreshData(swipeLayout)
    }
}