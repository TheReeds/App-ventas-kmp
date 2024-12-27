package io.dev.kmpventas.presentation.screens.configuration.role

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.dev.kmpventas.data.remote.dto.Role
import io.dev.kmpventas.data.remote.dto.RoleState
import io.dev.kmpventas.domain.repository.RoleRepository
import io.dev.kmpventas.presentation.components.NotificationState
import io.dev.kmpventas.presentation.components.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoleViewModel(
    private val repository: RoleRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RoleState())
    val state = _state.asStateFlow()

    init {
        loadRoles()
    }

    fun loadRoles(page: Int = 0, searchQuery: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                repository.getRoles(page = page, name = searchQuery)
                    .onSuccess { response ->
                        _state.value = _state.value.copy(
                            items = response.content,
                            currentPage = response.currentPage,
                            totalPages = response.totalPages,
                            totalElements = response.totalElements,
                            isLoading = false,
                            error = null
                        )
                    }
                    .onFailure { error ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = error.message,
                            notification = NotificationState(
                                message = error.message ?: "Error al cargar roles",
                                type = NotificationType.ERROR,
                                isVisible = true
                            )
                        )
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message,
                    notification = NotificationState(
                        message = e.message ?: "Error inesperado",
                        type = NotificationType.ERROR,
                        isVisible = true
                    )
                )
            }
        }
    }

    fun createRole(role: Role) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.createRole(role)
                .onSuccess {
                    loadRoles()
                    _state.value = _state.value.copy(
                        isDialogOpen = false,
                        selectedItem = null,
                        notification = NotificationState(
                            message = "Rol creado exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al crear rol",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun updateRole(role: Role) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.updateRole(role)
                .onSuccess {
                    loadRoles()
                    _state.value = _state.value.copy(
                        isDialogOpen = false,
                        selectedItem = null,
                        notification = NotificationState(
                            message = "Rol actualizado exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al actualizar rol",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun deleteRole(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.deleteRole(id)
                .onSuccess {
                    loadRoles()
                    _state.value = _state.value.copy(
                        notification = NotificationState(
                            message = "Rol eliminado exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al eliminar rol",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun setSelectedRole(role: Role?) {
        _state.value = _state.value.copy(
            selectedItem = role,
            isDialogOpen = role != null
        )
    }

    fun closeDialog() {
        _state.value = _state.value.copy(
            isDialogOpen = false,
            selectedItem = null
        )
    }

    fun nextPage() {
        if (_state.value.currentPage + 1 < _state.value.totalPages) {
            loadRoles(_state.value.currentPage + 1)
        }
    }

    fun previousPage() {
        if (_state.value.currentPage > 0) {
            loadRoles(_state.value.currentPage - 1)
        }
    }
}