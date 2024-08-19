package ca.moraru.calendarapp.ui.navigation.create

import android.util.Log
import androidx.lifecycle.ViewModel
import ca.moraru.calendarapp.data.Event
import ca.moraru.calendarapp.data.EventsRepository
import ca.moraru.calendarapp.ui.navigation.edit.EditEventUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

data class CreateEventUiState(
    val dayListOfEvents: List<Event>? = null
)

class CreateEventViewModel(private val eventsRepository: EventsRepository) : ViewModel() {
    private val _createEventUiState: MutableStateFlow<CreateEventUiState> = MutableStateFlow(
        CreateEventUiState()
    )
    val createEventUiState: StateFlow<CreateEventUiState> = _createEventUiState.asStateFlow()

    fun updateDayEvents(date: LocalDate) {
        var listOfEvents: List<Event> = listOf()
        runBlocking {
            try {
                listOfEvents = eventsRepository.getDateEventsStream(
                    date.year,
                    date.monthValue - 1,
                    date.dayOfMonth
                ).firstOrNull() ?: emptyList()
            } catch (error: Exception) {
                Log.e("Error", "Failed to update edited event list")
            }
        }
        _createEventUiState.update { currentState ->
            currentState.copy(
                dayListOfEvents = listOfEvents
            )
        }
    }

    fun createEvent(event: Event) : Event {
        runBlocking {
            try {
                eventsRepository.insertStream(event)
            } catch (error: Exception) {
                Log.e("Error", "Error creating event")
            }
        }
        val newEvent = runBlocking {
            eventsRepository.getEventStream(
                event.year,
                event.month,
                event.day,
                event.startTime
            ).firstOrNull()
        }
        return newEvent!!
    }

    fun isConflictingHours(event: Event) : Boolean {
        if (_createEventUiState.value.dayListOfEvents !== null) {
            for (item in _createEventUiState.value.dayListOfEvents!!) {
                if (item.id != event.id) {
                    if (item.startTime > event.startTime && item.startTime < event.endTime || item.endTime == event.endTime || item.endTime > event.startTime && item.endTime < event.endTime) {
                        return true
                    }
                }
            }
        }
        return false
    }
}