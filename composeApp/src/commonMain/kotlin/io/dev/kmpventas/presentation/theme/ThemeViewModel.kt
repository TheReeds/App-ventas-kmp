package io.dev.kmpventas.presentation.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel(private val settings: Settings) : ViewModel() {
    companion object {
        private const val DARK_MODE_KEY = "dark_mode_enabled"
    }

    private val _isDarkMode = MutableStateFlow(settings.getBoolean(DARK_MODE_KEY, false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleTheme() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        settings.putBoolean(DARK_MODE_KEY, newValue)
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        settings.putBoolean(DARK_MODE_KEY, enabled)
    }
}