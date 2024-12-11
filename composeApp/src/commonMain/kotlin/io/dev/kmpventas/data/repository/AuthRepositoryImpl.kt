package io.dev.kmpventas.data.repository

import io.dev.kmpventas.data.local.SessionManager
import io.dev.kmpventas.data.remote.api.ApiService
import io.dev.kmpventas.data.remote.dto.LoginDTO
import io.dev.kmpventas.data.remote.dto.MenuItem
import io.dev.kmpventas.data.remote.dto.toUser
import io.dev.kmpventas.domain.model.User
import io.dev.kmpventas.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    init {
        // Inicializar el token del ApiService con el token guardado
        sessionManager.getUser()?.let { user ->
            apiService.setAuthToken(user.token)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val loginDTO = LoginDTO(username = email, password = password)
            val loginResponse = apiService.login(loginDTO)
            sessionManager.saveLoginResponse(loginResponse)
            val user = loginResponse.toUser()
            sessionManager.saveUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserDetails(): Result<User> {
        // Retornamos usuario estático
        return Result.success(User(
            id = null,
            email = "jorge.gutierrez@example.com",
            name = "Jorge Gutierrez",
            token = sessionManager.getUser()?.token ?: ""
        ))
    }

    override suspend fun getMenuItems(): Result<List<MenuItem>> {
        return try {
            if (sessionManager.shouldRefreshToken() && sessionManager.isRefreshTokenValid()) {
                try {
                    val refreshResponse = apiService.refreshToken()
                    sessionManager.saveLoginResponse(refreshResponse)
                    apiService.setAuthToken(refreshResponse.access_token)
                } catch (e: Exception) {
                    // Si falla el refresh, continuamos con el token actual
                    println("Failed to refresh token: ${e.message}")
                }
            }
            Result.success(apiService.getMenuItems())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}