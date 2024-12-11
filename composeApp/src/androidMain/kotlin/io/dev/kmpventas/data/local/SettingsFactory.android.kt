package io.dev.kmpventas.data.local

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.java.KoinJavaComponent.get

actual class SettingsFactory {
    actual fun createSettings(): Settings {
        val context: Context = get(Context::class.java)
        val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return SharedPreferencesSettings(sharedPreferences)
    }
}