package io.dev.kmpventas.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.dsl.module

val androidModule = module {
    single<Settings> {
        SharedPreferencesSettings(
            get<Context>().getSharedPreferences("kmp_ventas_prefs", Context.MODE_PRIVATE)
        )
    }
}