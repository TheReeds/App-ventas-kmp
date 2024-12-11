package io.dev.kmpventas.domain.repository

import io.dev.kmpventas.data.remote.dto.MenuItem
import io.dev.kmpventas.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun getUserDetails(): Result<User>
    suspend fun getMenuItems(): Result<List<MenuItem>>
}