package com.fatihaltuntas.airqualityindex.DAO

import android.util.Log
import com.fatihaltuntas.airqualityindex.model.SensorData
import com.fatihaltuntas.airqualityindex.model.AirQuality
import com.fatihaltuntas.airqualityindex.model.Climate
import com.fatihaltuntas.airqualityindex.model.Gases
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class FirebaseHelper {
    private val database = FirebaseDatabase.getInstance(
        "https://air-quality-index-94061-default-rtdb.asia-southeast1.firebasedatabase.app"
    )
    private val averagesRef = database.reference.child("environmental_data/averages")
    private val realTimeDataRef = database.reference.child("environmental_data/real_time_readings/sensor_1")

    fun getLatestData(callback: (SensorData) -> Unit) {
        realTimeDataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        val timestamp = snapshot.child("timestamp").getValue(String::class.java) ?: ""
                        val airQuality = AirQuality(
                            ppm = snapshot.child("air_quality/ppm").getValue(Double::class.java) ?: 0.0,
                            status = snapshot.child("air_quality/status").getValue(String::class.java) ?: ""
                        )
                        val climate = Climate(
                            temperature = snapshot.child("climate/temperature").getValue(Double::class.java) ?: 0.0,
                            humidity = snapshot.child("climate/humidity").getValue(Double::class.java) ?: 0.0
                        )
                        val gases = Gases(
                            lpg_ppm = snapshot.child("gases/lpg_ppm").getValue(Double::class.java) ?: 0.0,
                            co_ppm = snapshot.child("gases/co_ppm").getValue(Double::class.java) ?: 0.0,
                            smoke_ppm = snapshot.child("gases/smoke_ppm").getValue(Double::class.java) ?: 0.0
                        )
                        val sensorData = SensorData(timestamp, airQuality, climate, gases)
                        callback(sensorData)
                    } catch (e: Exception) {
                        Log.e("FirebaseHelper", "Error parsing data: ${e.message}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseHelper", "Firebase error: ${error.message}")
            }
        })
    }

    fun getLast7DaysData(callback: (List<DailyData>) -> Unit) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dailyDataList = mutableListOf<DailyData>()

        averagesRef.child("daily").limitToLast(7)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dateSnapshot in snapshot.children) {
                        val date = dateSnapshot.key ?: continue
                        val data = parseDailyData(dateSnapshot, date)
                        dailyDataList.add(data)
                    }
                    callback(dailyDataList.sortedBy { it.date })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseHelper", "Error fetching daily data: ${error.message}")
                    callback(emptyList())
                }
            })
    }

    fun getLastMonthWeeklyData(callback: (List<WeeklyData>) -> Unit) {
        averagesRef.child("weekly").limitToLast(4)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val weeklyDataList = mutableListOf<WeeklyData>()
                    for (weekSnapshot in snapshot.children) {
                        val weekId = weekSnapshot.key ?: continue
                        val data = parseWeeklyData(weekSnapshot, weekId)
                        weeklyDataList.add(data)
                    }
                    callback(weeklyDataList.sortedBy { it.weekId })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseHelper", "Error fetching weekly data: ${error.message}")
                    callback(emptyList())
                }
            })
    }

    fun getMonthlyData(callback: (List<MonthlyData>) -> Unit) {
        averagesRef.child("monthly").limitToLast(12)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val monthlyDataList = mutableListOf<MonthlyData>()
                    for (monthSnapshot in snapshot.children) {
                        val monthId = monthSnapshot.key ?: continue
                        val data = parseMonthlyData(monthSnapshot, monthId)
                        monthlyDataList.add(data)
                    }
                    callback(monthlyDataList.sortedBy { it.monthId })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseHelper", "Error fetching monthly data: ${error.message}")
                    callback(emptyList())
                }
            })
    }

    private fun parseDailyData(snapshot: DataSnapshot, date: String): DailyData {
        return DailyData(
            date = date,
            airQualityPpm = snapshot.child("air_quality/avg_ppm").getValue(Double::class.java) ?: 0.0,
            temperature = snapshot.child("climate/temperature/avg").getValue(Double::class.java) ?: 0.0,
            humidity = snapshot.child("climate/humidity/avg").getValue(Double::class.java) ?: 0.0,
            lpgPpm = snapshot.child("gases/lpg/avg_ppm").getValue(Double::class.java) ?: 0.0,
            coPpm = snapshot.child("gases/co/avg_ppm").getValue(Double::class.java) ?: 0.0,
            smokePpm = snapshot.child("gases/smoke/avg_ppm").getValue(Double::class.java) ?: 0.0
        )
    }

    private fun parseWeeklyData(snapshot: DataSnapshot, weekId: String): WeeklyData {
        return WeeklyData(
            weekId = weekId,
            airQualityPpm = snapshot.child("air_quality/avg_ppm").getValue(Double::class.java) ?: 0.0,
            temperature = snapshot.child("climate/temperature/avg").getValue(Double::class.java) ?: 0.0,
            humidity = snapshot.child("climate/humidity/avg").getValue(Double::class.java) ?: 0.0,
            lpgPpm = snapshot.child("gases/lpg/avg_ppm").getValue(Double::class.java) ?: 0.0,
            coPpm = snapshot.child("gases/co/avg_ppm").getValue(Double::class.java) ?: 0.0,
            smokePpm = snapshot.child("gases/smoke/avg_ppm").getValue(Double::class.java) ?: 0.0
        )
    }

    private fun parseMonthlyData(snapshot: DataSnapshot, monthId: String): MonthlyData {
        return MonthlyData(
            monthId = monthId,
            airQualityPpm = snapshot.child("air_quality/avg_ppm").getValue(Double::class.java) ?: 0.0,
            temperature = snapshot.child("climate/temperature/avg").getValue(Double::class.java) ?: 0.0,
            humidity = snapshot.child("climate/humidity/avg").getValue(Double::class.java) ?: 0.0,
            lpgPpm = snapshot.child("gases/lpg/avg_ppm").getValue(Double::class.java) ?: 0.0,
            coPpm = snapshot.child("gases/co/avg_ppm").getValue(Double::class.java) ?: 0.0,
            smokePpm = snapshot.child("gases/smoke/avg_ppm").getValue(Double::class.java) ?: 0.0
        )
    }

    data class DailyData(
        val date: String,
        val airQualityPpm: Double,
        val temperature: Double,
        val humidity: Double,
        val lpgPpm: Double,
        val coPpm: Double,
        val smokePpm: Double
    )

    data class WeeklyData(
        val weekId: String,
        val airQualityPpm: Double,
        val temperature: Double,
        val humidity: Double,
        val lpgPpm: Double,
        val coPpm: Double,
        val smokePpm: Double
    )

    data class MonthlyData(
        val monthId: String,
        val airQualityPpm: Double,
        val temperature: Double,
        val humidity: Double,
        val lpgPpm: Double,
        val coPpm: Double,
        val smokePpm: Double
    )
}