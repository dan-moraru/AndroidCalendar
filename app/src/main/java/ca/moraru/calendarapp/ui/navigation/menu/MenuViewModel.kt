package ca.moraru.calendarapp.ui.navigation.menu

import androidx.lifecycle.ViewModel
import ca.moraru.calendarapp.data.Event
import ca.moraru.calendarapp.data.EventsRepository
import java.util.Calendar
import java.util.GregorianCalendar
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.random.nextInt

data class MenuUiState(
    val monthEventList: List<Event> = listOf()
)

class MenuViewModel(private val eventsRepository: EventsRepository) : ViewModel() {
    private val _menuUiState: MutableStateFlow<MenuUiState> = MutableStateFlow(MenuUiState())
    val menuUiState = _menuUiState.asStateFlow()

    init {
//        setupEventList()
        updateMenuUiState(GregorianCalendar())
    }

    fun updateMenuUiState(calendar: GregorianCalendar) {
        var listOfEvents: List<Event> = listOf()
        runBlocking {
            try {
                listOfEvents = eventsRepository.getMonthEventsStream(
                    year = calendar.get(Calendar.YEAR),
                    month = calendar.get(Calendar.MONTH)
                ).firstOrNull() ?: emptyList()
            } catch(error: Exception) {
                Log.e("Error", "Error updating the MenuUiState")
            }
        }
        _menuUiState.update { currentState ->
            currentState.copy(
                monthEventList = listOfEvents
            )
        }
    }

    private fun setupEventList() {
        val dates = listOf(4, 9, 12, 15, 19, 20, 24, 28)
        val listOfEvents: MutableList<Event> = mutableListOf()

        val randomNumbers = generateSequence {
            Random.nextInt(1..32)
        }.distinct().take(32).toList()
        var count = 0
        for (date in dates) {
            listOfEvents.add(Event(randomNumbers[count], "Soccer", "Play soccer with people", "Dawson College", GregorianCalendar().get(Calendar.YEAR), GregorianCalendar().get(Calendar.MONTH), date, 6.0, 10.5))
            count += 1
            listOfEvents.add(Event(randomNumbers[count], "Lunch", "Eat lunch in the cafeteria", "Dawson College", GregorianCalendar().get(Calendar.YEAR), GregorianCalendar().get(Calendar.MONTH), date, 12.0, 12.5))
            count += 1
            listOfEvents.add(Event(randomNumbers[count], "Doctor Appointment", "Go for a doctor's appointment", "Hospital", GregorianCalendar().get(Calendar.YEAR), GregorianCalendar().get(Calendar.MONTH), date, 14.0, 16.0))
            count += 1
            listOfEvents.add(Event(randomNumbers[count], "Homework", "Do homework at my desk", "Home", GregorianCalendar().get(Calendar.YEAR), GregorianCalendar().get(Calendar.MONTH), date, 18.0, 20.0))
            count += 1
        }

        runBlocking {
            try {
                eventsRepository.deleteAllStream()

                for (event in listOfEvents) {
                    eventsRepository.insertStream(event)
                    Log.d("Inserted", "1 Event")
                }
            } catch(error: Exception) {
                Log.e("Error", "Failed to setup initial data")
            }
        }
    }

    fun getWeekdayName(num: Int, weekDays: Array<String>) : String {
        var day = ""
        when (num) {
            1 -> day = weekDays[0]
            2 -> day = weekDays[1]
            3 -> day = weekDays[2]
            4 -> day = weekDays[3]
            5 -> day = weekDays[4]
            6 -> day = weekDays[5]
            7 -> day = weekDays[6]
        }
        return day
    }

    fun moveMonthBackwards(
        calendar: GregorianCalendar,
        updateDate: (GregorianCalendar) -> Unit
    ) {
        if (calendar.get(Calendar.MONTH) == 0) {
            val newCalendar = GregorianCalendar(calendar.get(Calendar.YEAR) - 1, 11, calendar.get(Calendar.DAY_OF_MONTH))
            updateMenuUiState(newCalendar)
            updateDate(newCalendar)
        } else {
            val newCalendar = GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) - 1, calendar.get(Calendar.DAY_OF_MONTH))
            updateMenuUiState(newCalendar)
            updateDate(newCalendar)
        }
    }

    fun moveMonthForwards(
        calendar: GregorianCalendar,
        updateDate: (GregorianCalendar) -> Unit
    ) {
        if (calendar.get(Calendar.MONTH) == 11) {
            val newCalendar = GregorianCalendar(calendar.get(Calendar.YEAR) + 1, 0, calendar.get(Calendar.DAY_OF_MONTH))
            updateMenuUiState(newCalendar)
            updateDate(newCalendar)
        } else {
            val newCalendar = GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
            updateMenuUiState(newCalendar)
            updateDate(newCalendar)
        }
    }

    fun checkForEvents(events: List<Event>) : List<Int> {
        val listOfDays: MutableList<Int> = mutableListOf()
        for (event in events) {
            if (!listOfDays.contains(event.day)) {
                listOfDays.add(event.day)
            }
        }
        return listOfDays
    }

    fun getCurrentMonthName(monthDays: Array<String>, calendar: GregorianCalendar) : String {
        when (calendar.get(Calendar.MONTH)) {
            0 -> return monthDays[0]
            1 -> return monthDays[1]
            2 -> return monthDays[2]
            3 -> return monthDays[3]
            4 -> return monthDays[4]
            5 -> return monthDays[5]
            6 -> return monthDays[6]
            7 -> return monthDays[7]
            8 -> return monthDays[8]
            9 -> return monthDays[9]
            10 -> return monthDays[10]
            11 -> return monthDays[11]
        }
        return "Not Found"
    }
}