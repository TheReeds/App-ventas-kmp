package io.dev.kmpventas


import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import io.dev.kmpventas.data.local.SessionManager
import io.dev.kmpventas.di.appModule
import io.dev.kmpventas.presentation.navigation.NavigationGraph
import io.dev.kmpventas.presentation.screens.viewmodel.ProvideHomeViewModel
import io.dev.kmpventas.presentation.theme.AppTheme
import io.dev.kmpventas.presentation.theme.ThemeViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    val themeViewModel: ThemeViewModel = koinInject()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()

    AppTheme(darkTheme = isDarkMode) {
        KoinContext {
            val navController = rememberNavController()
            val sessionManager: SessionManager = koinInject()

            ProvideHomeViewModel {
                NavigationGraph(
                    navController = navController,
                    onLogout = {
                        sessionManager.clearSession()
                        navController.navigate("login") {
                            popUpTo(0.toString()) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}