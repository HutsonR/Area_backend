package com.blackcube.models.tours

import kotlinx.serialization.Serializable

@Serializable
data class ArObjectModel(
    val id: String,
    val lat: Double,
    val lon: Double,
    val points: Int,
    val isScanned: Boolean
)