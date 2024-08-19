package ca.moraru.calendarapp.ui.navigation.menu

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.moraru.calendarapp.R
import ca.moraru.calendarapp.ui.ViewModelProvider
import java.util.Calendar
import java.util.GregorianCalendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    currentDate: GregorianCalendar,
    updateDate: (GregorianCalendar) -> Unit,
    changeView: (GregorianCalendar) -> Unit,
    createEventNavigation: () -> Unit,
) {
    val context = LocalContext
    val viewModel: MenuViewModel = viewModel(factory = ViewModelProvider.Factory)
    viewModel.updateMenuUiState(currentDate)
    val uiState = viewModel.menuUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    MenuHeader(
                        updateDate = updateDate,
                        currentDate = currentDate,
                        viewModel = viewModel,
                        context = context
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier.padding(bottom = 30.dp)
            )
        },
        bottomBar = {
            BottomAppBar {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(
                        onClick = { createEventNavigation() },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "+"
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        CalendarDates(
            uiState = uiState,
            currentDate = currentDate,
            viewModel = viewModel,
            innerPadding = innerPadding,
            context = context,
            changeView = changeView
        )
    }
}

@Composable
fun CalendarDates(
    uiState: State<MenuUiState>,
    currentDate: GregorianCalendar,
    viewModel: MenuViewModel,
    innerPadding: PaddingValues,
    context: ProvidableCompositionLocal<Context>,
    changeView: (GregorianCalendar) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        DaysOfTheWeek(
            context = context,
            viewModel = viewModel
        )
        CalendarDays(
            currentDate = currentDate,
            viewModel = viewModel,
            changeView = changeView,
            uiState = uiState
        )
    }
}

@Composable
fun DaysOfTheWeek(
    context: ProvidableCompositionLocal<Context>,
    viewModel: MenuViewModel,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val weekDays = context.current.resources.getStringArray(R.array.days)
        for (i in 1 .. 7) {
            val day = viewModel.getWeekdayName(i, weekDays)
            Text(
                text = day.take(3),
                modifier = Modifier
                    .background(
                        color = Color.Gray,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .height(35.dp)
                    .width(50.dp)
                    .padding(3.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun CalendarDays(
    uiState: State<MenuUiState>,
    currentDate: GregorianCalendar,
    viewModel: MenuViewModel,
    changeView: (GregorianCalendar) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 3.dp)
    ) {
        var daysToSkip = 2 - GregorianCalendar(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), 1).get(Calendar.DAY_OF_WEEK)
        val numberOfDays = GregorianCalendar(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), 1).getActualMaximum(Calendar.DAY_OF_MONTH)
        val numberOfWeeks = GregorianCalendar(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), numberOfDays).getActualMaximum(Calendar.WEEK_OF_MONTH)
        val emptySlotsToAdd = 7 - GregorianCalendar(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), numberOfDays).get(Calendar.DAY_OF_WEEK)
        val daysWithEvents = viewModel.checkForEvents(uiState.value.monthEventList)

        for (week in 0 until numberOfWeeks) {
            Row(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                var count = 0
                for (date in daysToSkip..numberOfDays) {
                    if (count == 7) {
                        break
                    }
                    if (date > 0) {
                        count += 1
                        Button(
                            onClick = {
                                changeView(GregorianCalendar(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), date))
                            },
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(15.dp),
                            modifier = Modifier
                                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (daysWithEvents.contains(date)) {
                                    Text(
                                        text = date.toString(),
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(25.dp),
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "●",
                                        color = Color.Red,
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(25.dp),
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    Text(
                                        text = date.toString(),
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(50.dp),
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        count += 1
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                        )
                    }

                    if (week == numberOfWeeks-1 && date == numberOfDays) {
                        for (i in 0 until emptySlotsToAdd) {
                            Box(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                            )
                        }
                    }
                }
                daysToSkip += 7
            }
        }
    }
}

@Composable
fun MenuHeader(
    updateDate: (GregorianCalendar) -> Unit,
    currentDate: GregorianCalendar,
    viewModel: MenuViewModel,
    context: ProvidableCompositionLocal<Context>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val monthDays = context.current.resources.getStringArray(R.array.months)

        Icon(
            Icons.Default.ArrowBack,
            contentDescription = context.current.resources.getString(R.string.backwards_button),
            modifier = Modifier
                .clickable {
                    viewModel.moveMonthBackwards(currentDate, updateDate)
                }
                .padding(10.dp)
        )
        Text(
            text = viewModel.getCurrentMonthName(monthDays, currentDate) + " " + currentDate.get(Calendar.YEAR)
        )
        Icon(
            Icons.Default.ArrowForward,
            contentDescription = context.current.resources.getString(R.string.backwards_button),
            modifier = Modifier
                .clickable {
                    viewModel.moveMonthForwards(currentDate, updateDate)
                }
                .padding(horizontal = 25.dp)
                .padding(top = 10.dp, bottom = 10.dp)
        )
    }
}