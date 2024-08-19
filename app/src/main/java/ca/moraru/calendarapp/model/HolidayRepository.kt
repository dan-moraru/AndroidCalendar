package ca.moraru.calendarapp.model

import ca.moraru.calendarapp.network.HolidayApiService

interface HolidayRepository {
    suspend fun getHolidayData(countryCode: String, year: Int) : List<HolidayModel>
}

class NetworkHolidayRepository(
    private val holidayApiService: HolidayApiService
) : HolidayRepository {
    override suspend fun getHolidayData(countryCode: String, year: Int): List<HolidayModel> = holidayApiService.getHolidays(countryCode, year)
}