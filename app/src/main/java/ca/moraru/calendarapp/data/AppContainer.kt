package ca.moraru.calendarapp.data

import android.content.Context
import ca.moraru.calendarapp.model.HolidayRepository
import ca.moraru.calendarapp.model.NetworkHolidayRepository
import ca.moraru.calendarapp.model.NetworkWeatherRepository
import ca.moraru.calendarapp.model.WeatherRepository
import ca.moraru.calendarapp.network.HolidayApiService
import ca.moraru.calendarapp.network.WeatherApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val eventsRepository: EventsRepository
    val weatherRepository: WeatherRepository
    val holidayRepository: HolidayRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val baseUrlHoliday = "https://date.nager.at/api/v3/"
    private val baseUrlWeather = "https://api.open-meteo.com/v1/"

    private val holidayRetrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrlHoliday)
        .build()
    private val weatherRetrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrlWeather)
        .build()

    private val retrofitServiceHoliday: HolidayApiService by lazy {
        holidayRetrofit.create(HolidayApiService::class.java)
    }

    private val retrofitServiceWeather: WeatherApiService by lazy {
        weatherRetrofit.create(WeatherApiService::class.java)
    }

    override val eventsRepository: EventsRepository by lazy {
        OfflineEventsRepository(CalendarDatabase.getDatabase(context).eventDao())
    }

    override val holidayRepository: HolidayRepository by lazy {
        NetworkHolidayRepository(retrofitServiceHoliday)
    }

    override val weatherRepository: WeatherRepository by lazy {
        NetworkWeatherRepository(retrofitServiceWeather)
    }
}