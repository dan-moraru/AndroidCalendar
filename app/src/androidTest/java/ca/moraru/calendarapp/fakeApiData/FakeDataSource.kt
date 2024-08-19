package ca.moraru.calendarapp.fakeApiData

import ca.moraru.calendarapp.model.Daily
import ca.moraru.calendarapp.model.HolidayModel
import ca.moraru.calendarapp.model.WeatherModel
import ca.moraru.calendarapp.model.daily_units


object FakeDataSource {
    val holidayData = listOf(
        HolidayModel(
            date = "2023-12-12",
            localName = "Name",
            name = "Name",
            countryCode = "CA",
            fixed = true,
            global = true,
            counties = listOf("QC", "AB"),
            launchYear = null,
            types = listOf("One", "Two")
        ),
        HolidayModel(
            date = "2023-12-13",
            localName = "Name2",
            name = "Name2",
            countryCode = "US",
            fixed = true,
            global = true,
            counties = listOf("Texas", "LA"),
            launchYear = null,
            types = listOf("Three", "Four")
        )
    )
    val weatherData = WeatherModel(
        latitude=2.0,
        longitude=1.25,
        generationtime_ms=0.05602836608886719,
        utc_offset_seconds=-18000.0,
        timezone="EST",
        timezone_abbreviation="EST",
        elevation=0.0,
        daily_units= daily_units(
            time="iso8601",
            weather_code="wmo code",
            temperature_2m_max="° C",
        temperature_2m_min="°C"
        ),
        daily= Daily(
            time= listOf("2023-12-12", "2023-12-13", "2023-12-14", "2023-12-15", "2023-12-16", "2023-12-17", "2023-12-18"),
            weather_code=listOf(80, 80, 95, 95, 2, 3, 3),
            temperature_2m_max=listOf(28.4, 29.2, 29.2, 28.8, 29.0, 28.9, 28.9),
            temperature_2m_min=listOf(27.4, 28.0, 27.0, 27.5, 28.3, 28.3, 28.1)
        )
    )
}
