package ca.moraru.calendarapp

import android.app.Application
import ca.moraru.calendarapp.data.AppContainer
import ca.moraru.calendarapp.data.AppDataContainer

class CalendarApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}