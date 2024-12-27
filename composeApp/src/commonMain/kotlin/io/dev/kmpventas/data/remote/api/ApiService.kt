package io.dev.kmpventas.data.remote.api

import io.dev.kmpventas.data.remote.dto.Category
import io.dev.kmpventas.data.remote.dto.CategoryResponse
import io.dev.kmpventas.data.remote.dto.LoginDTO
import io.dev.kmpventas.data.remote.dto.LoginResponse
import io.dev.kmpventas.data.remote.dto.MenuItem
import io.dev.kmpventas.data.remote.dto.MenuResponse
import io.dev.kmpventas.data.remote.dto.RefreshTokenDTO
import io.dev.kmpventas.data.remote.dto.Role
import io.dev.kmpventas.data.remote.dto.RoleResponse
import io.dev.kmpventas.data.remote.dto.UnitMeasurement
import io.dev.kmpventas.data.remote.dto.UnitMeasurementResponse
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





    // UNIT MEASUREMENT

    suspend fun getUnitMeasurements(page: Int = 0, size: Int = 20): UnitMeasurementResponse {
        return client.get(ApiConstants.Catalog.UNIT_MEASUREMENTS) {
            parameter("page", page)
            parameter("size", size)
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun getUnitMeasurementById(id: String): UnitMeasurement {
        return client.get(ApiConstants.Catalog.UNIT_MEASUREMENT_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun createUnitMeasurement(unitMeasurement: UnitMeasurement): UnitMeasurement {
        return client.post(ApiConstants.Catalog.UNIT_MEASUREMENTS) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(unitMeasurement)
        }.body()
    }

    suspend fun updateUnitMeasurement(id: String, unitMeasurement: UnitMeasurement): UnitMeasurement {
        return client.put(ApiConstants.Catalog.UNIT_MEASUREMENT_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(unitMeasurement)
        }.body()
    }

    suspend fun deleteUnitMeasurement(id: String) {
        client.delete(ApiConstants.Catalog.UNIT_MEASUREMENT_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }
    //Category
    suspend fun getCategories(page: Int = 0, size: Int = 10): CategoryResponse {
        return client.get(ApiConstants.Catalog.CATEGORIES) {
            parameter("page", page)
            parameter("size", size)
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun getActiveCategories(): List<Category> {
        return client.get(ApiConstants.Catalog.CATEGORIES_ACTIVE) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun getCategoryById(id: String): Category {
        return client.get(ApiConstants.Catalog.CATEGORIES_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun createCategory(category: Category): Category {
        return client.post(ApiConstants.Catalog.CATEGORIES) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(category)
        }.body()
    }

    suspend fun updateCategory(id: String, category: Category): Category {
        return client.put(ApiConstants.Catalog.CATEGORIES_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(category)
        }.body()
    }

    suspend fun deleteCategory(id: String) {
        client.delete(ApiConstants.Catalog.CATEGORIES_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }

    //ROLES
    suspend fun getRoles(page: Int = 0, size: Int = 20, name: String? = null): RoleResponse {
        return client.get(ApiConstants.Auth.ROLES) {
            parameter("page", page)
            parameter("size", size)
            name?.let { parameter("name", it) }
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun getRoleById(id: String): Role {
        return client.get(ApiConstants.Auth.ROLE_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun createRole(role: Role): Role {
        return client.post(ApiConstants.Auth.ROLES) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(role)
        }.body()
    }

    suspend fun updateRole(id: String, role: Role): Role {
        return client.put(ApiConstants.Auth.ROLE_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(role)
        }.body()
    }

    suspend fun deleteRole(id: String) {
        client.delete(ApiConstants.Auth.ROLE_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }
}