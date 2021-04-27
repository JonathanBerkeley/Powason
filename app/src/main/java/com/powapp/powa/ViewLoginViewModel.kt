package com.powapp.powa

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powapp.powa.data.DataEntity
import com.powapp.powa.data.InternalDatabase
import com.powapp.powa.util.NEW_ENTRY_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewLoginViewModel(app: Application) : AndroidViewModel(app) {
    private val database = InternalDatabase.getInstance(app)
    val currentLoginData = MutableLiveData<DataEntity>()
    val savedSite = MutableLiveData<String>()

    //Injects the login to the form from the database by the ID
    fun injectLoginById(loginId: Int) {
        //Run as coroutine (background thread)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //Gets the login if exists
                val login =
                    if (loginId != NEW_ENTRY_ID)
                        database?.loginDao()?.getLoginById(loginId)
                    else
                        DataEntity()
                //Posts data from background thread
                currentLoginData.postValue(login)
            }
        }
    }

    fun getLastSavedSite(loginId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val site = database?.loginDao()?.getSavedSite(loginId)
                //Posts the data from this background thread
                savedSite.postValue(site)
            }
        }
    }

    //Updates the login data when the user is done editing
    fun updateLoginData(): Boolean {
        //Basic string sanitizing
        currentLoginData.value?.let {
            it.run {
                title = title.trim()

                if (target.isEmpty()) {
                    target = ""
                }

                target = target.trim()
                target_name = target_name.trim()
                password = password?.trim()
                username = username?.trim()
            }

            //Error prevention
            if (it.id == NEW_ENTRY_ID && (it.title.isEmpty() || it.target_name.isEmpty()))
                return false
            //Runs background thread to perform update
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    //If the title or the website address is empty, delete the entry
                    if (it.title.isEmpty() || it.target_name.isEmpty()) {
                        database?.loginDao()?.deleteLoginData(it)
                    } else {
                        database?.loginDao()?.insertLogin(it)
                    }
                }
            }
        }
        return true
    }

    //Function for deleting data from the database using a background thread
    fun deleteLoginData(loginId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database?.loginDao()?.deleteLoginById(loginId)
            }
        }
    }
}