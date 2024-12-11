    package io.dev.kmpventas.presentation.screens.dashboard

    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.text.KeyboardOptions
    import androidx.compose.material.*
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.Menu
    import androidx.compose.material3.Button
    import androidx.compose.material3.CircularProgressIndicator
    import androidx.compose.material3.DrawerValue
    import androidx.compose.material3.ExperimentalMaterial3Api
    import androidx.compose.material3.Icon
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.ModalNavigationDrawer
    import androidx.compose.material3.Scaffold
    import androidx.compose.material3.Text
    import androidx.compose.material3.TextField
    import androidx.compose.material3.TopAppBar
    import androidx.compose.material3.rememberDrawerState
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.text.input.KeyboardType
    import androidx.compose.ui.unit.dp
    import androidx.lifecycle.compose.collectAsStateWithLifecycle
    import androidx.navigation.NavHostController
    import io.dev.kmpventas.domain.model.User
    import io.dev.kmpventas.presentation.components.AppDrawer
    import io.dev.kmpventas.presentation.screens.viewmodel.LocalHomeViewModel
    import kotlinx.coroutines.launch
    import org.koin.compose.viewmodel.koinViewModel
    import org.koin.core.annotation.KoinExperimentalAPI

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeScreen(
        navController: NavHostController,
        onLogout: () -> Unit,
    ) {
        val viewModel = LocalHomeViewModel.current
        val drawerState = rememberDrawerState(
            initialValue = if (viewModel.uiState.value.isDrawerOpen) DrawerValue.Open else DrawerValue.Closed
        )
        val scope = rememberCoroutineScope()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        // Observar cambios en la ruta actual
        LaunchedEffect(navController) {
            navController.currentBackStackEntryFlow.collect { entry ->
                entry.destination.route?.let { route ->
                    viewModel.setCurrentScreenTitle(route)
                }
            }
        }

        // Mantener sincronizado el estado del drawer
        LaunchedEffect(drawerState.currentValue) {
            viewModel.setDrawerOpen(drawerState.currentValue == DrawerValue.Open)
        }

        // Cargar datos iniciales
        LaunchedEffect(Unit) {
            viewModel.loadUserAndMenu()
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                uiState.user?.let { user ->
                    AppDrawer(
                        user = user,
                        menuItems = uiState.menuItems,
                        expandedMenuItems = uiState.expandedMenuItems,
                        onMenuItemExpand = { menuId ->
                            viewModel.toggleMenuItem(menuId)
                        },
                        currentRoute = navController.currentDestination?.route,
                        onNavigate = { route ->
                            scope.launch {
                                // Ya no cerramos el drawer automáticamente
                                navController.navigate(route) {
                                    // Pop hasta la ruta home para evitar acumulación en el back stack
                                    popUpTo("home") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
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
                        title = { Text(text = uiState.currentScreenTitle) },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        if (drawerState.isClosed) drawerState.open()
                                        else drawerState.close()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when {
                        uiState.isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        uiState.error != null -> {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = uiState.error!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.loadUserAndMenu() }
                                ) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Screens placeholder
    @Composable
    fun DashboardScreen(
        navController: NavHostController,
        paddingValues: PaddingValues
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Aquí va el contenido específico del Dashboard
            Text("Contenido del Dashboard")
        }
    }

    @Composable
    fun RolScreen(
        navController: NavHostController,
        paddingValues: PaddingValues
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Aquí va el contenido específico del Dashboard
            Text("Contenido del Rol")
        }
    }

    @Composable
    fun UserScreen(
        navController: NavHostController,
        paddingValues: PaddingValues
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Aquí va el contenido específico del Dashboard
            Text("Contenido del User")
        }
    }