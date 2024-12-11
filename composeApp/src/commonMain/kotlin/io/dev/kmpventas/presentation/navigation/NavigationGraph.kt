package io.dev.kmpventas.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.dev.kmpventas.data.local.SessionManager
import io.dev.kmpventas.presentation.screens.dashboard.DashboardScreen
import io.dev.kmpventas.presentation.screens.login.LoginScreen
import io.dev.kmpventas.presentation.screens.dashboard.HomeScreen
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
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Splash Screen
        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    if (sessionManager.isFirstRun()) {
                        navController.navigate("onboarding") {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else if (sessionManager.isLoggedIn()) {
                        navController.navigate("home") {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            )
        }
        // Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = { user ->
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("onboarding") {
            OnboardingScreen(
                onComplete = {
                    sessionManager.setFirstRunComplete()
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // Home screen con drawer
        composable("home") {
            BaseScreenLayout(
                navController = navController,
                title = "Inicio",
                onLogout = {
                    onLogout()
                    navController.navigate("login") {
                        popUpTo(0.toString()) { inclusive = true }
                    }
                }
            ) { paddingValues ->
                // Aquí puedes poner el contenido específico del Home
                // Por ahora usaremos DefaultScreen
                DefaultScreen(
                    title = "Inicio",
                    route = "home",
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
        "/pages/dashboard" to "Dashboard",
        "/pages/setup/rol" to "Roles",
        "/pages/setup/user" to "Usuarios",
        // Añade aquí más rutas implementadas cuando las crees
    )

    // Rutas principales
    val mainRoutes = listOf(
        "/pages/setup" to "Configuración",
        "/pages/warehouse" to "Almacén",
        "/pages/purchase" to "Compras",
        "/pages/sale" to "Ventas",
        "/pages/cash" to "Caja",
        "/pages/report" to "Reportes"
    )

    // Registrar rutas principales
    mainRoutes.forEach { (route, title) ->
        navGraphBuilder.composable(route) {
            BaseScreenLayout(
                navController = navController,
                title = title,
                onLogout = onLogout
            ) { paddingValues ->
                DefaultScreen(
                    title = title,
                    route = route,
                    navController = navController,
                    onLogout = onLogout,
                    paddingValues = paddingValues
                )
            }
        }
    }

    // Lista completa de todas las rutas posibles
    val allRoutes = listOf(
        "/pages/dashboard" to "Dashboard",

        // Configuración
        "/pages/setup/rol" to "Roles",
        "/pages/setup/user" to "Usuarios",
        "/pages/setup/person" to "Clientes",
        "/pages/setup/period" to "Periodos",
        "/pages/setup/type-voucher" to "Tipo Documento",
        "/pages/setup/company" to "Empresa",
        "/pages/setup/accounting-seat" to "Asientos Contables",
        "/pages/setup/users-online" to "Usuarios Conectados",

        // Almacén
        "/pages/warehouse/measuredunit" to "Unidad de Medida",
        "/pages/warehouse/category" to "Categorías",
        "/pages/warehouse/product" to "Productos",
        "/pages/warehouse/provider" to "Proveedores",
        "/pages/warehouse/warehouse" to "Almacenes",
        "/pages/warehouse/product-barcode-qrcode" to "Código Barras y QR",
        "/pages/warehouse/activate-store" to "Activar Almacén",
        "/pages/warehouse/products-stock" to "Stock de Productos",
        "/pages/warehouse/warehouse-transfer" to "Movimiento Almacén",
        "/pages/warehouse/kardex" to "Kardex",
        "/pages/warehouse/distributor" to "Vendedores",
        "/pages/warehouse/warehouse-stock-warehouse" to "Stock de Productos Alm",
        "/pages/warehouse/warehouse-product-warranty" to "Garantías",

        // Compras
        "/pages/purchase/purchase" to "Compras",
        "/pages/purchase/payment-providers" to "Métodos de pago",
        "/pages/purchase/requirement" to "Requerimientos",
        "/pages/purchase/quotes" to "Cotizaciones",
        "/pages/purchase/admin-approved" to "Aprobación Admin",
        "/pages/purchase/approved-management" to "Gestión aprobada",
        "/pages/purchase/import" to "Importaciones",

        // Ventas
        "/pages/sale/sale" to "Ventas",
        "/pages/sale/sales-report-general" to "Reporte General de Ventas",
        "/pages/sale/sales-report-month" to "Reporte Mensual de Ventas",
        "/pages/sale/sales-report-month-general" to "Reporte General Mensual",
        "/pages/sale/order" to "Órdenes",
        "/pages/sale/sales-receivable" to "Cuentas por Cobrar",
        "/pages/sale/proforma" to "Proformas",
        "/pages/sale/prices-list" to "Lista de Precios",
        "/pages/sale/prices-list-detail" to "Detalle de Precios",
        "/pages/sale/remission-guide" to "Guía de Remisión",
        "/pages/sale/orders-receivable" to "Órdenes por Cobrar",
        "/pages/sale/sales-advance" to "Adelanto de Ventas",
        "/pages/sale/sales-calendar" to "Calendario de Ventas",
        "/pages/sale/sales-utility" to "Utilidad de Ventas",

        // Caja
        "/pages/cash/movement" to "Movimientos de Caja",
        "/pages/cash/state-report-cash-day" to "Reporte Diario de Caja",
        "/pages/cash/state-report-cash-month" to "Reporte Mensual de Caja",
        "/pages/cash/state-report-cash-general-day" to "Reporte General Diario",
        "/pages/cash/state-report-cash-general-month" to "Reporte General Mensual",
        "/pages/cash/commissions" to "Comisiones",

        // Reportes
        "/pages/report/utility" to "Utilidades",
        "/pages/report/report-fise" to "Reporte FISE"
    )

    allRoutes.forEach { (route, title) ->
        navGraphBuilder.composable(route) {
            BaseScreenLayout(
                navController = navController,
                title = title,
                onLogout = onLogout
            ) { paddingValues ->
                if (implementedRoutes.containsKey(route)) {
                    // Si la ruta tiene una implementación específica
                    when (route) {
                        "/pages/dashboard" -> DashboardScreen(
                            navController = navController,
                            paddingValues = paddingValues
                        )
                        "/pages/setup/rol" -> RolScreen(
                            navController = navController,
                            paddingValues = paddingValues
                        )
                        "/pages/setup/user" -> UserScreen(
                            navController = navController,
                            paddingValues = paddingValues
                        )
                        else -> DefaultScreen(
                            title = implementedRoutes[route] ?: title,
                            route = route,
                            navController = navController,
                            onLogout = onLogout,
                            paddingValues = paddingValues
                        )
                    }
                } else {
                    // Si la ruta no tiene implementación específica, usar la pantalla por defecto
                    DefaultScreen(
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