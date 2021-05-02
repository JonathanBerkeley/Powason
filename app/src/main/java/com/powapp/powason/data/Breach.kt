package com.powapp.powason.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Breach (
    @Json(name = "Name") val Name: String
)