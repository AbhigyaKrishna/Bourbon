package me.abhigya.bourbon.domain

import android.location.Location

interface LocationProvider {

    suspend fun getLastKnownLocation(): Location?

    suspend fun getCountry(location: Location): String?

}