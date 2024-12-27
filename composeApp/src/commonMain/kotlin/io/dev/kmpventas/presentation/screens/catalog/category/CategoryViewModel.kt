package io.dev.kmpventas.presentation.screens.catalog.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.dev.kmpventas.data.remote.dto.Category
import io.dev.kmpventas.data.remote.dto.CategoryState
import io.dev.kmpventas.domain.repository.CategoryRepository
import io.dev.kmpventas.presentation.components.NotificationState
import io.dev.kmpventas.presentation.components.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CategoryState())
    val state = _state.asStateFlow()

    init {
        loadCategories()
        loadActiveCategories()
    }

    fun loadCategories(page: Int = 0) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                repository.getCategories(page = page)
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
                            error = error.message
                        )
                    }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun loadActiveCategories() {
        viewModelScope.launch {
            repository.getActiveCategories()
                .onSuccess { categories ->
                    _state.value = _state.value.copy(
                        activeCategories = categories
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = error.message
                    )
                }
        }
    }

    fun createCategory(category: Category) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.createCategory(category)
                .onSuccess {
                    loadCategories()
                    loadActiveCategories()
                    _state.value = _state.value.copy(
                        isDialogOpen = false,
                        selectedItem = null,
                        notification = NotificationState(
                            message = "Categoría creada exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al crear la categoría",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.updateCategory(category)
                .onSuccess {
                    loadCategories()
                    loadActiveCategories()
                    _state.value = _state.value.copy(
                        isDialogOpen = false,
                        selectedItem = null,
                        notification = NotificationState(
                            message = "Categoría actualizada exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al actualizar la categoría",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.deleteCategory(id)
                .onSuccess {
                    loadCategories()
                    loadActiveCategories()
                    _state.value = _state.value.copy(
                        notification = NotificationState(
                            message = "Categoría eliminada exitosamente",
                            type = NotificationType.SUCCESS,
                            isVisible = true
                        )
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notification = NotificationState(
                            message = error.message ?: "Error al eliminar la categoría",
                            type = NotificationType.ERROR,
                            isVisible = true
                        )
                    )
                }
        }
    }

    fun setSelectedCategory(category: Category?) {
        _state.value = _state.value.copy(
            selectedItem = category,
            isDialogOpen = category != null
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
            loadCategories(_state.value.currentPage + 1)
        }
    }

    fun previousPage() {
        if (_state.value.currentPage > 0) {
            loadCategories(_state.value.currentPage - 1)
        }
    }
}