package ca.moraru.calendarapp.model

import kotlinx.serialization.Serializable

@Serializable
data class HolidayModel (
    val date: String,
    val localName: String,
    val name: String,
    val countryCode: String,
    val fixed: Boolean,
    val global: Boolean,
    val counties: List<String>?,
    val launchYear: Int?,
    val types: List<String>
)

@Serializable
data class WeatherModel (
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Double,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val daily_units: daily_units,
    val daily: Daily
)

@Serializable
data class daily_units(
    val time: String,
    val weather_code: String,
    val temperature_2m_max: String,
    val temperature_2m_min: String
)

@Serializable
data class Daily(
    val time: List<String>,
    val weather_code: List<Int>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>
)