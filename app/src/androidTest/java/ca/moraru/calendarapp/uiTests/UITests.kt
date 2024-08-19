package ca.moraru.calendarapp.uiTests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import ca.moraru.calendarapp.ui.navigation.create.CreateEventScreen
import ca.moraru.calendarapp.ui.navigation.date.DateScreen
import ca.moraru.calendarapp.ui.navigation.edit.EditEventScreen
import ca.moraru.calendarapp.ui.navigation.menu.MenuScreen
import ca.moraru.calendarapp.ui.navigation.view.ViewEventScreen
import org.junit.Rule
import org.junit.Test
import java.util.GregorianCalendar

class UITests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun uIMenuTest(){
        composeTestRule.setContent {
            MenuScreen(
                currentDate = GregorianCalendar(),
                updateDate = {},
                changeView = {},
                createEventNavigation = {}
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
                createEventNavigation = {}
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
                changeEditView = {}
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
                backNavigation = {}
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
                backNavigation = {}
            )
        }

        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()

        composeTestRule.onNodeWithText("Save").assertIsDisplayed()

        composeTestRule.onNodeWithText("Create Event").assertIsDisplayed()
    }
}