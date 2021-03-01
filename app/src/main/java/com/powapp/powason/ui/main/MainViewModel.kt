package com.powapp.powason.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.powapp.powason.data.Login
import com.powapp.powason.util.DBG
import com.powapp.powason.util.FileHelper
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val listType = Types.newParameterizedType(
        List::class.java, Login::class.java
    )

    init {
        val parsedText = FileHelper.parseFromRaw(app, "login_data.json")
        moshiParseText(parsedText)
    }


    private fun moshiParseText(text: String) {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val adapter: JsonAdapter<List<Login>> = moshi.adapter(listType)
        val loginData = adapter.fromJson(text)

        for (login in loginData ?: emptyList()) {
            Log.i(DBG,"${login.target} (${login.target_url})")
        }
    }
}
