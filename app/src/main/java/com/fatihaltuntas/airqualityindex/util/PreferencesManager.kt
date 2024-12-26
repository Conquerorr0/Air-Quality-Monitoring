package com.fatihaltuntas.airqualityindex.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Bildirim ayarları
    fun setNotificationEnabled(type: String, enabled: Boolean) {
        prefs.edit().putBoolean("notification_$type", enabled).apply()
    }

    fun isNotificationEnabled(type: String): Boolean {
        return prefs.getBoolean("notification_$type", true)
    }

    fun setNotificationFrequency(frequency: String) {
        prefs.edit().putString("notification_frequency", frequency).apply()
    }

    fun getNotificationFrequency(): String {
        return prefs.getString("notification_frequency", "immediate") ?: "immediate"
    }

    // Tema ayarları
    fun setThemeMode(mode: Int) {
        prefs.edit().putInt("theme_mode", mode).apply()
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun getThemeMode(): Int {
        return prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun setColorTheme(theme: String) {
        prefs.edit().putString("color_theme", theme).apply()
    }

    fun getColorTheme(): String {
        return prefs.getString("color_theme", "default") ?: "default"
    }

    companion object {
        private const val PREFS_NAME = "air_quality_prefs"
    }
} 