package com.powapp.powa.data

import androidx.room.TypeConverter
import java.util.*

//Type converter for changing date from Long to Date object & vice versa
class DateConverter {
    @TypeConverter
    fun fromTimeStamp(convertedDate: Long): Date {
        return Date(convertedDate)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}