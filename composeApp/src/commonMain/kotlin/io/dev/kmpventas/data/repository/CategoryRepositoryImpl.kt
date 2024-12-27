package io.dev.kmpventas.data.repository

import io.dev.kmpventas.data.remote.api.ApiService
import io.dev.kmpventas.data.remote.dto.Category
import io.dev.kmpventas.data.remote.dto.CategoryResponse
import io.dev.kmpventas.domain.repository.CategoryRepository

class CategoryRepositoryImpl(
    private val apiService: ApiService
) : CategoryRepository {

    override suspend fun getCategories(page: Int, size: Int): Result<CategoryResponse> {
        return try {
            Result.success(apiService.getCategories(page, size))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getActiveCategories(): Result<List<Category>> {
        return try {
            Result.success(apiService.getActiveCategories())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategoryById(id: String): Result<Category> {
        return try {
            Result.success(apiService.getCategoryById(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createCategory(category: Category): Result<Category> {
        return try {
            Result.success(apiService.createCategory(category))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(category: Category): Result<Category> {
        return try {
            Result.success(apiService.updateCategory(category.id, category))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(id: String): Result<Unit> {
        return try {
            Result.success(apiService.deleteCategory(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}