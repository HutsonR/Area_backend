package com.blackcube.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(val token: String)