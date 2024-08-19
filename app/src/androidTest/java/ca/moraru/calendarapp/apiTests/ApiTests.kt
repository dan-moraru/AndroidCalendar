package ca.moraru.calendarapp.apiTests

import ca.moraru.calendarapp.fakeApiData.FakeDataSource
import ca.moraru.calendarapp.fakeApiData.FakeHolidayApiService
import ca.moraru.calendarapp.fakeApiData.FakeWeatherApiService
import ca.moraru.calendarapp.model.NetworkHolidayRepository
import ca.moraru.calendarapp.model.NetworkWeatherRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ApiTests {

    @Test
    fun weatherRepositoryTest() =
        runTest {
            val repository = NetworkWeatherRepository(
                weatherApiService = FakeWeatherApiService()
            )
            assertEquals(FakeDataSource.weatherData, repository.getWeather(2.0, 1.5, "EST"))
        }

    @Test
    fun holidayRepositoryTest() =
        runTest {
            val repository = NetworkHolidayRepository(
                holidayApiService = FakeHolidayApiService()
            )
            assertEquals(FakeDataSource.holidayData, repository.getHolidayData("CA", 2023))
        }
}
