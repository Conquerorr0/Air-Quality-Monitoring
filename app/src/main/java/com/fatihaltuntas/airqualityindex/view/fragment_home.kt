package com.fatihaltuntas.airqualityindex.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fatihaltuntas.airqualityindex.R
import com.fatihaltuntas.airqualityindex.databinding.FragmentHomeBinding
import com.fatihaltuntas.airqualityindex.DAO.FirebaseHelper
import com.fatihaltuntas.airqualityindex.model.SensorData

class fragment_home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseHelper = FirebaseHelper()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show loading state
        binding.txtAirQuality.text = "Yükleniyor..."
        binding.txtAirQualityStatus.text = ""
        binding.txtTemprature.text = "Yükleniyor..."
        binding.txtHumidity.text = "Yükleniyor..."
        binding.txtLPGValue.text = "Yükleniyor..."
        binding.txtCOValue.text = "Yükleniyor..."
        binding.txtSmokeValue.text = "Yükleniyor..."

        // Get and display real-time data from Firebase
        firebaseHelper.getLatestData { data ->
            activity?.runOnUiThread {
                // Air Quality
                binding.txtAirQuality.text = "Hava Kalitesi: ${data.air_quality.ppm}"
                updateAirQualityStatus(binding.txtAirQualityStatus, data.air_quality.status)
                
                // Climate data
                binding.txtTemprature.text = "Sıcaklık: ${data.climate.temperature} °C"
                binding.txtHumidity.text = "Nem: ${data.climate.humidity} %"

                // Gas data
                updateGasValues(data)
            }
        }
    }

    private fun updateAirQualityStatus(textView: TextView, status: String) {
        textView.text = status
        val color = when (status.lowercase()) {
            "iyi" -> resources.getColor(R.color.status_good, null)
            "orta" -> resources.getColor(R.color.status_moderate, null)
            "hassas" -> resources.getColor(R.color.status_sensitive, null)
            "sağlıksız" -> resources.getColor(R.color.status_unhealthy, null)
            "kötü" -> resources.getColor(R.color.status_very_unhealthy, null)
            "tehlikeli" -> resources.getColor(R.color.status_hazardous, null)
            else -> Color.BLACK
        }
        textView.setTextColor(color)
        binding.txtAirQuality.setTextColor(color) // Ana hava kalitesi metninin rengini de güncelle
        textView.textSize = 24f
    }

    private fun updateGasValues(data: SensorData) {
        // LPG değeri güncelleme
        binding.txtLPGValue.text = "%.2f".format(data.gases.lpg_ppm)
        binding.txtLPGUnit.text = "milyonda parça (Sıvı Petrol Gazı)"

        // CO değeri güncelleme
        binding.txtCOValue.text = "%.2f".format(data.gases.co_ppm)
        binding.txtCOUnit.text = "milyonda parça (Karbon Monoksit)"

        // Duman değeri güncelleme
        binding.txtSmokeValue.text = "%.2f".format(data.gases.smoke_ppm)
        binding.txtSmokeUnit.text = "milyonda parça (Duman Yoğunluğu)"
    }
}
