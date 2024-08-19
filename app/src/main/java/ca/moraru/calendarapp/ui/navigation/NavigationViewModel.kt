package ca.moraru.calendarapp.ui.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.location.LocationRequest
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import ca.moraru.calendarapp.model.HolidayModel
import ca.moraru.calendarapp.model.HolidayRepository
import com.google.android.gms.location.FusedLocationProviderClient
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.viewModelScope
import ca.moraru.calendarapp.R
import ca.moraru.calendarapp.data.Weather
import ca.moraru.calendarapp.model.WeatherModel
import ca.moraru.calendarapp.model.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

data class NavigationUiState(
    val currentDate: GregorianCalendar = GregorianCalendar(),
    val currentEventId: Int = 0,
    val holidayData: List<HolidayModel> = listOf(),
    val currentWeather: Weather = Weather("", 0.0, 0.0, -1),
    val weatherList: List<Weather> = emptyList(),
    val currentLocation: Location? = null
)

class NavigationViewModel(
    private val holidayRepository: HolidayRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NavigationUiState())
    val uiState: StateFlow<NavigationUiState> = _uiState.asStateFlow()

    fun updateDate(currentDate: GregorianCalendar, context: Context) {
        val previousDate = _uiState.value.currentDate.get(Calendar.YEAR)

        _uiState.update { currentState ->
            currentState.copy(
                currentDate = currentDate
            )
        }

        if (_uiState.value.currentLocation != null) {
            if (previousDate != _uiState.value.currentDate.get(Calendar.YEAR)) {
                viewModelScope.launch {
                    updateHolidays(context, Locale.getDefault(), _uiState.value.currentLocation!!)
                }
            }
        }
    }

    fun updateEventId(eventId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                currentEventId = eventId
            )
        }
    }

    suspend fun updateLocation(fusedLocationClient: FusedLocationProviderClient, context: Context) {
        // Perform permissions request
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Before we request the location we need to update the fusedLocationClient
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location : Location? ->
                    if (location != null) {
                        try {
                            viewModelScope.launch {
                                updateHolidays(context, Locale.getDefault(), location)
                                updateWeatherList(location)
                            }
                        } catch (except: IOException) {
                            Log.d("Network Error", "Failed to load the APIs")
                        }
                        // Update the location
                        _uiState.update { currentState ->
                            currentState.copy(
                                currentLocation = location
                            )
                        }
                    }
                }
        }
    }

    @Suppress("DEPRECATION")
    private fun getCountryCode(context: Context, locale: Locale, location: Location) : String? {
        val geocoder = Geocoder(context, locale)
        val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (address != null) {
            val countryName = address[0].countryName
            val countries = Locale.getISOCountries()
            for (country in countries) {
                val countryLocale = Locale("", country)
                if (countryName.equals(countryLocale.displayCountry, ignoreCase = true)) {
                    return country
                }
            }
        }
        return null
    }

    private suspend fun updateHolidays(context: Context, locale: Locale, location: Location) {
        val countryCode = getCountryCode(context, locale, location)
        if (countryCode !== null) {
            runBlocking {
                val holidayData = holidayRepository.getHolidayData(
                    countryCode,
                    _uiState.value.currentDate.get(Calendar.YEAR)
                )

                _uiState.update { currentState ->
                    currentState.copy(
                        holidayData = holidayData
                    )
                }
            }
        }
    }

    fun updateWeather(currentDate: GregorianCalendar) {
        val formattedString = formatDate(currentDate)
        for (weather in _uiState.value.weatherList) {
            if (formattedString == weather.id) {
                _uiState.update { currentState ->
                    currentState.copy(
                        currentWeather = weather
                    )
                }
                return
            }
        }
        _uiState.update { currentState ->
            currentState.copy(
                currentWeather = Weather("", 0.0, 0.0, -1)
            )
        }
    }

    private fun updateWeatherList(location: Location?) {
        var weatherData: WeatherModel?

        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude
            val timeZone = TimeZone.getDefault().id

            runBlocking {
                weatherData = weatherRepository.getWeather(latitude, longitude, timeZone)
            }

            if (weatherData != null) {
                _uiState.update { currentState ->
                    currentState.copy(
                        weatherList = getWeatherOfDays(weatherData!!)
                    )
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(
                        weatherList = emptyWeatherData()
                    )
                }
            }
        }
    }

    private fun getWeatherOfDays(weatherModel: WeatherModel): List<Weather> {
        val weatherList = mutableListOf<Weather>()
        for (i in 0..6) {
            val id = weatherModel.daily.time[i]
            val maxTemp = weatherModel.daily.temperature_2m_max[i]
            val minTemp = weatherModel.daily.temperature_2m_min[i]
            val imgUrl = getWeatherImage(weatherModel.daily.weather_code[i])
            weatherList.add(Weather(id, maxTemp, minTemp, imgUrl))
        }
        return weatherList
    }

    private fun emptyWeatherData() : List<Weather> {
        val weatherList = mutableListOf<Weather>()
        for (i in 0..6) {
            weatherList.add(Weather("", 0.0, 0.0, 0))
        }
        return weatherList
    }

    private fun getWeatherImage(imgCOde: Int): Int {
        return when (imgCOde) {
            in 0..3, in 10..12 -> R.drawable.clearicon
            in 4..9, in 18..19, 28, in 30..49 -> R.drawable.cloudicon
            in 13..16, in 20..21, in 24..25, in 50..69, in 80..92 -> R.drawable.rainicon
            in 22..23, in 26..27, in 70..79, in 93..94 -> R.drawable.snowicon
            17, 29, in 95..99 -> R.drawable.stormicon
            else -> -1
        }
    }

    private fun formatDate(calendar: GregorianCalendar): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}