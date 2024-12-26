package com.fatihaltuntas.airqualityindex.view

import android.content.Context
import android.widget.TextView
import com.fatihaltuntas.airqualityindex.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    private val tvDate: TextView = findViewById(R.id.tvDate)
    private val tvValue: TextView = findViewById(R.id.tvValue)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val value = e?.y ?: return
        val timestamp = Date(e.x.toLong())
        
        val formattedDate = SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp)
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