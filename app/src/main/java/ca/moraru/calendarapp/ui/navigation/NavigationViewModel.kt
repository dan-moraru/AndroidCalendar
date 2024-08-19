package ca.moraru.calendarapp.ui.navigation

import androidx.lifecycle.ViewModel
import ca.moraru.calendarapp.data.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.GregorianCalendar

data class NavigationUiState(
    val currentDate: GregorianCalendar = GregorianCalendar(),
    val currentEventId: Int = 0
)

class NavigationViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(NavigationUiState())
    val uiState: StateFlow<NavigationUiState> = _uiState.asStateFlow()

    fun updateDate(currentDate: GregorianCalendar) {
        _uiState.update { currentState ->
            currentState.copy(
                currentDate = currentDate
            )
        }
    }

    fun updateEventId(eventId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                currentEventId = eventId
            )
        }
    }
}