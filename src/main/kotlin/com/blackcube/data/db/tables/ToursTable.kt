package com.blackcube.data.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable

object ToursTable : UUIDTable("tours") {
    val imageUrl = varchar("image_url", 255)
    val title = varchar("title", 255)
    val description = text("description")
    val duration = varchar("duration", 50)
    val distance = varchar("distance", 50)
    val isAR = bool("isAR")
}