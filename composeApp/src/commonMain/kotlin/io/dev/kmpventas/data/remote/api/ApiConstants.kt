package io.dev.kmpventas.data.remote.api

object ApiConstants {
    const val BASE_URL = "http://192.168.100.157:8080"
    const val LOGIN_ENDPOINT = "/auth/login"
    const val REGISTER_ENDPOINT = "register"
    const val DETAILS_ENDPOINT = "details"
    const val MENU_ENDPOINT = "/module/menu"
    const val REFRESH_ENDPOINT = "/auth/refresh"

    object Catalog {
        const val UNIT_MEASUREMENTS = "/unit-measurements"
        const val UNIT_MEASUREMENT_BY_ID = "/unit-measurements/{id}"

        const val CATEGORIES = "/categories"
        const val CATEGORIES_BY_ID = "/categories/{id}"
        const val CATEGORIES_ACTIVE = "/categories/active"
    }
    object Auth {
        const val ROLES = "/role"
        const val ROLE_BY_ID = "/role/{id}"
    }
}
