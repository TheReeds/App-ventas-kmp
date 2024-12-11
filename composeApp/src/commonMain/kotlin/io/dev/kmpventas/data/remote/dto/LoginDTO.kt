package io.dev.kmpventas.data.remote.dto

import io.dev.kmpventas.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class LoginDTO(
    val username: String,
    val password: String,
    val rememberMe: String = ""
)

@Serializable
data class LoginResponse(
    val access_token: String,
    val expires_in: Int,
    val refresh_expires_in: Int,
    val refresh_token: String,
    val token_type: String,
    val session_state: String,
    val scope: String
)

@Serializable
data class RefreshTokenDTO(
    val refreshToken: String
)

@Serializable
data class MenuItem(
    val id: String,
    val title: String,
    val subtitle: String?,
    val type: String,
    val icon: String,
    val status: Boolean,
    val moduleOrder: Int,
    val link: String,
    val parentModuleId: String?,
    val children: List<MenuItem>? = null
)

// Actualización de la respuesta del menú
@Serializable
data class MenuResponse(
    val data: List<MenuItem>
)
fun LoginResponse.toUser(): User {
    return User(
        id = null,
        email = "jorge.gutierrez@example.com", // Datos estáticos solo para el email
        name = "Jorge Gutierrez", // Datos estáticos solo para el nombre
        token = this.access_token
    )
}