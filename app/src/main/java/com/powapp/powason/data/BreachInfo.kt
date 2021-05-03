package com.powapp.powason.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class BreachInfo (
    @Json(name = "Name") val Name: String,
    @Json(name = "Title") val Title: String,
    @Json(name = "Domain") val Domain: String,
    @Json(name = "BreachDate") val BreachDate: Date,
    @Json(name = "AddedDate") val AddedDate: Date,
    @Json(name = "ModifiedDate") val ModifiedDate: String,
    @Json(name = "PwnCount") val PwnCount: Int,
    @Json(name = "Description") val Description: String,
    @Json(name = "DataClasses") val DataClasses: List<String>,
    @Json(name = "IsVerified") val IsVerified: Boolean,
    @Json(name = "IsFabricated") val IsFabricated: Boolean,
    @Json(name = "IsSensitive") val IsSensitive: Boolean,
    @Json(name = "IsRetired") val IsRetired: Boolean,
    @Json(name = "IsSpamList") val IsSpamList: Boolean,
    @Json(name = "LogoPath") val LogoPath: String
)