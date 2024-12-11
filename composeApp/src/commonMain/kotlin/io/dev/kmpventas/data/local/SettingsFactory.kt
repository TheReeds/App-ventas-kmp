package io.dev.kmpventas.data.local

import com.russhwolf.settings.Settings

expect class SettingsFactory() {
    fun createSettings(): Settings
}