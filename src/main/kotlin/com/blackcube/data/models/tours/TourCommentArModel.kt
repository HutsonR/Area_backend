package com.blackcube.data.models.tours

import kotlinx.serialization.Serializable

@Serializable
data class TourCommentArModel(
    val tourId: String,
    val text: String,
    val lat: Double,
    val lon: Double
)
