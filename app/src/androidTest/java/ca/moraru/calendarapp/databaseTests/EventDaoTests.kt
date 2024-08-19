package ca.moraru.calendarapp.databaseTests

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ca.moraru.calendarapp.data.CalendarDatabase
import ca.moraru.calendarapp.data.Event
import ca.moraru.calendarapp.data.EventDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class EventDaoTests {

    private lateinit var eventDao: EventDao
    private lateinit var calendarDatabase: CalendarDatabase
    private val year: Int = 2023
    private val month: Int = 9
    private val day: Int = 28
    private val event1 = Event(1, "Test 1", "Just a test for first event", "Android", year, month, day, 7.0, 9.0)
    private val event2 = Event(2, "Test 2", "Just a test for second event", "Android", year, month, day, 1.0, 2.0)

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        calendarDatabase  = Room.inMemoryDatabaseBuilder(context, CalendarDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        eventDao = calendarDatabase.eventDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        if (::calendarDatabase.isInitialized) {
            calendarDatabase.close()
        }
    }

    @Test
    @Throws(Exception::class)
    fun daoGet_getNothing() = runBlocking {
        val allEvents = eventDao.getMonthEvents(year, month).first()

        assertTrue(allEvents.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_addEventAndGet() = runBlocking {
        addOneEventToDb()

        val event = eventDao.getEvent(year, month, day, 7.0).first()

        assertEquals(event, event1)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_addEventAndGetById() = runBlocking {
        addOneEventToDb()

        val event = eventDao.getEventById(1).first()

        assertEquals(event, event1)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_addEventAndGetById_failure() = runBlocking {
        val event = eventDao.getEventById(1).first()

        assertEquals(event, null)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsertTwo_addEventsAndGetOfSameDay() = runBlocking {
        addTwoEventToDb()

        val allEvents = eventDao.getDateEvents(year, month, day).first()

        assertEquals(allEvents[0], event1)
        assertEquals(allEvents[1], event2)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetMonthEvents_addEventsAndGetOfSameMonth() = runBlocking {
        addTwoEventToDb()

        val allEvents = eventDao.getMonthEvents(year, month).first()

        assertEquals(allEvents[0], event1)
        assertEquals(allEvents[1], event2)
        assertTrue(allEvents.size == 2)
    }

    @Test
    @Throws(Exception::class)
    fun daoDeleteEvent_deletesAllEventsFromDB() = runBlocking {
        addTwoEventToDb()

        eventDao.deleteAll()
        val allEvents = eventDao.getDateEvents(year, month, day).first()

        assertTrue(allEvents.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun daoDeleteEvent_deleteEvent() = runBlocking {
        addTwoEventToDb()

        eventDao.delete(event1)
        val allEvents = eventDao.getDateEvents(year, month, day).first()

        assertEquals(allEvents[0], event2)
        assertTrue(allEvents.size == 1)
    }

    @Test
    @Throws(Exception::class)
    fun daoUpdateEvents_updatesEventsInDB() = runBlocking {
        addTwoEventToDb()
        val newEvent1 = Event(1, "Test 1 edited", "edited", "Androidet", year, month, day, 13.0, 15.0)
        val newEvent2 = Event(2, "Test 2 edited", "second edited", "droid", year, month, 29, 6.0, 1.0)

        eventDao.update(newEvent1)
        eventDao.update(newEvent2)
        val allEventsOn28 = eventDao.getDateEvents(year, month, day).first()
        val allEventsOn29 = eventDao.getDateEvents(year, month, 29).first()

        assertEquals(allEventsOn28[0], newEvent1)
        assertTrue(allEventsOn28.size == 1)
        assertEquals(allEventsOn29[0], newEvent2)
        assertTrue(allEventsOn29.size == 1)
    }

    private suspend fun addOneEventToDb() {
        eventDao.insert(event1)
    }

    private suspend fun addTwoEventToDb() {
        eventDao.insert(event1)
        eventDao.insert(event2)
    }
}