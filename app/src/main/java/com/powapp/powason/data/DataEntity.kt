package com.powapp.powason.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.powapp.powason.util.NEW_ENTRY_ID
import java.util.*

@Entity(tableName = "logins")
data class DataEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var date: Date = Date(Date().time),
    var title: String,
    var target: String,
    var target_name: String,
    var password: String?,
    var username: String?,
    var breachCount: Int? = 0
) {
    //Constructors for the login data
    constructor() : this(NEW_ENTRY_ID, Date(), "", "", "", "", "")
    constructor(
        date: Date,
        title: String,
        target: String,
        target_name: String,
        password: String?,
        username: String?
    ) : this(NEW_ENTRY_ID, date, title, target, target_name, password, username)

    constructor(
        title: String,
        target: String,
        target_name: String,
        password: String?,
        username: String?
    ) : this(NEW_ENTRY_ID, Date(Date().time), title, target, target_name, password, username)
}