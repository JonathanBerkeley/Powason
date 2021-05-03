package com.powapp.powason.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.powapp.powason.util.DBG
import com.powapp.powason.util.WEB_HIBP_URL
import com.powapp.powason.util.WEB_SERVICE_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException

class LoginDataRepository(val app: Application) {

    val loginData = MutableLiveData<List<Login>>()
    val breachData = MutableLiveData<AccountData>()

    private var badConnectionHIBP: Boolean = false
    private var badConnectionWS: Boolean = false
    /*
    private val listType = Types.newParameterizedType(
        List::class.java, Login::class.java
    )
     */

    init {
        refreshData()
        Log.i(DBG, "Network availability: ${networkAvailable()}")
    }

    @WorkerThread
    suspend fun callWebService() {
        if (networkAvailable()) {
            badConnectionWS = try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(WEB_SERVICE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service = retrofit.create(WebService::class.java)
                val serviceData = service.getLoginData().body() ?: emptyList()
                loginData.postValue(serviceData)
                false
            } catch (e: SocketTimeoutException) {
                Log.i(DBG, "Couldn't connect to web service")
                true
            }
        }
    }

    @WorkerThread
    private suspend fun callHIBPAccountApi(account: DataEntity?, type: RequestType) {
        if (networkAvailable()) {
            badConnectionHIBP = try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(WEB_HIBP_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service = retrofit.create(HIBPService::class.java)

                if (account?.username != null && type == RequestType.LOW_DATA) {
                    val serviceData = service.getBreachName(email = account.username!!).body() ?: emptyList()

                    val ac = AccountData(account, serviceData)
                    breachData.postValue(ac)
                }
                false
            } catch (e: SocketTimeoutException) {
                Log.i(DBG, "Couldn't connect to remote web service")
                true
            }
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

    @Suppress("DEPRECATION")
    // Despite investigation, there is no easy replacement for this
    // The new options require a high target API level which would make this less backwards compatible
    private fun networkAvailable(): Boolean {
        val connectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false
    }

    fun checkForBreaches(account: DataEntity?, type: RequestType) {
        CoroutineScope(Dispatchers.IO).launch {
            callHIBPAccountApi(account, type)
        }
    }

    private fun refreshData() {
        CoroutineScope(Dispatchers.IO).launch {
            callWebService()
        }
    }

    fun refreshData(swipeLayout: SwipeRefreshLayout) {
        CoroutineScope(Dispatchers.IO).launch {
            callWebService()
        }

        val connected: Boolean = networkAvailable()
        if (!connected || badConnectionHIBP || badConnectionWS) {
            //Alert the user to the lack of internet connection
            val alertDialogBuilder = AlertDialog.Builder(swipeLayout.context)
            alertDialogBuilder.setTitle("Alert")
            if (!connected) {
                alertDialogBuilder.setMessage(
                    "Could not connect to internet " +
                            "- check your internet connection and try again"
                )
            } else if (badConnectionHIBP) {
                alertDialogBuilder.setMessage(
                    "Could not connect to third party api " +
                            "- try again later"
                )
            } else if (badConnectionWS) {
                alertDialogBuilder.setMessage(
                    "Could not connect to web service " +
                            "- try again later"
                )
            }
            alertDialogBuilder.setPositiveButton("Ok", null)
            alertDialogBuilder.show()
        }

        //This stops the refreshing animation
        swipeLayout.isRefreshing = false
    }
}