package io.dev.kmpventas.presentation.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.dev.kmpventas.data.remote.dto.UnitMeasurement
import io.dev.kmpventas.data.remote.dto.UnitMeasurementState
import io.dev.kmpventas.domain.repository.CatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UnitMeasurementViewModel(
    private val repository: CatalogRepository
) : ViewModel() {
    private val _state = MutableStateFlow(UnitMeasurementState())
    val state = _state.asStateFlow()

    init {
        loadUnitMeasurements()
    }

    fun loadUnitMeasurements(page: Int = 0) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.getUnitMeasurements(page)
                .onSuccess { response ->
                    _state.value = _state.value.copy(
                        items = response.content,
                        currentPage = response.currentPage,
                        totalPages = response.totalPages,
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
        }
    }

    fun selectItem(item: UnitMeasurement?) {
        _state.value = _state.value.copy(
            selectedItem = item,
            isDialogOpen = true
        )
    }

    fun closeDialog() {
        _state.value = _state.value.copy(
            isDialogOpen = false,
            selectedItem = null
        )
    }

    fun toggleState(item: UnitMeasurement) {
        viewModelScope.launch {
            repository.updateUnitMeasurement(item.copy(state = !item.state))
                .onSuccess {
                    loadUnitMeasurements(_state.value.currentPage)
                }
        }
    }
}