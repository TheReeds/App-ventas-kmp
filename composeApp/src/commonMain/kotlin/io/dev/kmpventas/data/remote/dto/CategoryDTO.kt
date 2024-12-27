package io.dev.kmpventas.data.remote.dto

import io.dev.kmpventas.presentation.components.NotificationState
import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val totalPages: Int,
    val currentPage: Int,
    val content: List<Category>,
    val totalElements: Int
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val description: String,
    val code: String,
    val state: Boolean,
    val companyId: String,
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String?
)

data class CategoryState(
    val items: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val selectedItem: Category? = null,
    val isDialogOpen: Boolean = false,
    val activeCategories: List<Category> = emptyList(),
    val notification: NotificationState = NotificationState()
)