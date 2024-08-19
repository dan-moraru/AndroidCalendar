package ca.moraru.calendarapp.network

import ca.moraru.calendarapp.model.HolidayModel
import retrofit2.http.GET
import retrofit2.http.Path

interface HolidayApiService {
    @GET("PublicHolidays/{year}/{countryCode}")
    suspend fun getHolidays(@Path("countryCode") countryCode: String, @Path("year") year: Int) : List<HolidayModel>
}