package com.normalnywork.tundramarket.data.local.location

import com.normalnywork.tundramarket.domain.entities.Location
import kotlinx.coroutines.flow.Flow

interface CurrentLocationProvider {

    fun observeLocation(): Flow<LocationReading>
}

data class LocationReading(
    val location: Location,
    val accuracyMeters: Float?,
)
