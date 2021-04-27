package com.powapp.powa.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LoginDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLogin(login: DataEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(login: List<DataEntity>)

    //Gets all DataEntity entries from the SQLite room database
    @Query("SELECT * FROM logins ORDER BY date ASC")
    fun getAll(): LiveData<List<DataEntity>>

    //Returns the DataEntity object for the login with supplied id in the database
    @Query("SELECT * FROM logins WHERE id = :id")
    fun getLoginById(id: Int): DataEntity?

    @Query("SELECT COUNT(*) FROM logins")
    fun getCount(): Int

    //Empties database for testing purposes when dev menu is enabled
    @Query("DELETE FROM logins WHERE 1 = 1")
    fun emptyDatabase()

    //Resets the primary key of the database - Dev mode feature
    @Query("DELETE FROM sqlite_sequence WHERE name = 'logins'")
    fun resetDatabasePK()

    //For deleting specific logins where the id is supplied
    @Query("DELETE FROM logins WHERE id = :id")
    fun deleteLoginById(id: Int)

    //Gets last saved website from the database for the purpose of generating favicon in edit screen
    @Query("SELECT target_name FROM logins WHERE id = :id")
    fun getSavedSite(id: Int): String?

    @Delete
    fun deleteLoginData(login: DataEntity)
}