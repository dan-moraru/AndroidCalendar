package ca.moraru.calendarapp.fakeApiData

import ca.moraru.calendarapp.model.HolidayModel
import ca.moraru.calendarapp.model.HolidayRepository
import ca.moraru.calendarapp.model.WeatherModel
import ca.moraru.calendarapp.model.WeatherRepository

class FakeNetworkWeatherRepository : WeatherRepository {
    override suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        timeZone: String
    ): WeatherModel {
        return FakeDataSource.weatherData
    }
}

class FakeNetworkHolidayRepository : HolidayRepository {
    override suspend fun getHolidayData(countryCode: String, year: Int): List<HolidayModel> {
        return FakeDataSource.holidayData
    }
}
