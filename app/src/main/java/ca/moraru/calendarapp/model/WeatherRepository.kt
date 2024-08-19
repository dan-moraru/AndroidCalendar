package ca.moraru.calendarapp.model

import ca.moraru.calendarapp.network.WeatherApiService

interface WeatherRepository {
    suspend fun getWeather(latitude: Double, longitude: Double, timeZone: String) : WeatherModel
}

class NetworkWeatherRepository(
    private val weatherApiService: WeatherApiService
) : WeatherRepository {
    override suspend fun getWeather(latitude: Double, longitude: Double, timeZone: String) : WeatherModel = weatherApiService.getWeather(latitude, longitude, timeZone)
}