package ca.moraru.calendarapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.moraru.calendarapp.ui.ViewModelProvider
import ca.moraru.calendarapp.ui.navigation.create.CreateEventScreen
import ca.moraru.calendarapp.ui.navigation.date.DateScreen
import ca.moraru.calendarapp.ui.navigation.edit.EditEventScreen
import ca.moraru.calendarapp.ui.navigation.menu.MenuScreen
import ca.moraru.calendarapp.ui.navigation.view.ViewEventScreen

enum class CalendarScreen {
    Menu,
    Date,
    EditEvent,
    CreateEvent,
    ViewEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarApp(
    viewModel: NavigationViewModel = viewModel(factory = ViewModelProvider.Factory),
    navController: NavHostController = rememberNavController(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = CalendarScreen.Menu.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = CalendarScreen.Menu.name) {
                MenuScreen(
                    currentDate = uiState.currentDate,
                    updateDate = {
                        viewModel.updateDate(it)
                    },
                    changeView = {
                        viewModel.updateDate(it)
                        navController.navigate(CalendarScreen.Date.name)
                    },
                    createEventNavigation = {
                        navController.navigate(CalendarScreen.CreateEvent.name)
                    }
                )
            }
            composable(route = CalendarScreen.Date.name) {
                DateScreen(
                    currentDate = uiState.currentDate,
                    updateDate = {
                        viewModel.updateDate(it)
                    },
                    updateEvent = {
                      viewModel.updateEventId(it)
                    },
                    backNavigation = {
                        navController.popBackStack()
                    },
                    viewEventNavigation = {
                        navController.navigate(CalendarScreen.ViewEvent.name)
                    },
                    createEventNavigation = {
                        navController.navigate(CalendarScreen.CreateEvent.name)
                    }
                )
            }
            composable(route = CalendarScreen.ViewEvent.name) {
                ViewEventScreen(
                    uiState.currentEventId,
                    backNavigation = {
                        navController.popBackStack()
                    },
                    changeEditView = {
                        navController.navigate(CalendarScreen.EditEvent.name)
                    }
                )
            }
            composable(route = CalendarScreen.CreateEvent.name) {
                CreateEventScreen(
                    currentDate = uiState.currentDate,
                    updateDate = {
                        viewModel.updateDate(it)
                    },
                    backNavigation = {
                        navController.popBackStack()
                    }
                )
            }
            composable(route = CalendarScreen.EditEvent.name) {
                EditEventScreen(
                    currentEventId = uiState.currentEventId,
                    updateDate = {
                      viewModel.updateDate(it)
                    },
                    updateEvent = {
                        viewModel.updateEventId(it)
                    },
                    backNavigation = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

