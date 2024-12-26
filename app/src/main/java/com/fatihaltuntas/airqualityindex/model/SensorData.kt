// SensorData.kt
package com.fatihaltuntas.airqualityindex.model

import com.google.firebase.database.PropertyName

data class SensorData(
    @get:PropertyName("timestamp")
    @set:PropertyName("timestamp")
    var timestamp: String = "",
    
    @get:PropertyName("air_quality")
    @set:PropertyName("air_quality")
    var air_quality: AirQuality = AirQuality(),
    
    @get:PropertyName("climate")
    @set:PropertyName("climate")
    var climate: Climate = Climate(),
    
    @get:PropertyName("gases")
    @set:PropertyName("gases")
    var gases: Gases = Gases()
) {
    val timestampLong: Long
        get() = timestamp.toLongOrNull() ?: 0L
}

data class AirQuality(
    @get:PropertyName("ppm")
    @set:PropertyName("ppm")
    var ppm: Double = 0.0,
    
    @get:PropertyName("status")
    @set:PropertyName("status")
    var status: String = ""
)

data class Climate(
    @get:PropertyName("temperature")
    @set:PropertyName("temperature")
    var temperature: Double = 0.0,
    
    @get:PropertyName("humidity")
    @set:PropertyName("humidity")
    var humidity: Double = 0.0
)

data class Gases(
    @get:PropertyName("lpg_ppm")
    @set:PropertyName("lpg_ppm")
    var lpg_ppm: Double = 0.0,
    
    @get:PropertyName("co_ppm")
    @set:PropertyName("co_ppm")
    var co_ppm: Double = 0.0,
    
    @get:PropertyName("smoke_ppm")
    @set:PropertyName("smoke_ppm")
    var smoke_ppm: Double = 0.0
)