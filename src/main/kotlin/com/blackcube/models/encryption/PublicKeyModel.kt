package com.blackcube.models.encryption

import kotlinx.serialization.Serializable

/**
 * Модель для передачи публичного ключа клиенту в Base64
 */
@Serializable
data class PublicKeyModel(
    val publicKeyBase64: String
)
