package ca.moraru.calendarapp.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ca.moraru.calendarapp.CalendarApplication
import ca.moraru.calendarapp.ui.navigation.create.CreateEventViewModel
import ca.moraru.calendarapp.ui.navigation.date.DateViewModel
import ca.moraru.calendarapp.ui.navigation.edit.EditEventViewModel
import ca.moraru.calendarapp.ui.navigation.menu.MenuViewModel
import ca.moraru.calendarapp.ui.navigation.view.ViewEventViewModel
import ca.moraru.calendarapp.ui.navigation.NavigationViewModel

object ViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            MenuViewModel(
                calendarApplication().container.eventsRepository
            )
        }
        initializer {
            DateViewModel(
                calendarApplication().container.eventsRepository
            )
        }
        initializer {
            NavigationViewModel(
                calendarApplication().container.holidayRepository,
                calendarApplication().container.weatherRepository
            )
        }
        initializer {
            ViewEventViewModel(
                calendarApplication().container.eventsRepository,
            )
        }
        initializer {
            CreateEventViewModel(
                calendarApplication().container.eventsRepository
            )
        }
        initializer {
            EditEventViewModel(
                calendarApplication().container.eventsRepository
            )
        }
    }
}

fun CreationExtras.calendarApplication(): CalendarApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as CalendarApplication)