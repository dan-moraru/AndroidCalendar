package ca.moraru.calendarapp.viewModelTests

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ca.moraru.calendarapp.data.CalendarDatabase
import ca.moraru.calendarapp.data.Event
import ca.moraru.calendarapp.data.EventDao
import ca.moraru.calendarapp.data.OfflineEventsRepository
import ca.moraru.calendarapp.ui.navigation.view.ViewEventViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.GregorianCalendar

class ViewEventViewModelTests {
    private val year: Int = 2023
    private val month: Int = 10
    private val day: Int = 28
    private val event1 = Event(1, "Test 1", "Just a test for first event", "Android", year, month, day, 1.0, 2.0)
    private val event2 = Event(2, "Test 2", "Just a test for second event", "Android", year, month, day, 7.0, 9.0)

    private lateinit var eventDao: EventDao
    private lateinit var calendarDatabase: CalendarDatabase
    private lateinit var repository: OfflineEventsRepository
    private lateinit var viewModel: ViewEventViewModel

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        calendarDatabase = Room.inMemoryDatabaseBuilder(context, CalendarDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        eventDao = calendarDatabase.eventDao()
        repository = OfflineEventsRepository(eventDao)
        viewModel = ViewEventViewModel(repository)
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
        var eventViewUiState = viewModel.eventViewUiState.value

        viewModel.updateMenuUiState(2)

        assertNotEquals("Test 2", eventViewUiState.currentEvent.title)
    }

    @Test
    fun test_updateUiMenuState() {
        var eventViewUiState = viewModel.eventViewUiState.value
        runBlocking {
            addOneEventToDB()

            viewModel.updateMenuUiState(1)

            eventViewUiState = viewModel.eventViewUiState.value
        }

        assertEquals("Test 1", eventViewUiState.currentEvent.title)
        assertEquals(1.0, eventViewUiState.currentEvent.startTime, 0.1)
    }

    @Test
    fun test_deleteEvent() {
        var eventViewUiState = viewModel.eventViewUiState.value
        runBlocking {
            addOneEventToDB()
            addOneDifferentEventToDB()

            viewModel.updateMenuUiState(2)

            viewModel.deleteEvent(event1)

            viewModel.updateMenuUiState(1)

            eventViewUiState = viewModel.eventViewUiState.value
        }

        assertNotEquals("Test 1", eventViewUiState.currentEvent.title)
    }

    private suspend fun addOneEventToDB() {
        repository.insertStream(event1)
    }

    private suspend fun addOneDifferentEventToDB() {
        repository.insertStream(event2)
    }
}