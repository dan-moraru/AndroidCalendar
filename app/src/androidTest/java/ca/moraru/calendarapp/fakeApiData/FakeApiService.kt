package ca.moraru.calendarapp.fakeApiData

import ca.moraru.calendarapp.model.HolidayModel
import ca.moraru.calendarapp.model.WeatherModel
import ca.moraru.calendarapp.network.HolidayApiService
import ca.moraru.calendarapp.network.WeatherApiService

class FakeWeatherApiService : WeatherApiService {
    override suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        timeZone: String
    ): WeatherModel {
        return FakeDataSource.weatherData
    }
}

class FakeHolidayApiService : HolidayApiService {
    override suspend fun getHolidays(countryCode: String, year: Int): List<HolidayModel> {
        return FakeDataSource.holidayData
    }
}