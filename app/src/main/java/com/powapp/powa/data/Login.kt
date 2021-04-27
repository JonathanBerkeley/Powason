package com.powapp.powa.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Login (
    @Json(name = "title") val  title: String,
    @Json(name = "target") val target: String,
    @Json(name = "target_url") val target_url: String,
    @Json(name = "password") val password: String,
    @Json(name = "username") val username: String
)