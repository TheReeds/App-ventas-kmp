package io.dev.kmpventas.data.remote.dto

import io.dev.kmpventas.presentation.components.NotificationState
import kotlinx.serialization.Serializable

@Serializable
data class RoleResponse(
    val totalPages: Int,
    val currentPage: Int,
    val content: List<Role>,
    val totalElements: Int
)

@Serializable
data class Role(
    val id: String,
    val name: String,
    val code: String,
    val description: String,
    val status: Boolean,
    val company: Company? = null,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)

@Serializable
data class Company(
    val id: String,
    val ruc: String,
    val companyName: String,
    val tradeName: String,
    val taxRegime: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
    val calculationIgvByTotal: Boolean,
    val logo: Boolean,
    val userSol: String,
    val passwordSol: String,
    val processType: String,
    val printFormat: String,
    val countryCode: String,
    val withholdingAgent: Boolean,
    val electronicBillingOse: Boolean,
    val codDepSunat: String,
    val codProvSunat: String,
    val codUbigeoSunat: String,
    val createdAt: String,
    val updatedAt: String
)

data class RoleState(
    val items: List<Role> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: Role? = null,
    val isDialogOpen: Boolean = false,
    val notification: NotificationState = NotificationState()
)