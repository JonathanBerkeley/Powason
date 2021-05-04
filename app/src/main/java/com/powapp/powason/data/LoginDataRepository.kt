package com.powapp.powason.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.powapp.powason.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.SocketTimeoutException

class LoginDataRepository(val app: Application) {

    val loginData = MutableLiveData<List<DataEntity>>()
    val breachName = MutableLiveData<AccountData>()
    val breachInfo = MutableLiveData<AccountDataFull>()
    val crackedPWInfo = MutableLiveData<CrackedPasswords>()

    private var badConnectionHIBP: Boolean = false
    private var badConnectionWS: Boolean = false
    private var badConnectionPwnedPW: Boolean = false

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
    suspend fun callHIBPAccountApi(account: DataEntity?, type: RequestType) {
        if (networkAvailable()) {
            badConnectionHIBP = try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(WEB_HIBP_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val service = retrofit.create(HIBPService::class.java)

                if (account?.username != null) {
                    if (type == RequestType.LOW_DATA) {
                        val serviceData =
                            service.getBreachName(email = account.username!!).body() ?: emptyList()

                        val ac = AccountData(account, serviceData)
                        breachName.postValue(ac)
                    } else if (type == RequestType.FULL_DATA) {
                        val serviceData =
                            service.getBreachInfo(email = account.username!!).body() ?: emptyList()

                        val ac = AccountDataFull(account, serviceData)
                        breachInfo.postValue(ac)
                    }
                }

                false
            } catch (e: SocketTimeoutException) {
                Log.i(DBG, "Couldn't connect to remote web service")
                true
            }
        }
    }

    @WorkerThread
    suspend fun callPasswordApi(account: DataEntity?) {
        if (networkAvailable()) {
            badConnectionPwnedPW = try {
                val gson: Gson = GsonBuilder().setLenient().create()
                val retrofit = Retrofit.Builder()
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl(WEB_PWNED_PW_URL)
                    .build()
                val service = retrofit.create(PwnedPasswordService::class.java)

                if (account != null) {
                    account.passwordHash = HashHelper.sha1(account.password ?: "")
                    if (account.passwordHash != "") {
                        val serviceData = service.getPasswordHashes(
                            hash = account.passwordHash.substring(0, 5)
                        )
                        val ac = CrackedPasswords(account, serviceData)
                        crackedPWInfo.postValue(ac)
                    }
                }
                false
            } catch (e: SocketTimeoutException) {
                Log.i(DBG, "Couldn't connect to pwned password service")
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

    fun checkForPasswordLeak(account: DataEntity?) {
        CoroutineScope(Dispatchers.IO).launch {
            callPasswordApi(account)
        }
    }

    fun checkForBreaches(account: DataEntity?, type: RequestType) {
        CoroutineScope(Dispatchers.IO).launch {
            callHIBPAccountApi(account, type)
        }
    }

    fun refreshData() {
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
            } else if (badConnectionPwnedPW) {
                alertDialogBuilder.setMessage(
                    "Could not connect to password checking api " +
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