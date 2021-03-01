package com.powapp.powason.data

import com.squareup.moshi.Json

data class Login (
        @Json(name = "title") val  title: String,
        @Json(name = "target") val target: String,
        @Json(name = "target_url") val target_url: String,
        @Json(name = "password") val password: String,
        @Json(name = "username") val username: String
)