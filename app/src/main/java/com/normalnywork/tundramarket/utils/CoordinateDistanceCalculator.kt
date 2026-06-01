package com.normalnywork.tundramarket.utils

import com.normalnywork.tundramarket.domain.entities.Location
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object CoordinateDistanceCalculator {

    private const val EarthRadiusKm = 6371f

    infix fun Location.distanceTo(location: Location): Float {
        return calculateDistance(
            fromLatitude = latitude.toDouble(),
            fromLongitude = longitude.toDouble(),
            toLatitude = location.latitude.toDouble(),
            toLongitude = location.longitude.toDouble(),
        ).toFloat()
    }

    private fun calculateDistance(
        fromLatitude: Double,
        fromLongitude: Double,
        toLatitude: Double,
        toLongitude: Double,
    ): Double {
        val latitudeDistance = Math.toRadians(toLatitude - fromLatitude)
        val longitudeDistance = Math.toRadians(toLongitude - fromLongitude)
        val startLatitude = Math.toRadians(fromLatitude)
        val endLatitude = Math.toRadians(toLatitude)

        val haversine = sin(latitudeDistance / 2).pow(2) + sin(longitudeDistance / 2).pow(2) * cos(startLatitude) * cos(endLatitude)

        return EarthRadiusKm * 2 * atan2(sqrt(haversine), sqrt(1 - haversine))
    }
}