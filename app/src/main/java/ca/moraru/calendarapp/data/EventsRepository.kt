package ca.moraru.calendarapp.data

import kotlinx.coroutines.flow.Flow

interface EventsRepository {

    fun getDateEventsStream(year: Int, month: Int, day: Int): Flow<List<Event>>

    fun getMonthEventsStream(year: Int, month: Int): Flow<List<Event>>

    fun getEventStream(year: Int, month: Int, day: Int, startTime: Double) : Flow<Event>

    fun getEventByIdStream(eventId: Int) : Flow<Event>

    suspend fun deleteAllStream()

    suspend fun insertStream(event: Event)

    suspend fun updateStream(event: Event)

    suspend fun deleteStream(event: Event)
}