package ca.moraru.calendarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ca.moraru.calendarapp.ui.navigation.CalendarApp
import ca.moraru.calendarapp.ui.theme.CalendarAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarAppTheme {
                CalendarApp()
            }
        }
    }
}