package io.dev.kmpventas.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.dev.kmpventas.data.local.SessionManager
import io.dev.kmpventas.data.remote.dto.MenuItem
import io.dev.kmpventas.domain.model.User
import io.dev.kmpventas.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val user: User? = null,
    val menuItems: List<MenuItem> = emptyList(),
    val currentScreenTitle: String = "Home",
    val isLoading: Boolean = false,
    val error: String? = null,
    val expandedMenuItems: Set<String> = emptySet(),
    val isDrawerOpen: Boolean = false
)

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        initializeData()
    }

    fun initializeData() {
        viewModelScope.launch {
            println("DEBUG: Initializing data")
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Recuperar usuario guardado
                val savedUser = sessionManager.getUser()
                if (savedUser != null) {
                    println("DEBUG: Found saved user: ${savedUser.name}")

                    // Establecer el usuario inmediatamente
                    _uiState.value = _uiState.value.copy(user = savedUser)

                    // Cargar el menú inmediatamente al encontrar usuario guardado
                    authRepository.getMenuItems()
                        .onSuccess { menuItems ->
                            println("DEBUG: Menu items loaded successfully: ${menuItems.size} items")
                            _uiState.value = _uiState.value.copy(
                                menuItems = menuItems,
                                isLoading = false
                            )
                        }
                        .onFailure { error ->
                            println("DEBUG: Failed to load menu items, trying to refresh token")
                            // Si falla la carga del menú, intentamos refrescar el token
                            refreshTokenAndLoadMenu()
                        }

                    // Intentar refrescar los datos del usuario en segundo plano
                    authRepository.getUserDetails()
                        .onSuccess { updatedUser ->
                            println("DEBUG: User details refreshed")
                            _uiState.value = _uiState.value.copy(user = updatedUser)
                            sessionManager.saveUser(updatedUser)
                        }
                        .onFailure { error ->
                            println("DEBUG: Error refreshing user details: ${error.message}")
                        }
                } else {
                    println("DEBUG: No saved user found")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                println("DEBUG: Error in initialization: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de inicialización: ${e.message}"
                )
            }
        }
    }


    private suspend fun loadMenuItems() {
        try {
            authRepository.getMenuItems()
                .onSuccess { menuItems ->
                    _uiState.value = _uiState.value.copy(
                        menuItems = menuItems,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        error = "Error al cargar menú: ${error.message}",
                        isLoading = false
                    )
                }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Error inesperado al cargar menú: ${e.message}",
                isLoading = false
            )
        }
    }

    private suspend fun retryLoadMenuWithRefresh() {
        // Intentar refrescar el token mediante una nueva llamada a getUserDetails
        authRepository.getUserDetails()
            .onSuccess { user ->
                // Si se actualizó el usuario correctamente, intentar cargar el menú nuevamente
                authRepository.getMenuItems()
                    .onSuccess { menuItems ->
                        _uiState.value = _uiState.value.copy(
                            menuItems = menuItems,
                            isLoading = false,
                            error = null
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error al cargar menú: ${error.message}"
                        )
                    }
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al refrescar sesión: ${error.message}"
                )
            }
    }
    fun logout() {
        viewModelScope.launch {
            println("DEBUG: Performing logout")
            try {
                _uiState.value = HomeUiState()
                sessionManager.clearSession()
            } catch (e: Exception) {
                println("DEBUG: Error during logout: ${e.message}")
                sessionManager.clearSession()
            }
        }
    }

    fun loadUserAndMenu() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Primero intentar obtener el usuario guardado
                val savedUser = sessionManager.getUser()
                if (savedUser != null) {
                    _uiState.value = _uiState.value.copy(user = savedUser)

                    // Cargar el menú inmediatamente
                    loadMenuItems()

                    // Actualizar usuario en segundo plano
                    authRepository.getUserDetails()
                        .onSuccess { user ->
                            _uiState.value = _uiState.value.copy(user = user)
                            sessionManager.saveUser(user)
                        }
                } else {
                    // Si no hay usuario guardado, intentar obtener los detalles
                    authRepository.getUserDetails()
                        .onSuccess { user ->
                            _uiState.value = _uiState.value.copy(user = user)
                            sessionManager.saveUser(user)
                            loadMenuItems()
                        }
                        .onFailure { error ->
                            _uiState.value = _uiState.value.copy(
                                error = "Error al cargar usuario: ${error.message}",
                                isLoading = false
                            )
                        }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error inesperado: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    fun toggleMenuItem(menuId: String) {
        val currentExpanded = _uiState.value.expandedMenuItems
        _uiState.value = if (currentExpanded.contains(menuId)) {
            _uiState.value.copy(expandedMenuItems = currentExpanded - menuId)
        } else {
            _uiState.value.copy(expandedMenuItems = currentExpanded + menuId)
        }
    }

    fun setDrawerOpen(isOpen: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDrawerOpen = isOpen)

            // Si el drawer se abre y no hay menú items, intentar cargar
            if (isOpen && _uiState.value.menuItems.isEmpty()) {
                loadMenuItems()
            }
        }
    }

    fun updateCurrentScreen(title: String) {
        _uiState.value = _uiState.value.copy(currentScreenTitle = title)
    }

    fun getCurrentScreenTitle(): String {
        return _uiState.value.currentScreenTitle
    }

    fun setCurrentScreenTitle(route: String) {
        val title = _uiState.value.menuItems
            .flatMap { menuItem ->
                listOfNotNull(menuItem) + (menuItem.children ?: emptyList())
            }
            .find { it.link == route }
            ?.title ?: "Home"

        updateCurrentScreen(title)
    }
    private suspend fun refreshTokenAndLoadMenu() {
        println("DEBUG: Attempting to refresh token and reload menu")
        authRepository.getUserDetails()
            .onSuccess { user ->
                println("DEBUG: Token refreshed successfully")
                _uiState.value = _uiState.value.copy(user = user)
                sessionManager.saveUser(user)

                // Intentar cargar el menú nuevamente con el token actualizado
                authRepository.getMenuItems()
                    .onSuccess { menuItems ->
                        println("DEBUG: Menu items loaded after token refresh")
                        _uiState.value = _uiState.value.copy(
                            menuItems = menuItems,
                            isLoading = false,
                            error = null
                        )
                    }
                    .onFailure { error ->
                        println("DEBUG: Failed to load menu items after token refresh: ${error.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error al cargar menú: ${error.message}"
                        )
                    }
            }
            .onFailure { error ->
                println("DEBUG: Failed to refresh token: ${error.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al refrescar sesión: ${error.message}"
                )
            }
    }

}