package com.blackcube.models.user

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val id: String,
    val email: String,
    val name: String,
    val passwordHash: String,
    val createdAt: Instant
)