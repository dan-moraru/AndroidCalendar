package ca.moraru.calendarapp.viewModelTests

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ca.moraru.calendarapp.data.CalendarDatabase
import ca.moraru.calendarapp.data.Event
import ca.moraru.calendarapp.data.EventDao
import ca.moraru.calendarapp.data.OfflineEventsRepository
import ca.moraru.calendarapp.ui.navigation.create.CreateEventViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.LocalDate

class CreateEventViewModelTests {
    private val year: Int = 2023
    private val month: Int = 10
    private val day: Int = 28
    private val event1 = Event(1, "Test 1", "Just a test for first event", "Android", year, month, day, 1.0, 2.0)
    private val event2 = Event(2, "Test 2", "Just a test for second event", "Android", year, month, day, 7.0, 9.0)
    private val event3 = Event(3, "Test 3", "Just a test for third event", "Android", 2023, 11, 18, 1.0, 2.0)

    private lateinit var eventDao: EventDao
    private lateinit var calendarDatabase: CalendarDatabase
    private lateinit var repository: OfflineEventsRepository
    private lateinit var viewModel: CreateEventViewModel

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        calendarDatabase = Room.inMemoryDatabaseBuilder(context, CalendarDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        eventDao = calendarDatabase.eventDao()
        repository = OfflineEventsRepository(eventDao)
        viewModel = CreateEventViewModel(repository)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        if (::calendarDatabase.isInitialized) {
            calendarDatabase.close()
        }
    }

    @Test
    fun test_updateDayEventsWithNoData(){
        var createEventUiState = viewModel.createEventUiState.value
        val date = LocalDate.of(year, month, day)
        runBlocking {
            viewModel.updateDayEvents(date)

            createEventUiState = viewModel.createEventUiState.value
        }

        assertTrue(createEventUiState.dayListOfEvents!!.isEmpty())
    }

    @Test
    fun test_updateDayEvents() {
        var createEventUiState = viewModel.createEventUiState.value
        val date = LocalDate.of(year, 11, day)
        runBlocking {
            addThreeEventToDB()

            viewModel.updateDayEvents(date)

            createEventUiState = viewModel.createEventUiState.value
        }

        assertEquals(2, createEventUiState.dayListOfEvents!!.size)
    }

    @Test
    fun test_createEvent() {
        val newlyCreatedEvent: Event
        var createEventUiState = viewModel.createEventUiState.value
        val year = 2023
        val month = 8
        val day = 18
        val date = LocalDate.of(year, month + 1, day)
        val newEvent = Event(5, "new event", "yes", "yes", year, month, day, 1.0, 2.0)

        runBlocking {
            newlyCreatedEvent = viewModel.createEvent(newEvent)

            viewModel.updateDayEvents(date)

            createEventUiState = viewModel.createEventUiState.value
        }

        assertEquals(1, createEventUiState.dayListOfEvents!!.size)
        assertEquals(newlyCreatedEvent, createEventUiState.dayListOfEvents!!.first())
    }

    @Test
    fun test_isConflictingHoursFalse() {
        val newEvent = Event(4, "Test 1", "Just a test for first event", "Android", year, month, day, 3.0, 4.0)
        var createEventUiState = viewModel.createEventUiState.value
        var result : Boolean

        runBlocking {
            addThreeEventToDB()

            result = viewModel.isConflictingHours(newEvent)

            createEventUiState = viewModel.createEventUiState.value
        }

        assertFalse(result)
    }

    @Test
    fun test_isConflictingHoursTrue() {
        val newEvent = Event(4, "Test 1", "Just a test for first event", "Android", year, month, day, 1.0, 4.0)
        var createEventUiState = viewModel.createEventUiState.value
        var result : Boolean

        runBlocking {
            addThreeEventToDB()

            result = viewModel.isConflictingHours(newEvent)

            createEventUiState = viewModel.createEventUiState.value
        }

        assertFalse(result)
    }

    private suspend fun addThreeEventToDB() {
        repository.insertStream(event1)
        repository.insertStream(event2)
        repository.insertStream(event3)
    }
}