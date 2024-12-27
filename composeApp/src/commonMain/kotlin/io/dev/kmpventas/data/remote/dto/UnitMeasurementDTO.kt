package io.dev.kmpventas.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UnitMeasurementResponse(
    val totalPages: Int,
    val currentPage: Int,
    val content: List<UnitMeasurement>,
    val totalElements: Int
)

@Serializable
data class UnitMeasurement(
    val id: String,
    val name: String,
    val sunatCode: String,
    val symbolPrint: String,
    val description: String,
    val state: Boolean,
    val companyId: String,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)

data class UnitMeasurementState(
    val items: List<UnitMeasurement> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: UnitMeasurement? = null,
    val isDialogOpen: Boolean = false
)