package ca.moraru.calendarapp.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class OfflineEventsRepository(private val eventDao: EventDao) : EventsRepository {

    override fun getDateEventsStream(year: Int, month: Int, day: Int): Flow<List<Event>> = eventDao.getDateEvents(year, month, day)

    override fun getMonthEventsStream(year: Int, month: Int): Flow<List<Event>> = eventDao.getMonthEvents(year, month)

    override fun getEventStream(year: Int, month: Int, day: Int, startTime: Double) : Flow<Event> = eventDao.getEvent(year, month, day, startTime)

    override fun getEventByIdStream(eventId: Int) : Flow<Event> = eventDao.getEventById(eventId)

    override suspend fun deleteAllStream() = eventDao.deleteAll()

    override suspend fun insertStream(event: Event) = eventDao.insert(event)

    override suspend fun updateStream(event: Event) = eventDao.update(event)

    override suspend fun deleteStream(event: Event) = eventDao.delete(event)
}