// FragmentHistory.kt
package com.fatihaltuntas.airqualityindex.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.fatihaltuntas.airqualityindex.R
import com.fatihaltuntas.airqualityindex.databinding.FragmentHistoryBinding
import com.fatihaltuntas.airqualityindex.DAO.FirebaseHelper
import com.fatihaltuntas.airqualityindex.model.SensorData
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class FragmentHistory : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var firebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseHelper = FirebaseHelper()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCharts()
        loadDailyData() // Başlangıçta günlük verileri yükle

        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadDailyData()
                    1 -> loadWeeklyData()
                    2 -> loadMonthlyData()
                }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun setupCharts() {
        charts.forEach { chart ->
            chart.apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                setDrawGridBackground(false)
                
                // Animasyon ayarları
                animateX(1000, Easing.EaseInOutQuart)
                
                // X ekseni ayarları
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = resources.getColor(R.color.black, null)
                    setDrawGridLines(true)
                    gridColor = resources.getColor(R.color.gray, null)
                    gridLineWidth = 0.5f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return SimpleDateFormat("HH:mm", Locale.getDefault())
                                .format(Date(value.toLong()))
                        }
                    }
                }

                // Sol Y ekseni ayarları
                axisLeft.apply {
                    textColor = resources.getColor(R.color.black, null)
                    setDrawGridLines(true)
                    gridColor = resources.getColor(R.color.gray, null)
                    gridLineWidth = 0.5f
                    enableGridDashedLine(10f, 10f, 0f)
                }

                // Sağ Y ekseni kapalı
                axisRight.isEnabled = false

                // Legend ayarları
                legend.apply {
                    textColor = resources.getColor(R.color.black, null)
                    textSize = 12f
                    form = Legend.LegendForm.CIRCLE
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(false)
                }

                // Marker view ayarları
                marker = CustomMarkerView(requireContext(), R.layout.marker_view)
            }
        }
    }

    private fun updateChartData(chart: com.github.mikephil.charting.charts.LineChart, 
                              entries: List<Entry>, 
                              label: String, 
                              color: Int,
                              format: String = "%.1f") {
        val dataSet = LineDataSet(entries, label).apply {
            this.color = color
            setCircleColor(color)
            lineWidth = 2f
            circleRadius = 4f
            valueTextSize = 10f
            valueTextColor = resources.getColor(R.color.black, null)
            setDrawValues(true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format(format, value)
                }
            }
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
            setDrawFilled(true)
            fillAlpha = 30
            fillColor = color
            highLightColor = resources.getColor(R.color.accent, null)
            setDrawHorizontalHighlightIndicator(true)
            setDrawVerticalHighlightIndicator(true)
        }

        chart.data = LineData(dataSet)
        chart.animateY(1000, Easing.EaseInOutQuart)
        chart.invalidate()
    }

    private fun showLoading() {
        charts.forEach { chart ->
            chart.isVisible = false
        }
        binding.progressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.progressBar.isVisible = false
        charts.forEach { chart ->
            chart.isVisible = true
        }
    }

    private fun loadDailyData() {
        showLoading()
        firebaseHelper.getLast7DaysData { dataList ->
            if (dataList.isEmpty()) {
                activity?.runOnUiThread {
                    hideLoading()
                    // TODO: Show empty state
                }
                return@getLast7DaysData
            }
            
            activity?.runOnUiThread {
                // Günlük görünüm için tarih formatını güncelle
                val dates = dataList.map { it.date }
                charts.forEach { chart ->
                    chart.xAxis.apply {
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val index = value.toInt()
                                if (index >= 0 && index < dates.size) {
                                    return SimpleDateFormat("dd/MM", Locale.getDefault())
                                        .format(parseDate(dates[index]))
                                }
                                return ""
                            }
                        }
                        labelCount = dates.size
                        setLabelCount(dates.size, true)
                    }
                }

                // Hava Kalitesi Grafiği
                updateChartData(
                    binding.airQualityChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.airQualityPpm.toFloat()) 
                    },
                    "Hava Kalitesi (ppm)",
                    resources.getColor(R.color.primary, null)
                )

                // Sıcaklık Grafiği
                updateChartData(
                    binding.temperatureChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.temperature.toFloat()) 
                    },
                    "Sıcaklık (°C)",
                    resources.getColor(R.color.temperature_color, null),
                    "%.1f°C"
                )

                // Nem Grafiği
                updateChartData(
                    binding.humidityChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.humidity.toFloat()) 
                    },
                    "Nem (%)",
                    resources.getColor(R.color.humidity_color, null),
                    "%%%.0f"
                )

                // LPG Grafiği
                updateChartData(
                    binding.lpgChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.lpgPpm.toFloat()) 
                    },
                    "LPG (ppm)",
                    resources.getColor(R.color.lpg_color, null)
                )

                // CO Grafiği
                updateChartData(
                    binding.coChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.coPpm.toFloat()) 
                    },
                    "CO (ppm)",
                    resources.getColor(R.color.co_color, null)
                )

                // Duman Grafiği
                updateChartData(
                    binding.smokeChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.smokePpm.toFloat()) 
                    },
                    "Duman (ppm)",
                    resources.getColor(R.color.smoke_color, null)
                )

                hideLoading()
            }
        }
    }

    private fun loadWeeklyData() {
        showLoading()
        firebaseHelper.getLastMonthWeeklyData { dataList ->
            if (dataList.isEmpty()) {
                activity?.runOnUiThread {
                    hideLoading()
                    // TODO: Show empty state
                }
                return@getLastMonthWeeklyData
            }
            
            activity?.runOnUiThread {
                // Haftalık görünüm için tarih formatını güncelle
                val weekIds = dataList.map { it.weekId }
                charts.forEach { chart ->
                    chart.xAxis.apply {
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val index = value.toInt()
                                if (index >= 0 && index < weekIds.size) {
                                    val weekId = weekIds[index]
                                    return "Hafta ${weekId.substring(weekId.length - 2)}"
                                }
                                return ""
                            }
                        }
                        labelCount = weekIds.size
                        setLabelCount(weekIds.size, true)
                    }
                }

                // Hava Kalitesi Grafiği
                updateChartData(
                    binding.airQualityChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.airQualityPpm.toFloat()) 
                    },
                    "Hava Kalitesi (ppm)",
                    resources.getColor(R.color.primary, null)
                )

                // Sıcaklık Grafiği
                updateChartData(
                    binding.temperatureChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.temperature.toFloat()) 
                    },
                    "Sıcaklık (°C)",
                    resources.getColor(R.color.temperature_color, null),
                    "%.1f°C"
                )

                // Nem Grafiği
                updateChartData(
                    binding.humidityChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.humidity.toFloat()) 
                    },
                    "Nem (%)",
                    resources.getColor(R.color.humidity_color, null),
                    "%%%.0f"
                )

                // LPG Grafiği
                updateChartData(
                    binding.lpgChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.lpgPpm.toFloat()) 
                    },
                    "LPG (ppm)",
                    resources.getColor(R.color.lpg_color, null)
                )

                // CO Grafiği
                updateChartData(
                    binding.coChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.coPpm.toFloat()) 
                    },
                    "CO (ppm)",
                    resources.getColor(R.color.co_color, null)
                )

                // Duman Grafiği
                updateChartData(
                    binding.smokeChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.smokePpm.toFloat()) 
                    },
                    "Duman (ppm)",
                    resources.getColor(R.color.smoke_color, null)
                )

                hideLoading()
            }
        }
    }

    private fun loadMonthlyData() {
        showLoading()
        firebaseHelper.getMonthlyData { dataList ->
            if (dataList.isEmpty()) {
                activity?.runOnUiThread {
                    hideLoading()
                    // TODO: Show empty state
                }
                return@getMonthlyData
            }
            
            activity?.runOnUiThread {
                // Aylık görünüm için tarih formatını güncelle
                val monthIds = dataList.map { it.monthId }
                charts.forEach { chart ->
                    chart.xAxis.apply {
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val index = value.toInt()
                                if (index >= 0 && index < monthIds.size) {
                                    return SimpleDateFormat("MMM", Locale.getDefault())
                                        .format(SimpleDateFormat("yyyy-MM", Locale.getDefault()).parse(monthIds[index]))
                                }
                                return ""
                            }
                        }
                        labelCount = monthIds.size
                        setLabelCount(monthIds.size, true)
                    }
                }

                // Hava Kalitesi Grafiği
                updateChartData(
                    binding.airQualityChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.airQualityPpm.toFloat()) 
                    },
                    "Hava Kalitesi (ppm)",
                    resources.getColor(R.color.primary, null)
                )

                // Sıcaklık Grafiği
                updateChartData(
                    binding.temperatureChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.temperature.toFloat()) 
                    },
                    "Sıcaklık (°C)",
                    resources.getColor(R.color.temperature_color, null),
                    "%.1f°C"
                )

                // Nem Grafiği
                updateChartData(
                    binding.humidityChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.humidity.toFloat()) 
                    },
                    "Nem (%)",
                    resources.getColor(R.color.humidity_color, null),
                    "%%%.0f"
                )

                // LPG Grafiği
                updateChartData(
                    binding.lpgChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.lpgPpm.toFloat()) 
                    },
                    "LPG (ppm)",
                    resources.getColor(R.color.lpg_color, null)
                )

                // CO Grafiği
                updateChartData(
                    binding.coChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.coPpm.toFloat()) 
                    },
                    "CO (ppm)",
                    resources.getColor(R.color.co_color, null)
                )

                // Duman Grafiği
                updateChartData(
                    binding.smokeChart,
                    dataList.mapIndexed { index, data -> 
                        Entry(index.toFloat(), data.smokePpm.toFloat()) 
                    },
                    "Duman (ppm)",
                    resources.getColor(R.color.smoke_color, null)
                )

                hideLoading()
            }
        }
    }

    private fun parseDate(dateStr: String): Date {
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr) ?: Date()
        } catch (e: Exception) {
            Log.e("FragmentHistory", "Error parsing date: $dateStr", e)
            Date()
        }
    }

    inner class CustomMarkerView(context: android.content.Context, layoutResource: Int) : 
        MarkerView(context, layoutResource) {
        
        private val tvDate: TextView = findViewById(R.id.tvDate)
        private val tvValue: TextView = findViewById(R.id.tvValue)
        
        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            val value = e?.y ?: return
            val timestamp = Date(e.x.toLong())
            val dateFormat = when {
                binding.tabLayout.selectedTabPosition == 0 -> "HH:mm"
                binding.tabLayout.selectedTabPosition == 1 -> "dd/MM HH:mm"
                else -> "dd/MM"
            }
            
            val formattedDate = SimpleDateFormat(dateFormat, Locale.getDefault()).format(timestamp)
            val formattedValue = when (highlight?.dataSetIndex) {
                0 -> String.format("%.1f ppm", value) // Hava Kalitesi
                1 -> String.format("%.1f°C", value)   // Sıcaklık
                2 -> String.format("%%.0f%%", value)  // Nem
                3 -> String.format("%.1f ppm", value) // LPG
                4 -> String.format("%.1f ppm", value) // CO
                5 -> String.format("%.1f ppm", value) // Duman
                else -> String.format("%.1f", value)
            }
            
            tvDate.text = formattedDate
            tvValue.text = formattedValue
        }

        override fun getOffset(): MPPointF {
            return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
        }
    }

    private val charts by lazy {
        listOf(
            binding.airQualityChart,
            binding.temperatureChart,
            binding.humidityChart,
            binding.lpgChart,
            binding.coChart,
            binding.smokeChart
        )
    }
}