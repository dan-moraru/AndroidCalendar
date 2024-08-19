package ca.moraru.calendarapp.ui.navigation.menu

import android.content.Context
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.moraru.calendarapp.R
import ca.moraru.calendarapp.data.Weather
import ca.moraru.calendarapp.model.HolidayModel
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    currentDate: GregorianCalendar,
    holidayData: List<HolidayModel>,
    weatherDays: List<Weather>,
    viewModel: MenuViewModel,
    updateDate: (GregorianCalendar) -> Unit,
    changeView: (GregorianCalendar) -> Unit,
    createEventNavigation: () -> Unit,
) {
    val context = LocalContext
    viewModel.updateMenuUiState(currentDate)
    val uiState = viewModel.menuUiState.collectAsState()
    val monthHolidays = mutableListOf<HolidayModel>()

    // Filter holidays for the month
    for (holiday in holidayData) {
        val date = holiday.date.dropLast(3)
        val currentYear = currentDate.get(Calendar.YEAR).toString()
        val month = (currentDate.get(Calendar.MONTH) + 1)
        val currentMonth: String = if (month < 10) {
            "0$month"
        } else {
            month.toString()
        }

        val current = "$currentYear-$currentMonth"
        if (date == current) {
            monthHolidays.add(holiday)
        }
    }

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
                    containerColor = Color(0.22f, 0.255f, 0.616f, 1.0f),
                ),
                modifier = Modifier.padding(bottom = 30.dp)
            )
        },
        bottomBar = {
            BottomAppBar {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    Button(
                        onClick = { createEventNavigation() },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0.216f, 0.447f, 0.847f, 1.0f)
                        )
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
            weatherDays = weatherDays,
            viewModel = viewModel,
            innerPadding = innerPadding,
            context = context,
            changeView = changeView,
            monthsHolidays = monthHolidays
        )
    }
}

@Composable
fun CalendarDates(
    uiState: State<MenuUiState>,
    currentDate: GregorianCalendar,
    weatherDays: List<Weather>,
    viewModel: MenuViewModel,
    innerPadding: PaddingValues,
    context: ProvidableCompositionLocal<Context>,
    changeView: (GregorianCalendar) -> Unit,
    monthsHolidays: List<HolidayModel>
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
            uiState = uiState,
            monthHolidays = monthsHolidays,
            weatherDays = weatherDays
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
                        color = Color(0.216f, 0.447f, 0.847f, 1.0f),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .height(35.dp)
                    .width(50.dp)
                    .padding(3.dp),
                color = Color.White,
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
    weatherDays: List<Weather>,
    viewModel: MenuViewModel,
    changeView: (GregorianCalendar) -> Unit,
    monthHolidays: List<HolidayModel>
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
        var dayContent = ""

        for (week in 0 until numberOfWeeks) {
            Row(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                var count = 0
                for (date in daysToSkip..numberOfDays) {
                    if (count == 7) {
                        break
                    }
                    if (date > 0) {
                        count += 1
                        var backgroundColor = Color(0.22f, 0.529f, 0.745f, 1.0f)
                        var borderColor = Brush.verticalGradient(listOf(Color(
                            0.22f,
                            0.529f,
                            0.745f,
                            1.0f
                        ), Color(0.22f, 0.529f, 0.745f, 1.0f)
                        ))

                        if (date == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) && currentDate.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) && currentDate.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                            backgroundColor = Color(19, 85, 201, 255)
                        }

                        for (holiday in monthHolidays) {
                            val day: String = if (date < 10) {
                                "0$date"
                            } else {
                                date.toString()
                            }

                            if (day == holiday.date.drop(8)) {
                                dayContent = "⁕"
                                break
                            }
                        }

                        for (weather in weatherDays) {
                            val dayOfWeather = extractDayFromDate(weather.id)
                            if (dayOfWeather == date &&currentDate.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) && currentDate.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                                if (daysWithEvents.contains(date)) {
                                    borderColor = when (weather.imgUrl) {
                                        R.drawable.clearicon -> Brush.verticalGradient(listOf(
                                            Color(255, 152, 0, 255),
                                            Color(255, 152, 0, 255)
                                        ))
                                        R.drawable.cloudicon -> Brush.verticalGradient(listOf(
                                            Color(87, 87, 87, 255),
                                            Color(84, 85, 85, 255)
                                        ))
                                        R.drawable.rainicon -> Brush.verticalGradient(listOf(
                                            Color(7, 98, 255, 255),
                                            Color(7, 98, 255, 255)
                                        ))
                                        R.drawable.snowicon -> Brush.verticalGradient(listOf(
                                            Color(7, 218, 255, 255),
                                            Color(7, 218, 255, 255)
                                        ))
                                        R.drawable.stormicon -> Brush.verticalGradient(listOf(
                                            Color(255, 222, 7, 255),
                                            Color(255, 235, 59, 255)
                                        ))
                                        else -> Brush.verticalGradient(listOf(
                                            Color(255, 222, 7, 255),
                                            Color(2, 184, 255, 255)
                                        ))
                                    }
                                } else {
                                    borderColor = when (weather.imgUrl) {
                                        R.drawable.clearicon -> Brush.verticalGradient(listOf(
                                            Color(255, 222, 7, 255),
                                            Color(255, 114, 7, 255)
                                        ))
                                        R.drawable.cloudicon -> Brush.verticalGradient(listOf(
                                            Color(87, 87, 87, 255),
                                            Color(7, 176, 255, 255)
                                        ))
                                        R.drawable.rainicon -> Brush.verticalGradient(listOf(
                                            Color(7, 176, 255, 255),
                                            Color(7, 98, 255, 255)
                                        ))
                                        R.drawable.snowicon -> Brush.verticalGradient(listOf(
                                            Color(7, 218, 255, 255),
                                            Color(250, 249, 243, 255)
                                        ))
                                        R.drawable.stormicon -> Brush.verticalGradient(listOf(
                                            Color(255, 222, 7, 255),
                                            Color(0, 0, 0, 255)
                                        ))
                                        else -> Brush.verticalGradient(listOf(
                                            Color(255, 222, 7, 255),
                                            Color(2, 184, 255, 255)
                                        ))
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                changeView(GregorianCalendar(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), date))
                            },
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(15.dp),
                            modifier = Modifier
                                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                                .border(width = 3.dp, brush = borderColor, shape = RoundedCornerShape(15.dp))
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (daysWithEvents.contains(date)) {
                                    Text(
                                        text = date.toString(),
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(25.dp)
                                            .background(backgroundColor),
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    if (dayContent != "") {
                                        Row {
                                            Text(
                                                text = dayContent,
                                                color = Color(1.0f, 0.757f, 0.027f, 1.0f),
                                                modifier = Modifier
                                                    .width(25.dp)
                                                    .height(25.dp)
                                                    .background(backgroundColor),
                                                textAlign = TextAlign.End
                                            )
                                            Text(
                                                text = "●",
                                                color = Color(0.831f, 0.169f, 0.502f, 1.0f),
                                                modifier = Modifier
                                                    .width(25.dp)
                                                    .height(25.dp)
                                                    .background(backgroundColor),
                                                textAlign = TextAlign.Start
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "●",
                                            color = Color(0.831f, 0.169f, 0.502f, 1.0f),
                                            modifier = Modifier
                                                .width(50.dp)
                                                .height(25.dp)
                                                .background(backgroundColor),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    if (dayContent != "") {
                                        Column {
                                            Text(
                                                text = date.toString(),
                                                modifier = Modifier
                                                    .width(50.dp)
                                                    .height(25.dp)
                                                    .background(backgroundColor),
                                                fontSize = 16.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = dayContent,
                                                color = Color(1.0f, 0.757f, 0.027f, 1.0f),
                                                modifier = Modifier
                                                    .width(50.dp)
                                                    .height(25.dp)
                                                    .background(backgroundColor),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = date.toString(),
                                            modifier = Modifier
                                                .width(50.dp)
                                                .height(50.dp)
                                                .background(backgroundColor),
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                dayContent = ""
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
                .padding(10.dp),
            tint = Color.White
        )
        Text(
            text = viewModel.getCurrentMonthName(monthDays, currentDate) + " " + currentDate.get(Calendar.YEAR),
            color = Color.White
        )
        Icon(
            Icons.Default.ArrowForward,
            contentDescription = context.current.resources.getString(R.string.backwards_button),
            modifier = Modifier
                .clickable {
                    viewModel.moveMonthForwards(currentDate, updateDate)
                }
                .padding(horizontal = 25.dp)
                .padding(top = 10.dp, bottom = 10.dp),
            tint = Color.White
        )
    }
}

fun extractDayFromDate(dateString: String): Int {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = dateFormat.parse(dateString)

    val calendar = Calendar.getInstance()
    calendar.time = date ?: Date()

    return calendar.get(Calendar.DAY_OF_MONTH)
}