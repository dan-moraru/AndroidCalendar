package ca.moraru.calendarapp.ui.navigation

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.moraru.calendarapp.ui.ViewModelProvider
import ca.moraru.calendarapp.ui.navigation.create.CreateEventScreen
import ca.moraru.calendarapp.ui.navigation.create.CreateEventViewModel
import ca.moraru.calendarapp.ui.navigation.date.DateScreen
import ca.moraru.calendarapp.ui.navigation.date.DateViewModel
import ca.moraru.calendarapp.ui.navigation.edit.EditEventScreen
import ca.moraru.calendarapp.ui.navigation.edit.EditEventViewModel
import ca.moraru.calendarapp.ui.navigation.menu.MenuScreen
import ca.moraru.calendarapp.ui.navigation.menu.MenuViewModel
import ca.moraru.calendarapp.ui.navigation.view.ViewEventScreen
import ca.moraru.calendarapp.ui.navigation.view.ViewEventViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

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
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: NavigationViewModel = viewModel(factory = ViewModelProvider.Factory),
    navController: NavHostController = rememberNavController(),
) {
    val updated = rememberUpdatedState(Unit)
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val menuViewModel: MenuViewModel = viewModel(factory = ViewModelProvider.Factory)
    val dateViewModel: DateViewModel = viewModel(factory = ViewModelProvider.Factory)
    val viewEventViewModel: ViewEventViewModel = viewModel(factory = ViewModelProvider.Factory)
    val editEventViewModel: EditEventViewModel = viewModel(factory = ViewModelProvider.Factory)
    val createEventViewModel: CreateEventViewModel = viewModel(factory = ViewModelProvider.Factory)

    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                CoroutineScope(Dispatchers.Default).launch {
                    viewModel.updateLocation(fusedLocationClient, context)
                }
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                CoroutineScope(Dispatchers.Default).launch {
                    viewModel.updateLocation(fusedLocationClient, context)
                }
            } else -> {
                Log.d("LOCATION", "NO LOCATION")
            }
        }
    }

    LaunchedEffect(updated) {
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ))
    }

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = CalendarScreen.Menu.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = CalendarScreen.Menu.name) {
                MenuScreen(
                    currentDate = uiState.currentDate,
                    holidayData = uiState.holidayData,
                    weatherDays = uiState.weatherList,
                    viewModel = menuViewModel,
                    updateDate = {
                        viewModel.updateDate(it, context)
                    },
                    changeView = {
                        viewModel.updateDate(it, context)
                        viewModel.updateWeather(it)
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
                    currentWeather = uiState.currentWeather,
                    updateWeather = {
                        viewModel.updateWeather(it)
                    },
                    viewModel = dateViewModel,
                    updateDate = {
                        viewModel.updateDate(it, context)
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
                    holidayData = uiState.holidayData
                ) {
                    navController.navigate(CalendarScreen.CreateEvent.name)
                }
            }
            composable(route = CalendarScreen.ViewEvent.name) {
                ViewEventScreen(
                    uiState.currentEventId,
                    viewModel = viewEventViewModel,
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
                    viewModel = createEventViewModel,
                    updateDate = {
                        viewModel.updateDate(it, context)
                    },
                    backNavigation = {
                        navController.popBackStack()
                    }
                )
            }
            composable(route = CalendarScreen.EditEvent.name) {
                EditEventScreen(
                    currentEventId = uiState.currentEventId,
                    viewModel = editEventViewModel,
                    updateDate = {
                      viewModel.updateDate(it, context)
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

