package com.normalnywork.tundramarket.data.local.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.normalnywork.tundramarket.domain.entities.Location
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.annotation.Singleton
import android.location.Location as AndroidLocation

@Singleton
class AndroidCurrentLocationProvider(private val context: Context) : CurrentLocationProvider {

    @SuppressLint("MissingPermission")
    override fun observeLocation(): Flow<LocationReading> = callbackFlow {
        if (!hasFineLocationPermission()) {
            close()
            return@callbackFlow
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            close()
            return@callbackFlow
        }

        val providers = LOCATION_PROVIDERS.filter { provider ->
            provider in locationManager.allProviders
        }
        val listener = android.location.LocationListener { location ->
            trySend(location.toLocationReading())
        }

        providers.forEach { provider ->
            runCatching {
                locationManager.getLastKnownLocation(provider)
                    ?.let { location -> trySend(location.toLocationReading()) }
            }
            runCatching {
                locationManager.requestLocationUpdates(
                    provider,
                    LOCATION_UPDATE_INTERVAL_MILLIS,
                    LOCATION_UPDATE_MIN_DISTANCE_METERS,
                    listener,
                    Looper.getMainLooper(),
                )
            }
        }

        awaitClose {
            locationManager.removeUpdates(listener)
        }
    }

    private fun hasFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun AndroidLocation.toLocationReading(): LocationReading {
        return LocationReading(
            location = Location(
                latitude = latitude.toFloat(),
                longitude = longitude.toFloat(),
            ),
            accuracyMeters = takeIf { hasAccuracy() }?.accuracy,
        )
    }

    private companion object {

        val LOCATION_PROVIDERS = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
        )

        const val LOCATION_UPDATE_INTERVAL_MILLIS = 1_000L
        const val LOCATION_UPDATE_MIN_DISTANCE_METERS = 0f
    }
}
