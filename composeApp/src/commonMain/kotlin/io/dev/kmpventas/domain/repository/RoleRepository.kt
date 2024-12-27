package io.dev.kmpventas.domain.repository

import io.dev.kmpventas.data.remote.dto.Role
import io.dev.kmpventas.data.remote.dto.RoleResponse

interface RoleRepository {
    suspend fun getRoles(page: Int = 0, size: Int = 20, name: String? = null): Result<RoleResponse>
    suspend fun getRoleById(id: String): Result<Role>
    suspend fun createRole(role: Role): Result<Role>
    suspend fun updateRole(role: Role): Result<Role>
    suspend fun deleteRole(id: String): Result<Unit>
}