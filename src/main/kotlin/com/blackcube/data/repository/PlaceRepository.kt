package com.blackcube.data.repository

import com.blackcube.data.db.tables.PlacesTable
import com.blackcube.data.utils.parseUuid
import com.blackcube.models.places.PlaceModel
import com.blackcube.utils.LoggerUtil
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface PlaceRepository {
    suspend fun getPlaces(limit: Int? = null): List<PlaceModel>
    suspend fun getPlaceById(id: String): PlaceModel?
}

class PlaceRepositoryImpl : PlaceRepository {

    override suspend fun getPlaces(limit: Int?): List<PlaceModel> = newSuspendedTransaction(Dispatchers.IO) {
        LoggerUtil.log("Getting places with limit: $limit")
        val query = if (limit != null) {
            PlacesTable.selectAll().limit(limit)
        } else {
            PlacesTable.selectAll()
        }

        query.map { placeRow ->
            PlaceModel(
                id = placeRow[PlacesTable.id].toString(),
                imageUrl = placeRow[PlacesTable.imageUrl],
                title = placeRow[PlacesTable.title],
                description = placeRow[PlacesTable.description],
                lat = placeRow[PlacesTable.lat],
                lon = placeRow[PlacesTable.lon]
            )
        }
    }

    override suspend fun getPlaceById(id: String): PlaceModel? = newSuspendedTransaction(Dispatchers.IO) {
        LoggerUtil.log("Getting place with id: $id")
        val placeUuid = parseUuid(id) ?: return@newSuspendedTransaction null

        PlacesTable
            .selectAll()
            .where { PlacesTable.id eq placeUuid }
            .singleOrNull()
            ?.let { row ->
                PlaceModel(
                    id = row[PlacesTable.id].toString(),
                    imageUrl = row[PlacesTable.imageUrl],
                    title = row[PlacesTable.title],
                    description = row[PlacesTable.description],
                    lat = row[PlacesTable.lat],
                    lon = row[PlacesTable.lon]
                )
            }
    }

}
