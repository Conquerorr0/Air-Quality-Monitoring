package com.fatihaltuntas.airqualityindex.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.fatihaltuntas.airqualityindex.databinding.FragmentThemeSettingsBinding
import com.fatihaltuntas.airqualityindex.util.PreferencesManager

class ThemeSettingsFragment : Fragment() {

    private lateinit var binding: FragmentThemeSettingsBinding
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThemeSettingsBinding.inflate(inflater, container, false)
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
            when (preferencesManager.getThemeMode()) {
                AppCompatDelegate.MODE_NIGHT_NO -> lightThemeRadio.isChecked = true
                AppCompatDelegate.MODE_NIGHT_YES -> darkThemeRadio.isChecked = true
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> systemThemeRadio.isChecked = true
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    lightThemeRadio.id -> setThemeMode(AppCompatDelegate.MODE_NIGHT_NO)
                    darkThemeRadio.id -> setThemeMode(AppCompatDelegate.MODE_NIGHT_YES)
                    systemThemeRadio.id -> setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }

    private fun setThemeMode(mode: Int) {
        preferencesManager.setThemeMode(mode)
        val message = when (mode) {
            AppCompatDelegate.MODE_NIGHT_NO -> "Açık tema"
            AppCompatDelegate.MODE_NIGHT_YES -> "Koyu tema"
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> "Sistem teması"
            else -> return
        }
        Toast.makeText(context, "$message seçildi", Toast.LENGTH_SHORT).show()
        
        // Tema değişikliğini hemen uygulamak için
        AppCompatDelegate.setDefaultNightMode(mode)
        activity?.recreate()
    }
} 