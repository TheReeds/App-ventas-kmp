package io.dev.kmpventas.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.dev.kmpventas.data.local.SessionManager
import io.dev.kmpventas.presentation.screens.catalog.UnitMeasurementScreen
import io.dev.kmpventas.presentation.screens.catalog.category.CategoryScreen
import io.dev.kmpventas.presentation.screens.configuration.role.RoleScreen
import io.dev.kmpventas.presentation.screens.dashboard.DashboardScreen
import io.dev.kmpventas.presentation.screens.login.LoginScreen
import io.dev.kmpventas.presentation.screens.dashboard.HomeScreen
import io.dev.kmpventas.presentation.screens.dashboard.HomeViewModel
import io.dev.kmpventas.presentation.screens.dashboard.RolScreen
import io.dev.kmpventas.presentation.screens.dashboard.UserScreen
import io.dev.kmpventas.presentation.screens.navigation.BaseScreenLayout
import io.dev.kmpventas.presentation.screens.navigation.DefaultScreen
import io.dev.kmpventas.presentation.screens.navigation.OnboardingScreen
import io.dev.kmpventas.presentation.screens.navigation.SplashScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    onLogout: () -> Unit,
    sessionManager: SessionManager = koinInject()
) {
    val viewModel: HomeViewModel = koinInject()

    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (sessionManager.shouldRefreshToken() && destination.route != Routes.LOGIN) {
                if (!sessionManager.isRefreshTokenValid()) {
                    onLogout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0.toString()) { inclusive = true }
                    }
                }
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    when {
                        sessionManager.isFirstRun() -> {
                            navController.navigate(Routes.ONBOARDING) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                        sessionManager.isLoggedIn() && sessionManager.isRefreshTokenValid() -> {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                        else -> {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.SPLASH) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }
        // Login
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { user ->
                    navController.navigate(Routes.HOME) {
                        popUpTo(0.toString()) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onComplete = {
                    sessionManager.setFirstRunComplete()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // Home screen con drawer
        composable(Routes.HOME) {
            BaseScreenLayout(
                navController = navController,
                title = "Inicio",
                onLogout = {
                    onLogout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0.toString()) { inclusive = true }
                    }
                }
            ) { paddingValues ->
                DefaultScreen(
                    title = "Inicio",
                    route = Routes.HOME,
                    navController = navController,
                    onLogout = onLogout,
                    paddingValues = paddingValues
                )
            }
        }

        // Rutas del menú
        setupMenuRoutes(
            navGraphBuilder = this,
            navController = navController,
            onLogout = onLogout
        )
    }
}

private fun setupMenuRoutes(
    navGraphBuilder: NavGraphBuilder,
    navController: NavHostController,
    onLogout: () -> Unit
) {
    // Lista de rutas implementadas específicamente
    val implementedRoutes = mapOf(
        // Configuración
        Routes.HomeScreen.Setup.USER to "Usuarios",
        Routes.HomeScreen.Setup.USER_COMPANY to "Usuario empresa",
        Routes.HomeScreen.Setup.COMPANY to "Empresa",
        Routes.HomeScreen.Setup.MODULE to "Módulos",
        Routes.HomeScreen.Setup.PARENT_MODULE to "Módulos Padres",
        Routes.HomeScreen.Setup.ROLE to "Roles",

        // Catálogo
        Routes.HomeScreen.Catalog.UNIT_MEASUREMENT to "Unidad de Medida",
        Routes.HomeScreen.Catalog.CATEGORY to "Categoría",

        // Contabilidad
        Routes.HomeScreen.Accounting.AREAS to "Areas",
        Routes.HomeScreen.Accounting.TYPE_AFFECTATION to "Tipo de Afectación",
        Routes.HomeScreen.Accounting.TYPE_DOCUMENT to "Tipo de Documento",
        Routes.HomeScreen.Accounting.ACCOUNTING_PLAN to "Plan Contable",
        Routes.HomeScreen.Accounting.ACCOUNTING_DYNAMICS to "Dinámica Contable",
        Routes.HomeScreen.Accounting.ACCOUNTING_ACCOUNT_CLASS to "Clase Cuenta Contable",
        Routes.HomeScreen.Accounting.STORES to "Almacén"
    )


    // Registrar todas las rutas
    implementedRoutes.forEach { (route, title) ->
        navGraphBuilder.composable(route) {
            BaseScreenLayout(
                navController = navController,
                title = title,
                onLogout = onLogout
            ) { paddingValues ->
                when (route) {
                    Routes.HomeScreen.Setup.USER -> UserScreen(
                        navController = navController,
                        paddingValues = paddingValues
                    )
                    Routes.HomeScreen.Catalog.UNIT_MEASUREMENT -> UnitMeasurementScreen(
                        navController = navController,
                        paddingValues = paddingValues
                    )
                    Routes.HomeScreen.Catalog.CATEGORY -> CategoryScreen(
                        navController = navController,
                        paddingValues = paddingValues
                    )
                    Routes.HomeScreen.Setup.ROLE -> RoleScreen(
                        navController = navController,
                        paddingValues = paddingValues
                    )
                    else -> DefaultScreen(
                        title = title,
                        route = route,
                        navController = navController,
                        onLogout = onLogout,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}