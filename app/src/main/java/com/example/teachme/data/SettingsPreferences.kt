package com.example.teachme.data

import android.content.Context
import android.content.SharedPreferences

class SettingsPreferences(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var notificationsEnabled: Boolean
        get() = preferences.getBoolean(NOTIFICATIONS_ENABLED, true)
        set(value) = preferences.edit().putBoolean(NOTIFICATIONS_ENABLED, value).apply()

    companion object {
        private const val PREFS_NAME = "settings_prefs"
        private const val NOTIFICATIONS_ENABLED = "notifications_enabled"
    }
}
