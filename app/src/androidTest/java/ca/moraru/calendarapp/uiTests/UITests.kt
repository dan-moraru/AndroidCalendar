package ca.moraru.calendarapp.uiTests

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ca.moraru.calendarapp.data.CalendarDatabase
import ca.moraru.calendarapp.data.EventDao
import ca.moraru.calendarapp.data.EventsRepository
import ca.moraru.calendarapp.data.OfflineEventsRepository
import ca.moraru.calendarapp.data.Weather
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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.util.GregorianCalendar

class UITests {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var eventDao: EventDao
    private lateinit var calendarDatabase: CalendarDatabase
    private lateinit var repository: OfflineEventsRepository

    private lateinit var viewModelMenu: MenuViewModel
    private lateinit var viewModelDate: DateViewModel
    private lateinit var viewModelView: ViewEventViewModel
    private lateinit var viewModelEdit: EditEventViewModel
    private lateinit var viewModelCreate: CreateEventViewModel

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        calendarDatabase = Room.inMemoryDatabaseBuilder(context, CalendarDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        eventDao = calendarDatabase.eventDao()
        repository = OfflineEventsRepository(eventDao)

        viewModelMenu = MenuViewModel(repository)
        viewModelMenu.setupEventList()
        viewModelDate = DateViewModel(repository)
        viewModelView = ViewEventViewModel(repository)
        viewModelEdit = EditEventViewModel(repository)
        viewModelCreate = CreateEventViewModel(repository)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        if (::calendarDatabase.isInitialized) {
            calendarDatabase.close()
        }
    }

    @Test
    fun uIMenuTest(){
        composeTestRule.setContent {
            MenuScreen(
                currentDate = GregorianCalendar(),
                updateDate = {},
                changeView = {},
                createEventNavigation = {},
                viewModel = viewModelMenu,
                holidayData = emptyList(),
                weatherDays = emptyList()
            )
        }

        composeTestRule.onNodeWithText("+").assertIsDisplayed()

        composeTestRule.onAllNodesWithText("●")[0].assertIsDisplayed()

        composeTestRule.onNodeWithText("1").assertIsDisplayed()

        composeTestRule.onNodeWithText("Sun").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mon").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Wed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Thu").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fri").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sat").assertIsDisplayed()
    }

    @Test
    fun uIDayTest(){
        composeTestRule.setContent {
            DateScreen(
                currentDate = GregorianCalendar(),
                updateEvent = {},
                updateDate = {},
                backNavigation = {},
                viewEventNavigation = {},
                viewModel = viewModelDate,
                createEventNavigation = {},
                currentWeather = Weather("", 0.0, 0.0, 0),
                updateWeather = {},
                holidayData = emptyList()
            )
        }

        composeTestRule.onNodeWithText("❮").assertIsDisplayed()

        composeTestRule.onNodeWithText("❯").assertIsDisplayed()

        composeTestRule.onNodeWithText("+").assertIsDisplayed()

        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
    }

    @Test
    fun uIEventTest(){
        composeTestRule.setContent {
            ViewEventScreen(
                currentEventId = 1,
                backNavigation = {},
                changeEditView = {},
                viewModel = viewModelView
            )
        }

        composeTestRule.onNodeWithText("Back").assertIsDisplayed()

        composeTestRule.onNodeWithText("Edit").assertIsDisplayed()

        composeTestRule.onNodeWithText("Delete").assertIsDisplayed()

        composeTestRule.onNodeWithText("My Event").assertIsDisplayed()

        composeTestRule.onNodeWithText("Location").assertIsDisplayed()
    }

    @Test
    fun uIEditEventTest(){
        composeTestRule.setContent {
            EditEventScreen(
                currentEventId = 1,
                updateEvent = {},
                updateDate = {},
                backNavigation = {},
                viewModel = viewModelEdit
            )
        }

        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()

        composeTestRule.onNodeWithText("Save").assertIsDisplayed()

        composeTestRule.onNodeWithText("Edit Event").assertIsDisplayed()
    }

    @Test
    fun uICreateEventTest(){
        composeTestRule.setContent {
            CreateEventScreen(
                currentDate = GregorianCalendar(),
                updateDate = {},
                backNavigation = {},
                viewModel = viewModelCreate
            )
        }

        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()

        composeTestRule.onNodeWithText("Save").assertIsDisplayed()

        composeTestRule.onNodeWithText("Create Event").assertIsDisplayed()
    }
}