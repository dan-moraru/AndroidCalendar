package ca.moraru.calendarapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Event::class], version = 3, exportSchema = false)
abstract class CalendarDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var Instance: CalendarDatabase? = null

        fun getDatabase(context: Context): CalendarDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CalendarDatabase::class.java, "event_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}