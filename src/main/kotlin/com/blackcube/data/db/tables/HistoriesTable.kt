package com.blackcube.data.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable

object HistoriesTable : UUIDTable("histories") {
    val tourId = reference("tour_id", ToursTable.id)
    val ordinalNumber = integer("ordinal_number").default(1)
    val title = varchar("title", 255)
    val description = text("description")
    val isCompleted = bool("is_completed")
    val lat = double("lat")
    val lon = double("lon")
}