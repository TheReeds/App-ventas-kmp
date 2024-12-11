package io.dev.kmpventas.data.local

import com.russhwolf.settings.Settings
import io.dev.kmpventas.data.remote.dto.LoginResponse
import io.dev.kmpventas.domain.model.User
import kotlinx.datetime.Clock

class SessionManager(private val settings: Settings) {
    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
        private const val KEY_REFRESH_TOKEN_EXPIRY = "refresh_token_expiry"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_FIRST_RUN = "first_run"
    }

    fun saveLoginResponse(loginResponse: LoginResponse) {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        settings.putString(KEY_ACCESS_TOKEN, loginResponse.access_token)
        settings.putString(KEY_REFRESH_TOKEN, loginResponse.refresh_token)
        settings.putLong(KEY_TOKEN_EXPIRY, currentTime + (loginResponse.expires_in * 1000))
        settings.putLong(KEY_REFRESH_TOKEN_EXPIRY, currentTime + (loginResponse.refresh_expires_in * 1000))
    }

    fun saveUser(user: User) {
        settings.putString(KEY_USER_EMAIL, user.email)
        settings.putString(KEY_USER_NAME, user.name)
        settings.putString(KEY_ACCESS_TOKEN, user.token)
        settings.putBoolean(KEY_IS_LOGGED_IN, true)
    }

    fun getUser(): User? {
        if (!isLoggedIn()) return null

        val email = settings.getString(KEY_USER_EMAIL, "")
        val name = settings.getString(KEY_USER_NAME, "")
        val token = settings.getString(KEY_ACCESS_TOKEN, "")

        if (email.isEmpty() || name.isEmpty() || token.isEmpty()) {
            return null
        }

        return User(
            id = null,
            email = email,
            name = name,
            token = token
        )
    }

    fun clearSession() {
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_USER_EMAIL)
        settings.remove(KEY_USER_NAME)
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
        settings.remove(KEY_TOKEN_EXPIRY)
        settings.remove(KEY_REFRESH_TOKEN_EXPIRY)
        settings.putBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getRefreshToken(): String? = settings.getStringOrNull(KEY_REFRESH_TOKEN)
    fun getTokenExpiry(): Long = settings.getLong(KEY_TOKEN_EXPIRY, 0)
    fun getRefreshTokenExpiry(): Long = settings.getLong(KEY_REFRESH_TOKEN_EXPIRY, 0)

    fun shouldRefreshToken(): Boolean {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val tokenExpiry = settings.getLong(KEY_TOKEN_EXPIRY, 0)
        // Refrescar si quedan menos de 30 segundos de validez
        return tokenExpiry - currentTime < 30000
    }

    fun isRefreshTokenValid(): Boolean {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val refreshTokenExpiry = settings.getLong(KEY_REFRESH_TOKEN_EXPIRY, 0)
        return currentTime < refreshTokenExpiry
    }

    fun isLoggedIn(): Boolean = settings.getBoolean(KEY_IS_LOGGED_IN, false)
    fun isFirstRun(): Boolean = settings.getBoolean(KEY_FIRST_RUN, true)
    fun setFirstRunComplete() = settings.putBoolean(KEY_FIRST_RUN, false)
}