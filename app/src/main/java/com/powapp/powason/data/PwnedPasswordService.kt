package com.powapp.powason.data

import retrofit2.http.GET
import retrofit2.http.Path

interface PwnedPasswordService {
    @GET("range/{hash}")
    suspend fun getPasswordHashes(@Path("hash") hash: String): String
}
