package io.dev.kmpventas.di

import io.dev.kmpventas.data.remote.api.ApiConstants
import io.dev.kmpventas.data.remote.api.ApiService
import io.dev.kmpventas.data.repository.AuthRepositoryImpl
import io.dev.kmpventas.domain.repository.AuthRepository
import io.dev.kmpventas.domain.usecase.LoginUseCase
import io.dev.kmpventas.presentation.screens.dashboard.HomeViewModel
import io.dev.kmpventas.presentation.screens.login.LoginViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.singleOf
import com.russhwolf.settings.Settings
import io.dev.kmpventas.data.local.SessionManager
import io.dev.kmpventas.data.local.SettingsFactory
import io.ktor.client.plugins.HttpTimeout

val appModule = module {
    // Settings y SessionManager
    single { SettingsFactory().createSettings() }
    single { SessionManager(get()) }

    // HTTP Client
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 15000
            }
            defaultRequest {
                url(ApiConstants.BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
    }

    // API y Repository
    single { ApiService(get()) }
    single<AuthRepository> {
        AuthRepositoryImpl(
            apiService = get(),
            sessionManager = get()
        )
    }

    // UseCases
    singleOf(::LoginUseCase)

    // ViewModels
    viewModel {
        LoginViewModel(
            loginUseCase = get(),
            sessionManager = get()
        )
    }

    single {
        HomeViewModel(
            authRepository = get(),
            sessionManager = get()
        )
    }
}