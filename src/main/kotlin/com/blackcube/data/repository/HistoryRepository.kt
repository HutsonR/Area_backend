package com.blackcube.data.repository

import com.blackcube.data.db.tables.user_facts.UserHistoryProgressTable
import kotlinx.coroutines.Dispatchers
import mu.KotlinLogging
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

private val logger = KotlinLogging.logger {}

interface HistoryRepository {
    suspend fun completeHistory(userId: String, historyId: String): Boolean
}

class HistoryRepositoryImpl : HistoryRepository {
    override suspend fun completeHistory(userId: String, historyId: String) = newSuspendedTransaction(Dispatchers.IO) {
        logger.info { "Completing history with historyId: $historyId" }
        val userUUID = try {
            UUID.fromString(userId)
        } catch (e: Exception) {
            logger.error(e) { "Invalid UUID format for id: $userId" }
            return@newSuspendedTransaction false
        }
        val historyUUID = try {
            UUID.fromString(historyId)
        } catch (e: Exception) {
            logger.error(e) { "Invalid UUID format for id: $historyId" }
            return@newSuspendedTransaction false
        }
        val insertResult = UserHistoryProgressTable.insertIgnore {
            it[UserHistoryProgressTable.userId] = userUUID
            it[UserHistoryProgressTable.historyId] = historyUUID
        }
        insertResult.insertedCount > 0
    }
}
