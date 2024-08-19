package ca.moraru.calendarapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface EventDao {

    @Query("SELECT * from events WHERE year = :year AND month = :month AND day = :day")
    fun getDateEvents(year: Int, month: Int, day: Int): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE year = :year AND month = :month")
    fun getMonthEvents(year: Int, month: Int): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE year = :year AND month = :month AND day = :day AND startTime = :startTime")
    fun getEvent(year: Int, month: Int, day: Int, startTime: Double): Flow<Event>

    @Query("SELECT * FROM events WHERE id = :eventId")
    fun getEventById(eventId: Int): Flow<Event>

    @Query("DELETE FROM events")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: Event)

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)
}