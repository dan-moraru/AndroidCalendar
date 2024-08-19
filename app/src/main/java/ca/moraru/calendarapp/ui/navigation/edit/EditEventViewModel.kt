package ca.moraru.calendarapp.ui.navigation.edit

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

data class EditEventUiState(
    val currentEvent: Event? = null,
    val isConflicting: Boolean = false
)

class EditEventViewModel(private val eventsRepository: EventsRepository) : ViewModel() {
    private val _editEventUiState: MutableStateFlow<EditEventUiState> = MutableStateFlow(EditEventUiState())
    val editEventUiState: StateFlow<EditEventUiState> = _editEventUiState.asStateFlow()

    fun updateDayEvents(eventId: Int) {
        var event: Event? = null
        runBlocking {
            try {
                event = eventsRepository.getEventByIdStream(
                    eventId
                ).firstOrNull()
            } catch (error: Exception) {
                Log.e("Error", "Failed to update edited event list")
            }
        }
        _editEventUiState.update { currentState ->
            currentState.copy(
                currentEvent = event
            )
        }
    }

    fun isConflictingHours(event: Event) {
        var dayEvents: List<Event>
        runBlocking {
            dayEvents = eventsRepository.getDateEventsStream(
                event.year,
                event.month,
                event.day
            ).firstOrNull() ?: emptyList()
        }
        for (item in dayEvents) {
            if (item.id != event.id) {
                if (item.startTime > event.startTime && item.startTime < event.endTime || item.endTime == event.endTime || item.endTime > event.startTime && item.endTime < event.endTime) {
                    _editEventUiState.update { currentState ->
                        currentState.copy(
                            isConflicting = true
                        )
                    }
                }
            }
        }
    }

    fun updateEvent(event: Event) {
        runBlocking {
            try {
                eventsRepository.updateStream(event)
            } catch (error: Exception) {
                Log.e("Error", "Failed to update event")
            }
        }
    }
}