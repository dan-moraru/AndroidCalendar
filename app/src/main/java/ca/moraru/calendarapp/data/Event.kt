package ca.moraru.calendarapp.data

import java.time.LocalDate
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.lang.Integer.parseInt

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String,
    val description: String,
    val location: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val startTime: Double,
    val endTime: Double
)

fun doubleToHourString(doubleValue: Double): String {
    val intValue = doubleValue.toInt()
    val decimalValue = doubleValue - intValue
    val minutes = (decimalValue * 60).toInt()
    return "$intValue:${String.format("%02d", minutes)}"
}
