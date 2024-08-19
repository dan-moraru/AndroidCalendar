package ca.moraru.calendarapp.network

import ca.moraru.calendarapp.model.WeatherModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.TimeZone

interface WeatherApiService {
    @GET("forecast?daily=weather_code,temperature_2m_max,temperature_2m_min")
    suspend fun getWeather(@Query("latitude") latitude: Double, @Query("longitude") longitude: Double, @Query("timezone") timeZone: String) : WeatherModel
}