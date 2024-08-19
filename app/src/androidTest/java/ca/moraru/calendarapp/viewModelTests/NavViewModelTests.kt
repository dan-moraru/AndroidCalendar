package ca.moraru.calendarapp.viewModelTests

import ca.moraru.calendarapp.data.Event
import ca.moraru.calendarapp.ui.navigation.NavigationViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.GregorianCalendar

class NavViewModelTests {
    private val viewModel = NavigationViewModel()
    private val year: Int = 2023
    private val month: Int = 10
    private val day: Int = 28
    private val event = Event(1, "Test 1", "Just a test for first event", "Android", year, month, day, 7.0, 9.0)

    @Test
    fun test_updateDate() {
        var calendar = GregorianCalendar()
        calendar = GregorianCalendar(calendar.get(Calendar.YEAR + 1), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        viewModel.updateDate(calendar)
        val date = viewModel.uiState.value.currentDate

        assertEquals(calendar, date)
    }

    @Test
    fun test_updateEventId() {
        viewModel.updateEventId(event.id!!)
        val eventId = viewModel.uiState.value.currentEventId

        assertEquals(1, eventId)
    }
}