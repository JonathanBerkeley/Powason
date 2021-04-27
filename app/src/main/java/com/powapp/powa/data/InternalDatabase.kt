package com.powapp.powa.data

import android.content.Context
import androidx.room.*

@Database (entities = [DataEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class InternalDatabase : RoomDatabase() {
    abstract fun loginDao(): LoginDao?

    //Singleton
    companion object {
        private var INSTANCE: InternalDatabase? = null
        fun getInstance(context: Context): InternalDatabase? {
            //If the instance doesn't already exist, set up the room database
            //INSTANCE variable is a reference to the built database (database instance)
            if (INSTANCE == null) {
                synchronized(InternalDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        InternalDatabase::class.java,
                        "powa_logins.db"
                    ).build()
                }
            }
            //Returns the database instance
            return INSTANCE
        }
    }
}