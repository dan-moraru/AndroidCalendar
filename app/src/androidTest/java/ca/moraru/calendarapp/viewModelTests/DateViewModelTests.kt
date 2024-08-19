package ca.moraru.calendarapp.viewModelTests

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ca.moraru.calendarapp.data.CalendarDatabase
import ca.moraru.calendarapp.data.Event
import ca.moraru.calendarapp.data.EventDao
import ca.moraru.calendarapp.data.OfflineEventsRepository
import ca.moraru.calendarapp.ui.navigation.date.DateViewModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.GregorianCalendar

class DateViewModelTests {
    private val year: Int = 2023
    private val month: Int = 10
    private val day: Int = 28
    private val event1 = Event(1, "Test 1", "Just a test for first event", "Android", year, month, day, 1.0, 2.0)
    private val event2 = Event(2, "Test 2", "Just a test for second event", "Android", year, month, day, 7.0, 9.0)
    private val event3 = Event(3, "Test 3", "Just a test for third event", "Android", 2023, 11, 18, 1.0, 2.0)

    private lateinit var eventDao: EventDao
    private lateinit var calendarDatabase: CalendarDatabase
    private lateinit var repository: OfflineEventsRepository
    private lateinit var viewModel: DateViewModel

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        calendarDatabase = Room.inMemoryDatabaseBuilder(context, CalendarDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        eventDao = calendarDatabase.eventDao()
        repository = OfflineEventsRepository(eventDao)
        viewModel = DateViewModel(repository)
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
        var dateUiState = viewModel.dateUiState.value
        val currentDay = GregorianCalendar()

        viewModel.updateMenuUiState(currentDay)

        assertEquals(0, dateUiState.dayEventList.size)
    }

    @Test
    fun test_updateUiMenuState() {
        val currentDay = GregorianCalendar(2023, 10, 28)
        var dateUiState = viewModel.dateUiState.value
        runBlocking {
            addThreeEventToDb()

            viewModel.updateMenuUiState(currentDay)

            dateUiState = viewModel.dateUiState.value
        }

        assertEquals(2, dateUiState.dayEventList.size)
        assertEquals(event1, dateUiState.dayEventList.first())
        assertEquals(event2, dateUiState.dayEventList[1])
    }

    @Test
    fun test_sortEventList(){
        val events = listOf(event2, event1)

        val sortedListEvents = viewModel.sortEventList(events)

        assertNotEquals(events, sortedListEvents)
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
    fun test_getCurrentMonthName(){
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ).toTypedArray()
        val num = 5

        val result = viewModel.getCurrentMonthName(num, months)

        assertEquals("June", result)
    }

    @Test
    fun test_getCurrentMonthNameNull(){
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ).toTypedArray()
        val num = 13

        val result = viewModel.getCurrentMonthName(num, months)

        assertEquals("Not Found", result)
    }

    @Test
    fun test_moveDayBackwards(){
        var dateUiState = viewModel.dateUiState.value
        val currentDay = GregorianCalendar(2023, 10, 29)

        runBlocking {
            addThreeEventToDb()

            viewModel.moveDayBackwards(currentDay, ::helperDateFun, ::helperWeatherFun)

            dateUiState = viewModel.dateUiState.value
        }

        assertEquals(2, dateUiState.dayEventList.size)
    }

    @Test
    fun test_moveDayBackwardsNoEvents(){
        var dateUiState = viewModel.dateUiState.value
        val currentDay = GregorianCalendar(2023, 10, 28)

        runBlocking {
            addThreeEventToDb()

            viewModel.moveDayBackwards(currentDay, ::helperDateFun, ::helperWeatherFun)

            dateUiState = viewModel.dateUiState.value
        }

        assertEquals(0, dateUiState.dayEventList.size)
    }

    @Test
    fun test_moveDayForwards(){
        var dateUiState = viewModel.dateUiState.value
        val currentDay = GregorianCalendar(2023, 10, 27)

        runBlocking {
            addThreeEventToDb()

            viewModel.moveDayForwards(currentDay, ::helperDateFun, ::helperWeatherFun)

            dateUiState = viewModel.dateUiState.value
        }

        assertEquals(2, dateUiState.dayEventList.size)
    }

    @Test
    fun test_moveDayForwardsNoEvents(){
        var dateUiState = viewModel.dateUiState.value
        val currentDay = GregorianCalendar(2023, 10, 28)

        runBlocking {
            addThreeEventToDb()

            viewModel.moveDayForwards(currentDay, ::helperDateFun, ::helperWeatherFun)

            dateUiState = viewModel.dateUiState.value
        }

        assertEquals(0, dateUiState.dayEventList.size)
    }

    private fun helperDateFun(calendar: GregorianCalendar){}
    private fun helperWeatherFun(calendar: GregorianCalendar){}

    private suspend fun addThreeEventToDb() {
        repository.insertStream(event1)
        repository.insertStream(event2)
        repository.insertStream(event3)
    }
}