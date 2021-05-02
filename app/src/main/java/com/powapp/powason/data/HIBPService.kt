package com.powapp.powason.data

import com.powapp.powason.util.HIBP_API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface HIBPService {
    @Headers("""hibp-api-key: $HIBP_API_KEY""")
    @GET("breachedaccount/{email}")
    suspend fun getBreachData(@Path("email") email: String): Response<List<Breach>>
}