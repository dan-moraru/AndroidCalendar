package ca.moraru.calendarapp

import ca.moraru.calendarapp.data.doubleToHourString
import org.junit.Test

import org.junit.Assert.*

class FuncUnitTest {

    @Test
    fun doubleToHour_Test() {
        val hour = 3.0

        val result = doubleToHourString(hour)

        assertEquals("3:00", result)
    }

}