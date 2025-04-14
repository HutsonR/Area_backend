package com.blackcube.data.repository

import com.blackcube.data.db.tables.HistoriesTable
import com.blackcube.data.db.tables.ToursTable
import com.blackcube.models.tours.HistoryModel
import com.blackcube.models.tours.TourModel
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

private val logger = KotlinLogging.logger {}

interface TourRepository {
    suspend fun getTours(limit: Int? = null): List<TourModel>
    suspend fun getTourById(id: String): TourModel?
    suspend fun updateTour(id: String, tour: TourModel): Boolean
}

class TourRepositoryImpl : TourRepository {

    override suspend fun getTours(limit: Int?): List<TourModel> = newSuspendedTransaction {
        val toursQuery = if (limit != null) {
            ToursTable.selectAll().limit(limit)
        } else {
            ToursTable.selectAll()
        }

        toursQuery.map { tourRow ->
            val tourUUID = tourRow[ToursTable.id]
            val histories = HistoriesTable
                .selectAll()
                .where { HistoriesTable.tourId eq tourUUID }
                .orderBy(HistoriesTable.ordinalNumber to SortOrder.ASC)
                .map { histRow ->
                    HistoryModel(
                        id = histRow[HistoriesTable.id].toString(),
                        ordinalNumber = histRow[HistoriesTable.ordinalNumber],
                        title = histRow[HistoriesTable.title],
                        description = histRow[HistoriesTable.description],
                        isCompleted = histRow[HistoriesTable.isCompleted],
                        lat = histRow[HistoriesTable.lat],
                        lon = histRow[HistoriesTable.lon]
                    )
                }

            TourModel(
                id = tourUUID.toString(),
                imageUrl = tourRow[ToursTable.imageUrl],
                title = tourRow[ToursTable.title],
                description = tourRow[ToursTable.description],
                duration = tourRow[ToursTable.duration],
                distance = tourRow[ToursTable.distance],
                isCompleted = tourRow[ToursTable.isCompleted],
                isStarted = tourRow[ToursTable.isStarted],
                isAR = tourRow[ToursTable.isAR],
                histories = histories
            )
        }
    }

    override suspend fun getTourById(id: String): TourModel? = newSuspendedTransaction {
        val tourUUID = try {
            UUID.fromString(id)
        } catch (e: Exception) {
            logger.error(e) { "Invalid UUID format for id: $id" }
            return@newSuspendedTransaction null
        }
        logger.info { "Fetching tour with id: $tourUUID" }
        val tourRow = ToursTable
            .selectAll()
            .where { ToursTable.id eq tourUUID }
            .singleOrNull()
        if (tourRow == null) {
            logger.info { "Tour with id $tourUUID not found" }
            return@newSuspendedTransaction null
        }
        val histories = HistoriesTable
            .selectAll()
            .where { HistoriesTable.tourId eq tourUUID }
            .orderBy(HistoriesTable.ordinalNumber to SortOrder.ASC)
            .map { histRow ->
                HistoryModel(
                    id = histRow[HistoriesTable.id].toString(),
                    ordinalNumber = histRow[HistoriesTable.ordinalNumber],
                    title = histRow[HistoriesTable.title],
                    description = histRow[HistoriesTable.description],
                    isCompleted = histRow[HistoriesTable.isCompleted],
                    lat = histRow[HistoriesTable.lat],
                    lon = histRow[HistoriesTable.lon]
                )
            }
        TourModel(
            id = tourUUID.toString(),
            imageUrl = tourRow[ToursTable.imageUrl],
            title = tourRow[ToursTable.title],
            description = tourRow[ToursTable.description],
            duration = tourRow[ToursTable.duration],
            distance = tourRow[ToursTable.distance],
            isCompleted = tourRow[ToursTable.isCompleted],
            isStarted = tourRow[ToursTable.isStarted],
            isAR = tourRow[ToursTable.isAR],
            histories = histories
        )
    }

    override suspend fun updateTour(id: String, tour: TourModel): Boolean = newSuspendedTransaction {
        val tourUUID = try {
            UUID.fromString(id)
        } catch (e: Exception) {
            logger.error(e) { "Invalid UUID format for update, id: $id" }
            return@newSuspendedTransaction false
        }
        logger.info { "Updating tour with id: $tourUUID" }
        val updatedCount = ToursTable.update({ ToursTable.id eq tourUUID }) {
            it[imageUrl] = tour.imageUrl
            it[title] = tour.title
            it[description] = tour.description
            it[duration] = tour.duration
            it[distance] = tour.distance
            it[isCompleted] = tour.isCompleted
            it[isStarted] = tour.isStarted
            it[isAR] = tour.isAR
        }

        if (updatedCount <= 0) {
            logger.info { "Tour not found for id: $tourUUID" }
            return@newSuspendedTransaction false
        }

        val existingHistoryIds = HistoriesTable
            .selectAll()
            .where { HistoriesTable.tourId eq tourUUID }
            .map { it[HistoriesTable.id].toString() }
            .toSet()

        tour.histories.forEach { history ->
            val historyId = try {
                UUID.fromString(history.id)
            } catch (e: Exception) {
                logger.error(e) { "Invalid UUID format for update, historyId: ${history.id}" }
                return@newSuspendedTransaction false
            }
            if (existingHistoryIds.contains(historyId.toString())) {
                HistoriesTable.update({ HistoriesTable.id eq historyId }) {
                    it[title] = history.title
                    it[description] = history.description
                    it[isCompleted] = history.isCompleted
                    it[lat] = history.lat
                    it[lon] = history.lon
                }
            }
        }

        logger.info { "Updated tour and its histories for id $tourUUID" }
        true
    }
}
