package ca.moraru.calendarapp.data

import android.content.Context

interface AppContainer {
    val eventsRepository: EventsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val eventsRepository: EventsRepository by lazy {
        OfflineEventsRepository(CalendarDatabase.getDatabase(context).eventDao())
    }
}