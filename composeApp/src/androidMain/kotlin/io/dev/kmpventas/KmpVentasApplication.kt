package io.dev.kmpventas

import android.app.Application
import io.dev.kmpventas.di.androidModule
import io.dev.kmpventas.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@AndroidApp)
            modules(appModule)
        }
    }
}