package ca.moraru.calendarapp.viewModelTests

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ca.moraru.calendarapp.data.CalendarDatabase
import ca.moraru.calendarapp.data.Event
import ca.moraru.calendarapp.data.EventDao
import ca.moraru.calendarapp.data.OfflineEventsRepository
import ca.moraru.calendarapp.ui.navigation.edit.EditEventViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.GregorianCalendar

class EditEventViewModelTests {
    private val year: Int = 2023
    private val month: Int = 10
    private val day: Int = 28
    private val event1 = Event(1, "Test 1", "Just a test for first event", "Android", year, month, day, 1.0, 2.0)
    private val event2 = Event(2, "Test 2", "Just a test for second event", "Android", year, month, day, 7.0, 9.0)
    private val event3 = Event(3, "Test 3", "Just a test for third event", "Android", 2023, 11, 18, 1.0, 2.0)

    private lateinit var eventDao: EventDao
    private lateinit var calendarDatabase: CalendarDatabase
    private lateinit var repository: OfflineEventsRepository
    private lateinit var viewModel: EditEventViewModel

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        calendarDatabase = Room.inMemoryDatabaseBuilder(context, CalendarDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        eventDao = calendarDatabase.eventDao()
        repository = OfflineEventsRepository(eventDao)
        viewModel = EditEventViewModel(repository)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        if (::calendarDatabase.isInitialized) {
            calendarDatabase.close()
        }
    }

    @Test
    fun test_updateUiMenuStateWithNoData(){
        var editEventUiState = viewModel.editEventUiState.value

        viewModel.updateDayEvents(2)

        try {
            val title = editEventUiState.currentEvent!!.title
            Assert.fail("Expected NullPointerException, but no exception was thrown.")
        } catch (e: NullPointerException) {
        }
    }

    @Test
    fun test_updateDayEvents() {
        var editEventUiState = viewModel.editEventUiState.value
        runBlocking {
            addOneEventToDB()

            viewModel.updateDayEvents(1)

            editEventUiState = viewModel.editEventUiState.value
        }

        assertEquals("Test 1", editEventUiState.currentEvent!!.title)
        assertEquals(1.0, editEventUiState.currentEvent!!.startTime, 0.1)
    }

    @Test
    fun test_isConflictingHoursFalse() {
        val newEvent = Event(4, "Test 1", "Just a test for first event", "Android", year, month, day, 3.0, 4.0)
        var editEventUiState = viewModel.editEventUiState.value

        runBlocking {
            addThreeEventToDB()

            viewModel.isConflictingHours(newEvent)

            editEventUiState = viewModel.editEventUiState.value
        }

        assertFalse(editEventUiState.isConflicting)
    }

    @Test
    fun test_isConflictingHoursTrue() {
        val newEvent = Event(4, "Test 1", "Just a test for first event", "Android", year, month, day, 1.0, 4.0)
        var editEventUiState = viewModel.editEventUiState.value

        runBlocking {
            addThreeEventToDB()

            viewModel.isConflictingHours(newEvent)

            editEventUiState = viewModel.editEventUiState.value
        }

        assertTrue(editEventUiState.isConflicting)
    }

    @Test
    fun test_updateEvent() {
        val updatedEvent1 = Event(1, "Updated Test 1", "Updated Just a test for first event", "Android", year, month, day, 1.0, 2.0)
        var editEventUiState = viewModel.editEventUiState.value

        runBlocking {
            addOneEventToDB()
            viewModel.updateDayEvents(1)

            viewModel.updateEvent(updatedEvent1)

            viewModel.updateDayEvents(1)
            editEventUiState = viewModel.editEventUiState.value
        }

        assertEquals("Updated Test 1", editEventUiState.currentEvent!!.title)
    }

    private suspend fun addOneEventToDB() {
        repository.insertStream(event1)
    }

    private suspend fun addThreeEventToDB() {
        repository.insertStream(event1)
        repository.insertStream(event2)
        repository.insertStream(event3)
    }
}