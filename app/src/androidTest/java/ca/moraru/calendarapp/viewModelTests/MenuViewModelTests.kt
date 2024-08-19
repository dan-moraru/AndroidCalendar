package ca.moraru.calendarapp.viewModelTests

import ca.moraru.calendarapp.ui.navigation.menu.MenuViewModel
import org.junit.Test
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ca.moraru.calendarapp.R
import ca.moraru.calendarapp.data.AppDataContainer
import ca.moraru.calendarapp.data.CalendarDatabase
import ca.moraru.calendarapp.data.Event
import ca.moraru.calendarapp.data.EventDao
import ca.moraru.calendarapp.data.EventsRepository
import ca.moraru.calendarapp.data.OfflineEventsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import java.io.IOException
import java.util.Calendar
import java.util.GregorianCalendar

class MenuViewModelTests {
    private val year: Int = 2023
    private val month: Int = 10
    private val day: Int = 28
    private val event1 = Event(1, "Test 1", "Just a test for first event", "Android", year, month, day, 7.0, 9.0)
    private val event2 = Event(2, "Test 2", "Just a test for second event", "Android", year, month, day, 1.0, 2.0)
    private val event3 = Event(3, "Test 3", "Just a test for third event", "Android", 2023, 11, 18, 1.0, 2.0)

    private lateinit var eventDao: EventDao
    private lateinit var calendarDatabase: CalendarDatabase
    private lateinit var repository: OfflineEventsRepository
    private lateinit var viewModel: MenuViewModel

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        calendarDatabase = Room.inMemoryDatabaseBuilder(context, CalendarDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        eventDao = calendarDatabase.eventDao()
        repository = OfflineEventsRepository(eventDao)
        viewModel = MenuViewModel(repository)
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
        var menuUiState = viewModel.menuUiState.value
        val currentDay = GregorianCalendar()

        viewModel.updateMenuUiState(currentDay)

        assertEquals(0, menuUiState.monthEventList.size )
    }

    @Test
    fun test_updateUiMenuState() {
        val currentDay = GregorianCalendar(2023, 10, 28)
        var menuUiState = viewModel.menuUiState.value
        runBlocking {
            addThreeEventToDb()

            viewModel.updateMenuUiState(currentDay)

            menuUiState = viewModel.menuUiState.value
        }

        assertEquals(2, menuUiState.monthEventList.size)
        assertEquals(event1, menuUiState.monthEventList.first())
        assertEquals(event2, menuUiState.monthEventList[1])
    }

    @Test
    fun test_getWeekdayName(){
        val weekDays = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday").toTypedArray()
        val num = 2

        val result = viewModel.getWeekdayName(num, weekDays)

        assertEquals("Monday", result)
    }

    @Test
    fun test_getWeekdayNameNull(){
        val weekDays = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday").toTypedArray()
        val num = 8

        val result = viewModel.getWeekdayName(num, weekDays)

        assertEquals("", result)
    }

    @Test
    fun test_moveMonthBackwards(){
        var menuUiState = viewModel.menuUiState.value
        val currentDay = GregorianCalendar(2023, 11, 1)

        runBlocking {
            addThreeEventToDb()

            viewModel.moveMonthBackwards(currentDay, ::helperCalendarFun)

            menuUiState = viewModel.menuUiState.value
        }

        assertEquals(2, menuUiState.monthEventList.size)
    }

    @Test
    fun test_moveMonthBackwardsNextYear(){
        var menuUiState = viewModel.menuUiState.value
        val currentDay = GregorianCalendar(2024, 0, 1)

        runBlocking {
            addThreeEventToDb()

            viewModel.moveMonthBackwards(currentDay, ::helperCalendarFun)

            menuUiState = viewModel.menuUiState.value
        }

        assertEquals(11, menuUiState.monthEventList.first().month)
    }

    @Test
    fun test_moveMonthForwards(){
        var menuUiState = viewModel.menuUiState.value
        val currentDay = GregorianCalendar(2023, 9, 1)

        runBlocking {
            addThreeEventToDb()

            viewModel.moveMonthForwards(currentDay, ::helperCalendarFun)

            menuUiState = viewModel.menuUiState.value
        }

        assertEquals(2, menuUiState.monthEventList.size)
    }

    @Test
    fun test_moveMonthForwardsNextYear(){
        var menuUiState = viewModel.menuUiState.value
        val currentDay = GregorianCalendar(2023, 11, 1)

        runBlocking {
            addThreeEventToDb()

            viewModel.moveMonthForwards(currentDay, ::helperCalendarFun)

            menuUiState = viewModel.menuUiState.value
        }

        assertEquals(0, menuUiState.monthEventList.size)
    }

    @Test
    fun test_checkForEvents(){
        val events = listOf(event1, event2)

        val listOfDays = viewModel.checkForEvents(events)

        assertEquals(1, listOfDays.size)
        assertEquals(28, listOfDays.first())
    }

    @Test
    fun test_getCurrentMonthName(){
        val currentDay = GregorianCalendar(2023, 10, 28)
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ).toTypedArray()

        val result = viewModel.getCurrentMonthName(months, currentDay)

        assertEquals("November", result)
    }

    private fun helperCalendarFun(calendar: GregorianCalendar){}

    private suspend fun addThreeEventToDb() {
        repository.insertStream(event1)
        repository.insertStream(event2)
        repository.insertStream(event3)
    }
}