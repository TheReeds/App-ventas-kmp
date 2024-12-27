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
import io.dev.kmpventas.data.repository.CatalogRepositoryImpl
import io.dev.kmpventas.data.repository.CategoryRepositoryImpl
import io.dev.kmpventas.data.repository.RoleRepositoryImpl
import io.dev.kmpventas.domain.repository.CatalogRepository
import io.dev.kmpventas.domain.repository.CategoryRepository
import io.dev.kmpventas.domain.repository.RoleRepository
import io.dev.kmpventas.presentation.screens.catalog.UnitMeasurementViewModel
import io.dev.kmpventas.presentation.screens.catalog.category.CategoryViewModel
import io.dev.kmpventas.presentation.screens.configuration.role.RoleViewModel
import io.dev.kmpventas.presentation.theme.ThemeViewModel
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

    // Catalog Module
    single<CatalogRepository> { CatalogRepositoryImpl(get()) }
    viewModel { UnitMeasurementViewModel(get()) }

    single { ThemeViewModel(get()) }
    // Category Module
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    viewModel { CategoryViewModel(get()) }
    // Role Module
    single<RoleRepository> { RoleRepositoryImpl(get()) }
    viewModel { RoleViewModel(get()) }


}