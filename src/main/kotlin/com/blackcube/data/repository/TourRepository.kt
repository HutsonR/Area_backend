package com.blackcube.data.repository

import com.blackcube.data.db.tables.ArObjectsTable
import com.blackcube.data.db.tables.HistoriesTable
import com.blackcube.data.db.tables.ToursTable
import com.blackcube.data.db.tables.user_facts.UserArScansTable
import com.blackcube.data.db.tables.user_facts.UserHistoryProgressTable
import com.blackcube.data.db.tables.user_facts.UserToursTable
import com.blackcube.data.utils.parseUuid
import com.blackcube.data.utils.parseUuids
import com.blackcube.models.tours.ArObjectModel
import com.blackcube.models.tours.HistoryModel
import com.blackcube.models.tours.TourModel
import com.blackcube.utils.LoggerUtil
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

interface TourRepository {
    suspend fun getAllTours(userId: String, limit: Int? = null): List<TourModel>
    suspend fun getTourById(userId: String, tourId: String): TourModel?
    suspend fun startTour(userId: String, tourId: String): Boolean
    suspend fun finishTour(userId: String, tourId: String): Boolean
}

class TourRepositoryImpl : TourRepository {

    override suspend fun getAllTours(userId: String, limit: Int?): List<TourModel> = newSuspendedTransaction(Dispatchers.IO) {
        LoggerUtil.log("Getting all tours with userId: $userId and limit: $limit")
        val userUuid = parseUuid(userId)

        // 2) Готовим JOIN между ToursTable и UserToursTable
        val toursWithUser = ToursTable.join(
            UserToursTable,
            joinType = JoinType.LEFT,
            additionalConstraint = {
                (ToursTable.id eq UserToursTable.tourId) and
                        (UserToursTable.userId eq userUuid)
            }
        )

        // 3) Формируем запрос: выбираем конкретные колонки через select(...)
        var tourQuery: Query = toursWithUser.select(
            ToursTable.id,
            ToursTable.imageUrl,
            ToursTable.title,
            ToursTable.description,
            ToursTable.duration,
            ToursTable.distance,
            ToursTable.isAR,
            UserToursTable.startedAt,
            UserToursTable.finishedAt
        )
        // 4) Ограничиваем число строк, если задано
        if (limit != null) {
            tourQuery = tourQuery.limit(count = limit).offset(start = 0)
        }

        // 5) Выполняем запрос и собираем результаты
        val tourRows = tourQuery.toList()
        val tourIds = tourRows.map { it[ToursTable.id] }

        // 6) Загружаем истории и прогресс пользователя
        val rawHistories = HistoriesTable
            .join(
                UserHistoryProgressTable,
                joinType = JoinType.LEFT,
                additionalConstraint = {
                    (HistoriesTable.id eq UserHistoryProgressTable.historyId) and
                            (UserHistoryProgressTable.userId eq userUuid)
                }
            )
            .selectAll()
            .where { HistoriesTable.tourId inList tourIds }
            .orderBy(HistoriesTable.ordinalNumber to SortOrder.ASC)
            .map { row ->
                row[HistoriesTable.tourId] to HistoryModel(
                    id = row[HistoriesTable.id].toString(),
                    ordinalNumber = row[HistoriesTable.ordinalNumber],
                    title = row[HistoriesTable.title],
                    description = row[HistoriesTable.description],
                    isCompleted = row.getOrNull(UserHistoryProgressTable.historyId) != null,
                    lat = row[HistoriesTable.lat],
                    lon = row[HistoriesTable.lon]
                )
            }
        val historiesByTour = rawHistories.groupBy({ it.first }, { it.second })

        // 7) Загружаем AR-объекты и сканы пользователя
        val rawArObjects = ArObjectsTable
            .join(
                UserArScansTable,
                joinType = JoinType.LEFT,
                additionalConstraint = {
                    (ArObjectsTable.id eq UserArScansTable.arObjectId) and
                            (UserArScansTable.userId eq userUuid)
                }
            )
            .selectAll()
            .where { ArObjectsTable.tourId inList tourIds }
            .map { row ->
                row[ArObjectsTable.tourId] to ArObjectModel(
                    id = row[ArObjectsTable.id].toString(),
                    lat = row[ArObjectsTable.lat],
                    lon = row[ArObjectsTable.lon],
                    text = row[ArObjectsTable.text],
                    isScanned = row.getOrNull(UserArScansTable.arObjectId) != null
                )
            }
        val arByTour = rawArObjects.groupBy({ it.first }, { it.second })

        tourRows.map { row ->
            val tId = row[ToursTable.id]
            val startedAt = row.getOrNull(UserToursTable.startedAt)
            val finishedAt = row.getOrNull(UserToursTable.finishedAt)

            TourModel(
                id = tId.toString(),
                imageUrl = row[ToursTable.imageUrl],
                title = row[ToursTable.title],
                description = row[ToursTable.description],
                duration = row[ToursTable.duration],
                distance = row[ToursTable.distance],
                isStarted = startedAt != null,
                isCompleted = finishedAt != null,
                isAR = row[ToursTable.isAR],
                histories = historiesByTour[tId].orEmpty(),
                arObjects = arByTour[tId].orEmpty()
            )
        }
    }

    override suspend fun getTourById(userId: String, tourId: String): TourModel? = newSuspendedTransaction(Dispatchers.IO) {
        LoggerUtil.log("Fetching tour for user $userId with tour id: $tourId")
        getAllTours(userId).find { it.id == tourId }
    }

    override suspend fun startTour(userId: String, tourId: String): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        LoggerUtil.log("Starting tour for user $userId with tour id: $tourId")
        val (userUuid, tourUuid) = parseUuids(listOf(userId, tourId))
            ?: return@newSuspendedTransaction false

        val insertResult = UserToursTable.insertIgnore {
            it[UserToursTable.userId] = userUuid
            it[UserToursTable.tourId] = tourUuid
        }
        insertResult.insertedCount > 0
    }

    override suspend fun finishTour(userId: String, tourId: String): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        LoggerUtil.log("Finishing tour for user $userId with tour id: $tourId")
        val (userUuid, tourUuid) = parseUuids(listOf(userId, tourId))
            ?: return@newSuspendedTransaction false

        val updateResult = UserToursTable.update({
            (UserToursTable.userId eq userUuid) and (UserToursTable.tourId eq tourUuid)
        }) {
            it[finishedAt] = org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
        }
        updateResult > 0
    }
}
