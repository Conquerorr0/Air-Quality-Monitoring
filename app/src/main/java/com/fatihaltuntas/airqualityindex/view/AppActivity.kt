package com.fatihaltuntas.airqualityindex.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.fatihaltuntas.airqualityindex.R
import com.fatihaltuntas.airqualityindex.databinding.ActivityAppBinding
import com.fatihaltuntas.airqualityindex.util.PreferencesManager

class AppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppBinding
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(this)
        
        // Kaydedilmiş tema ayarlarını yükle
        AppCompatDelegate.setDefaultNightMode(preferencesManager.getThemeMode())
        
        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation Controller'ı ayarla
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Bottom Navigation'ı Navigation Controller ile bağla
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}