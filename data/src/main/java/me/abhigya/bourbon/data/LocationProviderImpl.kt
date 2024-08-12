package me.abhigya.bourbon.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import me.abhigya.bourbon.domain.LocationProvider

class LocationProviderImpl(private val context: Context) : LocationProvider {

    private val client: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(context) }

    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): Location? {
        return runCatching { client.lastLocation.await() }.getOrNull()
    }

    override suspend fun getCountry(location: Location): String? {
        val geocoder = Geocoder(context)
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        return addresses?.firstOrNull()?.countryName
    }

}