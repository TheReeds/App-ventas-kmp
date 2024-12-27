package io.dev.kmpventas.domain.repository

import io.dev.kmpventas.data.remote.dto.Category
import io.dev.kmpventas.data.remote.dto.CategoryResponse

interface CategoryRepository {
    suspend fun getCategories(page: Int = 0, size: Int = 10): Result<CategoryResponse>
    suspend fun getActiveCategories(): Result<List<Category>>
    suspend fun getCategoryById(id: String): Result<Category>
    suspend fun createCategory(category: Category): Result<Category>
    suspend fun updateCategory(category: Category): Result<Category>
    suspend fun deleteCategory(id: String): Result<Unit>
}