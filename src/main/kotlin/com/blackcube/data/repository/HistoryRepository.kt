package com.blackcube.data.repository

import com.blackcube.data.db.tables.user_facts.UserHistoryProgressTable
import com.blackcube.data.utils.parseUuids
import com.blackcube.utils.LoggerUtil
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface HistoryRepository {
    suspend fun completeHistory(userId: String, historyId: String): Boolean
}

class HistoryRepositoryImpl : HistoryRepository {
    override suspend fun completeHistory(userId: String, historyId: String) = newSuspendedTransaction(Dispatchers.IO) {
        LoggerUtil.log("Completing history with historyId: $historyId")
        val (userUuid, historyUuid) = parseUuids(listOf(userId, historyId))
            ?: return@newSuspendedTransaction false

        val insertResult = UserHistoryProgressTable.insertIgnore {
            it[UserHistoryProgressTable.userId] = userUuid
            it[UserHistoryProgressTable.historyId] = historyUuid
        }
        insertResult.insertedCount > 0
    }
}
