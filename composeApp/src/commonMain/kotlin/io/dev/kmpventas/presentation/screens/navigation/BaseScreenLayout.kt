package io.dev.kmpventas.presentation.screens.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.dev.kmpventas.presentation.components.AppDrawer
import io.dev.kmpventas.presentation.screens.dashboard.HomeViewModel
import io.dev.kmpventas.presentation.screens.viewmodel.LocalHomeViewModel
import io.dev.kmpventas.presentation.theme.AppTheme
import io.dev.kmpventas.presentation.theme.ThemeViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreenLayout(
    navController: NavHostController,
    title: String,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val viewModel = LocalHomeViewModel.current
    val themeViewModel: ThemeViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkMode by themeViewModel.isDarkMode.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(
        initialValue = if (uiState.isDrawerOpen) DrawerValue.Open else DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (uiState.user == null || uiState.menuItems.isEmpty()) {
            viewModel.loadUserAndMenu()
        }
    }

    AppTheme(darkTheme = isDarkMode) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f),
            drawerContent = {
                if (uiState.user != null) {
                    AppDrawer(
                        user = uiState.user!!,
                        menuItems = uiState.menuItems,
                        expandedMenuItems = uiState.expandedMenuItems,
                        onMenuItemExpand = { menuId ->
                            viewModel.toggleMenuItem(menuId)
                        },
                        currentRoute = navController.currentDestination?.route,
                        onNavigate = { route ->
                            scope.launch {
                                drawerState.close()
                                navController.navigate(route)
                            }
                        },
                        onLogout = {
                            scope.launch {
                                drawerState.close()
                                onLogout()
                            }
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        if (drawerState.isClosed) {
                                            drawerState.open()
                                            viewModel.setDrawerOpen(true)
                                        } else {
                                            drawerState.close()
                                            viewModel.setDrawerOpen(false)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        actions = {
                            var rotationState by remember { mutableStateOf(0f) }

                            IconButton(
                                onClick = {
                                    rotationState += 360f
                                    themeViewModel.toggleTheme()
                                },
                                modifier = Modifier
                                    .graphicsLayer(
                                        rotationZ = rotationState,
                                        cameraDistance = 12f
                                    )
                                    .animateContentSize()
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.LightMode
                                    else Icons.Default.DarkMode,
                                    contentDescription = if (isDarkMode) "Cambiar a modo claro"
                                    else "Cambiar a modo oscuro",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.shadow(
                            elevation = 4.dp,
                            spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            ambientColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    )
                },
                containerColor = MaterialTheme.colorScheme.background,
                // Agregamos WindowInsets.navigationBars para manejar la barra de navegación
                contentWindowInsets = WindowInsets.navigationBars
            ) { paddingValues ->
                // Mantenemos todos los paddings originales del Scaffold
                content(paddingValues)
            }
        }
    }

    LaunchedEffect(drawerState.currentValue) {
        viewModel.setDrawerOpen(drawerState.currentValue == DrawerValue.Open)
    }
}