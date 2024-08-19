package ca.moraru.calendarapp.navTests

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import ca.moraru.calendarapp.ui.navigation.CalendarApp
import ca.moraru.calendarapp.ui.navigation.CalendarScreen
import ca.moraru.calendarapp.R
import ca.moraru.calendarapp.data.CalendarDatabase
import ca.moraru.calendarapp.data.OfflineEventsRepository
import ca.moraru.calendarapp.ui.navigation.menu.MenuViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.junit.Test
import org.junit.Before
import org.junit.Rule

// DISCLAIMER : The navigation tests can only be run with pre-seeding the database
// At the current moment we do not have an alternative
// In the MenuViewModel.kt file, uncomment the method "setupEventList" on line 26
// WARNING: This will flush out your current data beware
// Because these tests we're thoroughly tested with the first milestone, it was not
// a main focus for this milestone
// Once you've run the app, make sure to re-comment the line to prevent test mishaps

class CalendarScreenNavTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Before
    fun setupCalendarNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
            CalendarApp(
                navController = navController,
                fusedLocationClient = fusedLocationProviderClient
            )
        }
    }

    @Test
    fun calendarNavHost_verifyStartDestination() {
        navController.assertCurrentRouteName(CalendarScreen.Menu.name)
    }

    @Test
    fun calendarNavHost_verifyBackNavigationNotShownOnMenuScreen() {
        val backText = composeTestRule.activity.getString(R.string.back_button)
        composeTestRule.onNodeWithContentDescription(backText).assertDoesNotExist()
    }

    @Test
    fun calendarNavHost_clickOneDay_navigatesToDayView() {
        composeTestRule.onAllNodesWithText("●")[0]
            .performClick()
        navController.assertCurrentRouteName(CalendarScreen.Date.name)
    }

    @Test
    fun calendarNavHost_clickBackFromDayScreen_navigatesToMenuScreen() {
        navigateToDayScreen()
        composeTestRule.onNodeWithStringId(R.string.back_button)
            .performClick()
        navController.assertCurrentRouteName(CalendarScreen.Menu.name)
    }

    @Test
    fun calendarNavHost_clickEventFromDayScreen_navigatesToEventScreen() {
        navigateToDayScreen()
        composeTestRule.onNodeWithText("Soccer")
            .performClick()
        navController.assertCurrentRouteName(CalendarScreen.ViewEvent.name)
    }

    @Test
    fun calendarNavHost_clickBackFromEventScreen_navigatesToDayScreen() {
        navigateToEventScreen()
        composeTestRule.onNodeWithStringId(R.string.back_button)
            .performClick()
        navController.assertCurrentRouteName(CalendarScreen.Date.name)
    }

    @Test
    fun calendarNavHost_clickEditFromEventScreen_navigatesToEditScreen() {
        navigateToEventScreen()
        composeTestRule.onNodeWithStringId(R.string.edit_button)
            .performClick()
        navController.assertCurrentRouteName(CalendarScreen.EditEvent.name)
    }

    @Test
    fun calendarNavHost_clickDeleteFromEventScreen_navigatesToMenuScreen() {
        navigateToRemoveableEventScreen()
        composeTestRule.onNodeWithStringId(R.string.delete_button)
            .performClick()
        composeTestRule.onNodeWithStringId(R.string.confirm)
            .performClick()
        navController.assertCurrentRouteName(CalendarScreen.Date.name)
    }

    @Test
    fun calendarNavHost_clickBackFromEditEventScreen_navigatesToEventScreen() {
        navigateToEditEventScreen()
        composeTestRule.onNodeWithText("Cancel")
            .performClick()
        navController.assertCurrentRouteName(CalendarScreen.ViewEvent.name)
    }

    @Test
    fun calendarNavHost_clickSaveFromEditEventScreen_navigatesToEventScreen() {
        navigateToEditEventScreen()
        composeTestRule.onNodeWithStringId(R.string.save_button)
            .performClick()
        navController.assertCurrentRouteName(CalendarScreen.ViewEvent.name)
    }

    @Test
    fun calendarNavHost_clickCreateFromMenu_navigatesToCreateView() {
        composeTestRule.onNodeWithStringId(R.string.create_button)
            .performClick()
        navController.assertCurrentRouteName(CalendarScreen.CreateEvent.name)
    }

    @Test
    fun calendarNavHost_clickCreateFromDay_navigatesToCreateView() {
        navigateToDayScreen()
        composeTestRule.onNodeWithStringId(R.string.create_button)
            .performClick()
        navController.assertCurrentRouteName(CalendarScreen.CreateEvent.name)
    }

    @Test
    fun calendarNavHost_clickCancelFromCreateFromMenu_navigatesToMenuView() {
        navigateToCreateScreenFromMenu()
        composeTestRule.onNodeWithText("Cancel")
            .performClick()
        navController.assertCurrentRouteName(CalendarScreen.Menu.name)
    }

    @Test
    fun calendarNavHost_clickCancelFromCreateFromDay_navigatesToDayView() {
        navigateToCreateScreenFromDay()
        composeTestRule.onNodeWithText("Cancel")
            .performClick()
        navController.assertCurrentRouteName(CalendarScreen.Date.name)
    }

    private fun navigateToDayScreen() {
        composeTestRule.onAllNodesWithText("●")[1]
            .performClick()
    }

    private fun navigateToCreateScreenFromMenu() {
        composeTestRule.onNodeWithStringId(R.string.create_button)
            .performClick()
    }

    private fun navigateToCreateScreenFromDay() {
        navigateToDayScreen()
        composeTestRule.onNodeWithStringId(R.string.create_button)
            .performClick()
    }

    private fun navigateToEventScreen() {
        navigateToDayScreen()
        composeTestRule.onNodeWithText("Soccer")
            .performClick()
    }

    private fun navigateToRemoveableEventScreen() {
        navigateToDayScreen()
        composeTestRule.onNodeWithText("Lunch")
            .performClick()
    }

    private fun navigateToEditEventScreen() {
        navigateToEventScreen()
        composeTestRule.onNodeWithStringId(R.string.edit_button)
            .performClick()
    }
}