package ca.moraru.calendarapp.ui.navigation.view

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

data class ViewEventUiState(
    val currentEvent: Event = Event(1, "Default title", "Default description", "Default location", GregorianCalendar().get(Calendar.YEAR), GregorianCalendar().get(Calendar.MONTH), GregorianCalendar().get(Calendar.DAY_OF_MONTH), 12.0, 14.5)
)

class ViewEventViewModel(private val eventsRepository: EventsRepository) : ViewModel() {
    private val _eventViewUiState: MutableStateFlow<ViewEventUiState> = MutableStateFlow(ViewEventUiState())
    val eventViewUiState: StateFlow<ViewEventUiState> = _eventViewUiState.asStateFlow()

    fun updateMenuUiState(eventId: Int) {
        var event: Event? = null
        runBlocking {
            try {
                event = eventsRepository.getEventByIdStream(
                    eventId = eventId
                ).firstOrNull()
            } catch(error: Exception) {
                Log.e("Error", "Error updating the DateUiState")
            }
        }
        if (event !== null) {
            _eventViewUiState.update { currentState ->
                currentState.copy(
                    currentEvent = event!!
                )
            }
        }
    }

    fun deleteEvent(event: Event) {
        runBlocking {
            try {
                eventsRepository.deleteStream(event)
            } catch (error: Exception) {
                Log.e("Error", "Error deleting the current event")
            }
        }
    }
}

