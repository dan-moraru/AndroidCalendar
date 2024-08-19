package ca.moraru.calendarapp.ui.navigation.edit

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.moraru.calendarapp.R
import ca.moraru.calendarapp.data.Event
import ca.moraru.calendarapp.data.doubleToHourString
import ca.moraru.calendarapp.ui.ViewModelProvider
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.lang.Integer.parseInt
import java.time.LocalDate
import java.time.LocalTime
import java.util.GregorianCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    currentEventId: Int,
    updateEvent: (Int) -> Unit,
    backNavigation: () -> Unit,
    updateDate: (GregorianCalendar) -> Unit
) {
    val updated = rememberUpdatedState(Unit)
    var titleInput by rememberSaveable { mutableStateOf("") }
    var descInput by rememberSaveable { mutableStateOf("") }
    var locationInput by rememberSaveable { mutableStateOf("") }
    var dayInput by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var startTimeInput by rememberSaveable { mutableStateOf(0.0) }
    var endTimeInput by rememberSaveable { mutableStateOf(0.0) }

    val viewModel: EditEventViewModel = viewModel(factory = ViewModelProvider.Factory)
    viewModel.updateDayEvents(currentEventId)
    val uiState by viewModel.editEventUiState.collectAsState()
    val context = LocalContext

    if (uiState.currentEvent !== null) {
        LaunchedEffect(updated) {
            val event = uiState.currentEvent!!
            titleInput = event.title
            descInput = event.description
            locationInput = event.location
            dayInput = LocalDate.of(
                event.year,
                event.month + 1,
                event.day
            )
            startTimeInput = event.startTime
            endTimeInput = event.endTime
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = context.current.resources.getString(R.string.edit_event),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 25.sp
                        )
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                )
            },
            bottomBar = {
                BottomAppBar {
                    EditFooter(
                        viewModel = viewModel,
                        backNavigation = backNavigation,
                        context = context,
                        event = Event(
                            id = uiState.currentEvent!!.id,
                            title = titleInput,
                            description = descInput,
                            location = locationInput,
                            day = dayInput.dayOfMonth,
                            month = dayInput.monthValue - 1,
                            year = dayInput.year,
                            startTime = startTimeInput,
                            endTime = endTimeInput
                        ),
                        updateEvent = updateEvent,
                        updateDate = updateDate,
                        uiState = uiState
                    )
                }
            }
        ) { innerPadding ->
            InputEditFields(
                innerPadding = innerPadding,
                context = context,
                titleInput = titleInput,
                descInput = descInput,
                locationInput = locationInput,
                dayInput = dayInput,
                startTimeInput = startTimeInput,
                endTimeInput = endTimeInput,
                updateTitle = { titleInput = it },
                updateDesc = { descInput = it },
                updateLocation = { locationInput = it },
                updateDate = { dayInput = it },
                updateStartTime = { startTimeInput = it },
                updateEndTime = { endTimeInput = it },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InputEditFields(
    innerPadding: PaddingValues,
    context: ProvidableCompositionLocal<Context>,
    titleInput: String,
    descInput: String,
    locationInput: String,
    dayInput: LocalDate,
    startTimeInput: Double,
    endTimeInput: Double,
    updateTitle: (String) -> Unit,
    updateDesc: (String) -> Unit,
    updateLocation: (String) -> Unit,
    updateDate: (LocalDate) -> Unit,
    updateStartTime: (Double) -> Unit,
    updateEndTime: (Double) -> Unit
) {
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            val title = context.current.resources.getString(R.string.title_field)
            val description = context.current.resources.getString(R.string.description_field)
            val location = context.current.resources.getString(R.string.location_field)
            val date = context.current.resources.getString(R.string.date_field)
            val start = context.current.resources.getString(R.string.starttime_field)
            val end = context.current.resources.getString(R.string.endtime_field)
            val dateDialog = rememberMaterialDialogState()
            val timeStartDialog = rememberMaterialDialogState()
            val timeEndDialog = rememberMaterialDialogState()

            OutlinedTextField(
                value = titleInput,
                onValueChange = { updateTitle(it) },
                label = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .width(350.dp)
                    .padding(15.dp)
            )
            OutlinedTextField(
                value = descInput,
                onValueChange = { updateDesc(it) },
                label = {
                    Text(
                        text = description,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .width(350.dp)
                    .padding(15.dp)
            )
            OutlinedTextField(
                value = locationInput,
                onValueChange = { updateLocation(it) },
                label = {
                    Text(
                        text = location,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .width(350.dp)
                    .padding(15.dp)
            )
            Row(
                modifier = Modifier.width(350.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = dayInput.toString(),
                    onValueChange = {},
                    label = {
                        Text(
                            text = date,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                    },
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier
                        .width(275.dp)
                        .padding(15.dp)
                )
                Button(
                    onClick = { dateDialog.show() },
                    modifier = Modifier.width(65.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange, contentDescription = context.current.resources.getString(R.string.date_description)
                    )
                }
            }
            Row(
                modifier = Modifier.width(350.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = doubleToHourString(startTimeInput),
                    onValueChange = {},
                    label = {
                        Text(
                            text = start,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                    },
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier
                        .width(275.dp)
                        .padding(15.dp)
                )
                Button(
                    onClick = { timeStartDialog.show() },
                    modifier = Modifier.width(65.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange, contentDescription = context.current.resources.getString(R.string.date_description)
                    )
                }
            }
            Row(
                modifier = Modifier.width(350.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = doubleToHourString(endTimeInput),
                    onValueChange = {},
                    label = {
                        Text(
                            text = end,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        )
                    },
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier
                        .width(275.dp)
                        .padding(15.dp)
                )
                Button(
                    onClick = { timeEndDialog.show() },
                    modifier = Modifier.width(65.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange, contentDescription = context.current.resources.getString(R.string.date_description)
                    )
                }
            }
            ChooseDate(
                dateDialog = dateDialog,
                context = context,
                updateDate = updateDate,
                dayInput = dayInput
            )
            ChooseStartTime(
                timeStartDialog = timeStartDialog,
                context = context,
                updateStartTime = updateStartTime,
                startTimeInput = startTimeInput,
                endTimeInput = endTimeInput
            )
            ChooseEndTime(
                timeEndDialog = timeEndDialog,
                context = context,
                updateEndTime = updateEndTime,
                startTimeInput = startTimeInput,
                endTimeInput = endTimeInput
            )
        }
    }
}

@Composable
fun ChooseDate(
    dateDialog: MaterialDialogState,
    context: ProvidableCompositionLocal<Context>,
    updateDate: (LocalDate) -> Unit,
    dayInput: LocalDate,
) {
    MaterialDialog(
        dialogState = dateDialog,
        backgroundColor = Color.White,
        buttons = {
            positiveButton(
                text = context.current.resources.getString(R.string.ok)
            )
            negativeButton(
                text = context.current.resources.getString(R.string.cancel)
            )
        }
    ) {
        datepicker(
            initialDate = dayInput,
            title = context.current.resources.getString(R.string.date_dialog_title),
        ) {
            updateDate(it)
        }
    }
}

@Composable
fun ChooseStartTime(
    timeStartDialog: MaterialDialogState,
    context: ProvidableCompositionLocal<Context>,
    updateStartTime: (Double) -> Unit,
    startTimeInput: Double,
    endTimeInput: Double
) {
    val hour = parseInt(startTimeInput.toString().split(".").toTypedArray()[0])
    var minute = parseInt(startTimeInput.toString().split(".").toTypedArray()[1])
    // Checks if the minute is 0.5 representation of a half hour
    if (minute == 5) {
        minute = 30
    }
    val startTime = LocalTime.of(hour, minute)
    val currentContext = LocalContext.current
    val toastMessage = currentContext.resources.getString(R.string.invalid_start_time)

    MaterialDialog(
        dialogState = timeStartDialog,
        backgroundColor = Color.White,
        buttons = {
            positiveButton(
                text = context.current.resources.getString(R.string.ok)
            )
            negativeButton(
                text = context.current.resources.getString(R.string.cancel)
            )
        }
    ) {
        timepicker(
            initialTime = startTime,
            is24HourClock = true,
            title = context.current.resources.getString(R.string.time_dialog_title)
        ) {
            val hourDouble = it.hour
            var minuteDouble = 0.0
            if (it.minute in 16..45) {
                minuteDouble = 0.5
            } else if (it.minute > 45) {
                minuteDouble = 1.0
            }

            if ((hourDouble + minuteDouble) < endTimeInput) {
                updateStartTime(hourDouble + minuteDouble)
            } else {
                Toast.makeText(currentContext, toastMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun ChooseEndTime(
    timeEndDialog: MaterialDialogState,
    context: ProvidableCompositionLocal<Context>,
    updateEndTime: (Double) -> Unit,
    startTimeInput: Double,
    endTimeInput: Double
) {
    val hour = parseInt(endTimeInput.toString().split(".").toTypedArray()[0])
    var minute = parseInt(endTimeInput.toString().split(".").toTypedArray()[1])
    // Checks if the minute is 0.5 representation of a half hour
    if (minute == 5) {
        minute = 30
    }
    val endTime = LocalTime.of(hour, minute)
    val currentContext = LocalContext.current
    val toastMessage = currentContext.resources.getString(R.string.invalid_end_time)

    MaterialDialog(
        dialogState = timeEndDialog,
        backgroundColor = Color.White,
        buttons = {
            positiveButton(
                text = context.current.resources.getString(R.string.ok)
            )
            negativeButton(
                text = context.current.resources.getString(R.string.cancel)
            )
        }
    ) {
        timepicker(
            initialTime = endTime,
            is24HourClock = true,
            title = context.current.resources.getString(R.string.time_dialog_title)
        ) {
            val hourDouble = it.hour
            var minuteDouble = 0.0
            if (it.minute in 16..45) {
                minuteDouble = 0.5
            } else if (it.minute > 45) {
                minuteDouble = 1.0
            }

            if ((hourDouble + minuteDouble) > startTimeInput) {
                updateEndTime(hourDouble + minuteDouble)
            } else {
                Toast.makeText(currentContext, toastMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun EditFooter(
    updateEvent: (Int) -> Unit,
    uiState: EditEventUiState,
    viewModel: EditEventViewModel,
    backNavigation: () -> Unit,
    context: ProvidableCompositionLocal<Context>,
    event: Event,
    updateDate: (GregorianCalendar) -> Unit
) {
    val currentContext = LocalContext.current
    val toastMessage = currentContext.resources.getString(R.string.invalid_time_range)
    val titleToastMessage = currentContext.resources.getString(R.string.title_toast_message)
    val descToastMessage = currentContext.resources.getString(R.string.description_toast_message)
    val locationToastMessage = currentContext.resources.getString(R.string.location_toast_message)
    viewModel.isConflictingHours(event)

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(onClick = { backNavigation() })
        {
            Text(text = context.current.resources.getString(R.string.cancel))
        }
        Button(onClick = {
            if (uiState.isConflicting) {
                Toast.makeText(currentContext, toastMessage, Toast.LENGTH_LONG).show()
            } else if (event.title.isEmpty()) {
                Toast.makeText(currentContext, titleToastMessage, Toast.LENGTH_LONG).show()
            } else if (event.description.isEmpty()) {
                Toast.makeText(currentContext, descToastMessage, Toast.LENGTH_LONG).show()
            } else if (event.location.isEmpty()) {
                Toast.makeText(currentContext, locationToastMessage, Toast.LENGTH_LONG).show()
            } else {
                viewModel.updateEvent(event)
                updateEvent(event.id!!)
                updateDate(GregorianCalendar(event.year, event.month, event.day))
                backNavigation()
            }
        })
        {
            Text(context.current.resources.getString(R.string.save))
        }
    }
}