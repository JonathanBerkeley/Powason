package com.powapp.powason.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.powapp.powason.util.DBG
import com.powapp.powason.util.FileHelper
import com.powapp.powason.util.WEB_HIBP_URL
import com.powapp.powason.util.WEB_SERVICE_URL
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class LoginDataRepository(val app: Application) {

    val loginData = MutableLiveData<List<Login>>()

    /*
    private val listType = Types.newParameterizedType(
        List::class.java, Login::class.java
    )
     */

    init {
        CoroutineScope(Dispatchers.IO).launch {
            callWebService()
        }
        Log.i(DBG, "Network availability: ${networkAvailable()}")
    }

    @WorkerThread
    suspend fun callWebService() {
        if (networkAvailable()) {
            val retrofit = Retrofit.Builder()
                .baseUrl(WEB_SERVICE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(WebService::class.java)
            val serviceData = service.getLoginData().body() ?: emptyList()
            loginData.postValue(serviceData)
        }
    }

    @WorkerThread
    suspend fun callHIBPWebService() {
        if (networkAvailable()) {
            val retrofit = Retrofit.Builder()
                .baseUrl(WEB_HIBP_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create(WebService::class.java)
            val serviceData = service.getBreachData().body() ?: emptyList()

            Log.i(DBG, serviceData.toString())
        }
    }

    /*
    private fun getLocalLoginData() {
        val parsedText = FileHelper.parseFromRaw(app, "login_data.json")
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<List<Monster>> = moshi.adapter(listType)
        loginData.value = adapter.fromJson(parsedText) ?: emptyList()
    }
    */

    private fun networkAvailable(): Boolean {
        val connectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false
    }
}