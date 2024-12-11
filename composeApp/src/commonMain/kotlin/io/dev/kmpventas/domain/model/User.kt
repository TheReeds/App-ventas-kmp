package io.dev.kmpventas.domain.model

data class User(
    val id: Int?,
    val email: String,
    val name: String,
    val token: String
)