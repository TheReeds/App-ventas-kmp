package io.dev.kmpventas.presentation.screens.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.dev.kmpventas.presentation.components.AppDrawer
import io.dev.kmpventas.presentation.screens.dashboard.HomeViewModel
import io.dev.kmpventas.presentation.screens.viewmodel.LocalHomeViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreenLayout(
    navController: NavHostController,
    title: String,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val viewModel = LocalHomeViewModel.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(
        initialValue = if (uiState.isDrawerOpen) DrawerValue.Open else DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (uiState.user == null || uiState.menuItems.isEmpty()) {
            viewModel.loadUserAndMenu()
        }
    }

    // Manejo del estado del drawer
    LaunchedEffect(uiState.isDrawerOpen) {
        if (uiState.isDrawerOpen && drawerState.isClosed) {
            drawerState.open()
        } else if (!uiState.isDrawerOpen && drawerState.isOpen) {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
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
                    title = { Text(text = title) },
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
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}