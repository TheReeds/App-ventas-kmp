package io.dev.kmpventas.data.repository

import io.dev.kmpventas.data.remote.api.ApiService
import io.dev.kmpventas.data.remote.dto.Role
import io.dev.kmpventas.data.remote.dto.RoleResponse
import io.dev.kmpventas.domain.repository.RoleRepository

class RoleRepositoryImpl(
    private val apiService: ApiService
) : RoleRepository {

    override suspend fun getRoles(page: Int, size: Int, name: String?): Result<RoleResponse> {
        return try {
            Result.success(apiService.getRoles(page, size, name))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRoleById(id: String): Result<Role> {
        return try {
            Result.success(apiService.getRoleById(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createRole(role: Role): Result<Role> {
        return try {
            Result.success(apiService.createRole(role))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRole(role: Role): Result<Role> {
        return try {
            Result.success(apiService.updateRole(role.id, role))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteRole(id: String): Result<Unit> {
        return try {
            Result.success(apiService.deleteRole(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}