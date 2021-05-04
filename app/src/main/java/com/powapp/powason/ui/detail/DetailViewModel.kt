package com.powapp.powason.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powapp.powason.data.*
import com.powapp.powason.util.NEW_ENTRY_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(app: Application): AndroidViewModel(app) {
    private val database = InternalDatabase.getInstance(app)
    val breachInfo = MutableLiveData<List<BreachInfo>>()

    //Injects the info to the form from the database by the ID
    fun injectBreachInfoById(breachInfoId: Int, dataRepository: LoginDataRepository) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val login =
                    if (breachInfoId != NEW_ENTRY_ID)
                        database?.loginDao()?.getLoginById(breachInfoId)
                    else
                        DataEntity()

                //Get from web service
                dataRepository.callHIBPAccountApi(login, RequestType.FULL_DATA)
            }
        }
    }
}