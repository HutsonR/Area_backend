package com.blackcube.data.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable

object PlacesTable : UUIDTable("places") {
    val imageUrl = varchar("image_url", 255)
    val title = varchar("title", 255)
    val description = text("description")
    val lat = double("lat")
    val lon = double("lon")
}