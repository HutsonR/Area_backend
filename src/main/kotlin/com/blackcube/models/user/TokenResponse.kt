package com.blackcube.models.user

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(val token: String)