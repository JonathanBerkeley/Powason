package com.powapp.powa

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.powapp.powa.data.InternalDatabase
import com.powapp.powa.data.LoginDao
import com.powapp.powa.data.SampleDataProvider
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var dao: LoginDao
    private lateinit var database: InternalDatabase

    //Runs before the test begins
    @Before
    fun buildDb() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(appContext, InternalDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.loginDao()!!
    }

    //The test
    @Test
    fun createNotes() {
        // Context of the app under test.
        dao.insertAll(SampleDataProvider.getSampleLogins())
        val count = dao.getCount()
        assertEquals(count, SampleDataProvider.getSampleLogins().size)
    }

    //Runs after the test completes (cleanup)
    @After
    fun closeDb() {
        database.close()
    }
}