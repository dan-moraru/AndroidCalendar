package ca.moraru.calendarapp.ui.navigation.date

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import java.util.Calendar
import java.util.GregorianCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateScreen(
    currentDate: GregorianCalendar,
    updateEvent: (Int) -> Unit,
    updateDate: (GregorianCalendar) -> Unit,
    backNavigation: () -> Unit,
    viewEventNavigation: () -> Unit,
    createEventNavigation: () -> Unit,
) {
    val viewModel: DateViewModel = viewModel(factory = ViewModelProvider.Factory)
    viewModel.updateMenuUiState(currentDate)
    val uiState by viewModel.dateUiState.collectAsState()

    Column(
        modifier = Modifier
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Header(
                            viewModel = viewModel,
                            currentDate = currentDate,
                            updateDate = updateDate
                        )
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                )
            },
            bottomBar = {
                BottomAppBar {
                    Footer(
                        backNavigation = backNavigation,
                        createEventNavigation = createEventNavigation
                    )
                }
            }
        ) { innerPadding ->
            EventsDisplay(
                viewModel = viewModel,
                innerPadding = innerPadding,
                viewEventNavigation = viewEventNavigation,
                uiState = uiState,
                updateEvent = updateEvent
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventsDisplay(
    updateEvent: (Int) -> Unit,
    viewModel: DateViewModel,
    innerPadding: PaddingValues,
    viewEventNavigation: () -> Unit,
    uiState: DateUiState
) {
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            HourSlots()
            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                val sizeForHalfHour = 27.5

                var previousEvent: Event? = null
                for (event in viewModel.sortEventList(uiState.dayEventList)) {
                    val paddingBefore = if (previousEvent == null) {
                        (event.startTime * sizeForHalfHour) + (sizeForHalfHour / 2)
                    } else {
                        (event.startTime - previousEvent.endTime) * sizeForHalfHour - (sizeForHalfHour / 4)
                    }
                    val height =
                        (event.endTime - event.startTime) * sizeForHalfHour + (sizeForHalfHour / 2)
                    previousEvent = event
                    Box(
                        modifier = Modifier
                            .width(300.dp)
                            .height(paddingBefore.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    EventCard(
                        updateEvent = updateEvent,
                        event = event,
                        height = height,
                        viewEventNavigation = viewEventNavigation,
                    )
                }
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun EventCard(
    updateEvent: (Int) -> Unit,
    event: Event,
    height: Double,
    viewEventNavigation: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(height.dp)
            .clip(RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
            .background(color = MaterialTheme.colorScheme.secondaryContainer)
            .clickable {
                updateEvent(event.id!!)
                viewEventNavigation()
            }
    ) {
        if (event.endTime - event.startTime > 1.0) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    text = event.title,
                    modifier = Modifier
                        .padding(start = 10.dp)
                )
                Text(
                    text = doubleToHourString(event.startTime) + " - " + doubleToHourString(event.endTime),
                    modifier = Modifier
                        .padding(start = 10.dp)
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Start,
            ) {
                Text(
                    text = event.title,
                    modifier = Modifier
                        .padding(start = 10.dp)
                )
                Text(
                    text = doubleToHourString(event.startTime) + " - " + doubleToHourString(event.endTime),
                    modifier = Modifier
                        .padding(start = 10.dp)
                )
            }
        }
    }
}

@Composable
fun HourSlots(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        for (hour in 0..24) {
            Text(
                text = "$hour:00",
                modifier = modifier
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun Header(
    updateDate: (GregorianCalendar) -> Unit,
    viewModel: DateViewModel,
    currentDate: GregorianCalendar
) {
    Column {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val context = LocalContext
            val weekDays = context.current.resources.getStringArray(R.array.days)
            val months = context.current.resources.getStringArray(R.array.months)

            TextButton(
                onClick = {
                    viewModel.moveDayBackwards(
                        calendar = currentDate,
                        updateDate = updateDate
                    )
                    viewModel.updateMenuUiState(currentDate)
                }
            ) {
                Text(
                    text = "❮",
                    fontSize = 25.sp,
                    color = Color.Black
                )
            }
            Text(
                text = viewModel.getWeekdayName(currentDate.get(Calendar.DAY_OF_WEEK), weekDays) + " " + viewModel.getCurrentMonthName(currentDate.get(Calendar.MONTH), months) + " " + currentDate.get(Calendar.DAY_OF_MONTH) + ", " + currentDate.get(Calendar.YEAR),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.width(275.dp)
            )
            TextButton(
                onClick = {
                    viewModel.moveDayForwards(
                        calendar = currentDate,
                        updateDate = updateDate
                    )
                    viewModel.updateMenuUiState(currentDate)
                },
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = "❯",
                    fontSize = 25.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun Footer(
    backNavigation: () -> Unit,
    createEventNavigation: () -> Unit
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { backNavigation() },
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            val context = LocalContext
            Text(
                text = context.current.resources.getString(R.string.back)
            )
        }
        TextButton(
            onClick = { createEventNavigation() },
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Text(
                text = "+",
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(top = 5.dp)
                    .align(Alignment.CenterVertically),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
    }
}
