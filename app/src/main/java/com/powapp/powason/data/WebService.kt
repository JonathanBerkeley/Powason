package com.powapp.powason.data

import retrofit2.Response
import retrofit2.http.GET

interface WebService {
    @GET("/file.json")
    suspend fun getLoginData(): Response<List<Login>>
}