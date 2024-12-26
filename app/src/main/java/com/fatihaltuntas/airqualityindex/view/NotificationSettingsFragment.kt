package com.fatihaltuntas.airqualityindex.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fatihaltuntas.airqualityindex.databinding.FragmentNotificationSettingsBinding
import com.fatihaltuntas.airqualityindex.util.PreferencesManager

class NotificationSettingsFragment : Fragment() {

    private lateinit var binding: FragmentNotificationSettingsBinding
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationSettingsBinding.inflate(inflater, container, false)
        preferencesManager = PreferencesManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        loadSettings()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadSettings() {
        binding.apply {
            airQualitySwitch.isChecked = preferencesManager.isNotificationEnabled("air_quality")
            temperatureSwitch.isChecked = preferencesManager.isNotificationEnabled("temperature")
            gasSwitch.isChecked = preferencesManager.isNotificationEnabled("gas")

            when (preferencesManager.getNotificationFrequency()) {
                "immediate" -> immediateRadio.isChecked = true
                "hourly" -> hourlyRadio.isChecked = true
                "daily" -> dailyRadio.isChecked = true
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            airQualitySwitch.setOnCheckedChangeListener { _, isChecked ->
                saveNotificationSetting("air_quality", isChecked)
            }

            temperatureSwitch.setOnCheckedChangeListener { _, isChecked ->
                saveNotificationSetting("temperature", isChecked)
            }

            gasSwitch.setOnCheckedChangeListener { _, isChecked ->
                saveNotificationSetting("gas", isChecked)
            }

            frequencyRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    immediateRadio.id -> saveFrequencySetting("immediate")
                    hourlyRadio.id -> saveFrequencySetting("hourly")
                    dailyRadio.id -> saveFrequencySetting("daily")
                }
            }
        }
    }

    private fun saveNotificationSetting(type: String, enabled: Boolean) {
        preferencesManager.setNotificationEnabled(type, enabled)
        val typeText = when (type) {
            "air_quality" -> "Hava kalitesi"
            "temperature" -> "Sıcaklık"
            "gas" -> "Gaz seviyesi"
            else -> return
        }
        Toast.makeText(context, "$typeText bildirimleri ${if (enabled) "açıldı" else "kapatıldı"}", Toast.LENGTH_SHORT).show()
    }

    private fun saveFrequencySetting(frequency: String) {
        preferencesManager.setNotificationFrequency(frequency)
        val message = when (frequency) {
            "immediate" -> "Anında bildirim"
            "hourly" -> "Saatlik özet"
            "daily" -> "Günlük özet"
            else -> return
        }
        Toast.makeText(context, "$message seçildi", Toast.LENGTH_SHORT).show()
    }
} 