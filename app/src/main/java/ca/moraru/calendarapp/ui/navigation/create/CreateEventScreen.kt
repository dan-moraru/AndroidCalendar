package ca.moraru.calendarapp.ui.navigation.create

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import ca.moraru.calendarapp.R
import ca.moraru.calendarapp.data.Event
import ca.moraru.calendarapp.data.doubleToHourString
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.lang.Integer.parseInt
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.GregorianCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    currentDate: GregorianCalendar,
    viewModel: CreateEventViewModel,
    backNavigation: () -> Unit,
    updateDate: (GregorianCalendar) -> Unit
) {
    var titleInput by rememberSaveable { mutableStateOf("") }
    var descInput by rememberSaveable { mutableStateOf("") }
    var locationInput by rememberSaveable { mutableStateOf("") }
    var dayInput by rememberSaveable { mutableStateOf(LocalDate.of(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH) + 1, currentDate.get(Calendar.DAY_OF_MONTH))) }
    var startTimeInput by rememberSaveable { mutableStateOf(0.0) }
    var endTimeInput by rememberSaveable { mutableStateOf(0.0) }

    val context = LocalContext
    viewModel.updateDayEvents(dayInput)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = context.current.resources.getString(R.string.create_event),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 25.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color(0.22f, 0.255f, 0.616f, 1.0f),
                ),
            )
        },
        bottomBar = {
            BottomAppBar {
                CreateFooter(
                    viewModel = viewModel,
                    backNavigation = backNavigation,
                    context = context,
                    event = Event(
                        id = null,
                        title = titleInput,
                        description = descInput,
                        location = locationInput,
                        day = dayInput.dayOfMonth,
                        month = dayInput.monthValue - 1,
                        year = dayInput.year,
                        startTime = startTimeInput,
                        endTime = endTimeInput
                    ),
                    updateDate = updateDate
                )
            }
        }
    ) { innerPadding ->
        InputFields(
            currentDate = currentDate,
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
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InputFields(
    currentDate: GregorianCalendar,
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
    updateEndTime: (Double) -> Unit,
    viewModel: CreateEventViewModel
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
                    modifier = Modifier.width(65.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0.216f, 0.447f, 0.847f, 1.0f)
                    )
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
                    modifier = Modifier.width(65.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0.216f, 0.447f, 0.847f, 1.0f)
                    )
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
                    modifier = Modifier.width(65.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0.216f, 0.447f, 0.847f, 1.0f)
                    )
                ) {
                    Icon(
                        Icons.Default.DateRange, contentDescription = context.current.resources.getString(R.string.date_description)
                    )
                }
            }
            ChooseDateDialog(
                dateDialog = dateDialog,
                context = context,
                updateDate = updateDate,
                dayInput = LocalDate.of(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH) + 1, currentDate.get(Calendar.DAY_OF_MONTH)),
                viewModel = viewModel
            )
            ChooseStartTimeDialog(
                timeStartDialog = timeStartDialog,
                context = context,
                updateStartTime = updateStartTime,
                startTimeInput = startTimeInput,
                endTimeInput = endTimeInput
            )
            ChooseEndTimeDialog(
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
fun ChooseDateDialog(
    dateDialog: MaterialDialogState,
    context: ProvidableCompositionLocal<Context>,
    updateDate: (LocalDate) -> Unit,
    dayInput: LocalDate,
    viewModel: CreateEventViewModel
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
            viewModel.updateDayEvents(it)
            updateDate(it)
        }
    }
}

@Composable
fun ChooseStartTimeDialog(
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
fun ChooseEndTimeDialog(
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
fun CreateFooter(
    viewModel: CreateEventViewModel,
    backNavigation: () -> Unit,
    context: ProvidableCompositionLocal<Context>,
    event: Event,
    updateDate: (GregorianCalendar) -> Unit
) {
    val currentContext = LocalContext.current
    val conflictToastMessage = currentContext.resources.getString(R.string.invalid_time_range)
    val titleToastMessage = currentContext.resources.getString(R.string.title_toast_message)
    val descToastMessage = currentContext.resources.getString(R.string.description_toast_message)
    val locationToastMessage = currentContext.resources.getString(R.string.location_toast_message)
    val timesToastMessage = currentContext.resources.getString(R.string.time_toast_message)

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = { backNavigation() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0.216f, 0.447f, 0.847f, 1.0f)
            )
        )
        {
            Text(text = context.current.resources.getString(R.string.cancel))
        }
        Button(
            onClick = {
                if (event.startTime >= event.endTime) {
                    Toast.makeText(currentContext, timesToastMessage, Toast.LENGTH_LONG).show()
                } else if (viewModel.isConflictingHours(event)) {
                    Toast.makeText(currentContext, conflictToastMessage, Toast.LENGTH_LONG).show()
                } else if (event.title.isEmpty()) {
                    Toast.makeText(currentContext, titleToastMessage, Toast.LENGTH_LONG).show()
                } else if (event.description.isEmpty()) {
                    Toast.makeText(currentContext, descToastMessage, Toast.LENGTH_LONG).show()
                } else if (event.location.isEmpty()) {
                    Toast.makeText(currentContext, locationToastMessage, Toast.LENGTH_LONG).show()
                } else {
                    val updatedEvent = viewModel.createEvent(event)
                    viewModel.updateDayEvents(LocalDate.of(updatedEvent.year, updatedEvent.month + 1, updatedEvent.day))
                    updateDate(GregorianCalendar(updatedEvent.year, updatedEvent.month, updatedEvent.day))
                    backNavigation()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0.216f, 0.447f, 0.847f, 1.0f)
            ))
        {
            Text(context.current.resources.getString(R.string.save))
        }
    }
}