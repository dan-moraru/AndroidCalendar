package ca.moraru.calendarapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import ca.moraru.calendarapp.ui.navigation.CalendarApp
import ca.moraru.calendarapp.ui.theme.CalendarAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)

        setContent {
            CalendarAppTheme {
                CalendarApp(fusedLocationClient = fusedLocationClient)
            }
        }
    }
}