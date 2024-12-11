package io.dev.kmpventas.domain.usecase

import io.dev.kmpventas.domain.model.User
import io.dev.kmpventas.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return repository.login(email, password)
    }
}