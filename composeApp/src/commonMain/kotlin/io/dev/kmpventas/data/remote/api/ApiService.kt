package io.dev.kmpventas.data.remote.api

import io.dev.kmpventas.data.remote.dto.LoginDTO
import io.dev.kmpventas.data.remote.dto.LoginResponse
import io.dev.kmpventas.data.remote.dto.MenuItem
import io.dev.kmpventas.data.remote.dto.MenuResponse
import io.dev.kmpventas.data.remote.dto.RefreshTokenDTO
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.sync.Mutex


class ApiService(private val client: HttpClient) {
    private var _authToken: String? = null
    val authToken: String? get() = _authToken
    private var _refreshToken: String? = null

    private val refreshMutex = Mutex()

    suspend fun login(loginDTO: LoginDTO): LoginResponse {
        val response = client.post(ApiConstants.LOGIN_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(loginDTO)
        }
        val loginResponse = response.body<LoginResponse>()
        _authToken = loginResponse.access_token
        _refreshToken = loginResponse.refresh_token
        return loginResponse
    }

    suspend fun refreshToken(): LoginResponse {
        return _refreshToken?.let { refreshToken ->
            val response = client.post(ApiConstants.REFRESH_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenDTO(refreshToken))
            }
            val loginResponse = response.body<LoginResponse>()
            _authToken = loginResponse.access_token
            _refreshToken = loginResponse.refresh_token
            loginResponse
        } ?: throw IllegalStateException("No refresh token available")
    }


    /*suspend fun getUserDetails(): UserDetailsResponse {
        return client.get(ApiConstants.DETAILS_ENDPOINT) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }*/

    suspend fun getMenuItems(): List<MenuItem> {
        return client.get(ApiConstants.MENU_ENDPOINT) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            } ?: throw IllegalStateException("No auth token available")
        }.body()
    }
    fun setAuthToken(token: String?) {
        _authToken = token
    }
    fun setRefreshToken(token: String?) {
        _refreshToken = token
    }
}