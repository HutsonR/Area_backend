package com.blackcube.data.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable

object ArObjectsTable : UUIDTable("ar_objects") {
    val tourId = reference("tour_id", ToursTable.id)
    val lat = double("lat")
    val lon = double("lon")
}