package io.dev.kmpventas


import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import io.dev.kmpventas.data.local.SessionManager
import io.dev.kmpventas.di.appModule
import io.dev.kmpventas.presentation.navigation.NavigationGraph
import io.dev.kmpventas.presentation.screens.viewmodel.ProvideHomeViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinContext {
        val navController = rememberNavController()
        val sessionManager: SessionManager = koinInject()

        ProvideHomeViewModel {
            NavigationGraph(
                navController = navController,
                onLogout = {
                    // Limpiar la sesión
                    sessionManager.clearSession()
                    // Navegar al login y limpiar el back stack
                    navController.navigate("login") {
                        popUpTo(0.toString()) { inclusive = true }
                    }
                }
            )
        }
    }
}