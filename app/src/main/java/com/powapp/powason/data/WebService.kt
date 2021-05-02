package com.powapp.powason.data

import com.powapp.powason.util.HIBP_API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface WebService {
    @GET("/brute")
    suspend fun getLoginData(): Response<List<Login>>
}