package ca.moraru.calendarapp.ui.navigation.date

import android.util.Log
import androidx.lifecycle.ViewModel
import ca.moraru.calendarapp.data.Event
import ca.moraru.calendarapp.data.EventsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.GregorianCalendar

data class DateUiState(
    val dayEventList: List<Event> = listOf()
)

class DateViewModel(private val eventsRepository: EventsRepository) : ViewModel() {
    private val _dateUiState: MutableStateFlow<DateUiState> = MutableStateFlow(DateUiState())
    val dateUiState: StateFlow<DateUiState> = _dateUiState.asStateFlow()

    init {
        updateMenuUiState(GregorianCalendar())
    }

    fun updateMenuUiState(calendar: GregorianCalendar) {
        var listOfEvents: List<Event> = listOf()
        runBlocking {
            try {
                listOfEvents = eventsRepository.getDateEventsStream(
                    year = calendar.get(Calendar.YEAR),
                    month = calendar.get(Calendar.MONTH),
                    day = calendar.get(Calendar.DAY_OF_MONTH)
                ).firstOrNull() ?: emptyList()
            } catch(error: Exception) {
                Log.e("Error", "Error updating the DateUiState")
            }
        }
        _dateUiState.update { currentState ->
            currentState.copy(
                dayEventList = listOfEvents
            )
        }
    }

    fun sortEventList(eventList: List<Event>) : List<Event> {
        val listOfEvents = eventList.toMutableList()
        listOfEvents.sortBy { it.startTime }
        return listOfEvents
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

    fun getCurrentMonthName(month: Int, monthDays: Array<String>) : String {
        when (month) {
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

    fun moveDayBackwards(
        calendar: GregorianCalendar,
        updateDate: (GregorianCalendar) -> Unit
    ) {
        var listOfEvents: List<Event> = listOf()
        runBlocking {
            try {
                val newCalendar = GregorianCalendar(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                newCalendar.add(Calendar.DAY_OF_MONTH, -1)
                updateDate(newCalendar)

                listOfEvents = eventsRepository.getDateEventsStream(
                    newCalendar.get(Calendar.YEAR),
                    newCalendar.get(Calendar.MONTH),
                    newCalendar.get(Calendar.DAY_OF_MONTH)
                ).firstOrNull() ?: emptyList()
            } catch(error: Exception) {
                Log.e("Error", "Error moving the day backwards")
            }
        }
        _dateUiState.update { currentState ->
            currentState.copy(
                dayEventList = listOfEvents
            )
        }
    }

    fun moveDayForwards(
        calendar: GregorianCalendar,
        updateDate: (GregorianCalendar) -> Unit
    ) {
        var listOfEvents: List<Event> = listOf()
        runBlocking {
            try {
                val newCalendar = GregorianCalendar(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                newCalendar.add(Calendar.DAY_OF_MONTH, 1)
                updateDate(newCalendar)

                listOfEvents = eventsRepository.getDateEventsStream(
                    newCalendar.get(Calendar.YEAR),
                    newCalendar.get(Calendar.MONTH),
                    newCalendar.get(Calendar.DAY_OF_MONTH)
                ).firstOrNull() ?: emptyList()
            } catch(error: Exception) {
                Log.e("Error", "Error moving the day forward")
            }
        }
        _dateUiState.update { currentState ->
            currentState.copy(
                dayEventList = listOfEvents
            )
        }
    }
}