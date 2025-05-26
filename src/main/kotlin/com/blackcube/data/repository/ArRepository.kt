package com.blackcube.data.repository

import com.blackcube.data.db.tables.user_facts.UserArScansTable
import com.blackcube.data.utils.parseUuids
import com.blackcube.utils.LoggerUtil
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface ArRepository {
    suspend fun scanArObject(userId: String, arObjectId: String): Boolean
}

class ArRepositoryImpl : ArRepository {
    override suspend fun scanArObject(userId: String, arObjectId: String): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        LoggerUtil.log("Scanning AR with ID: $arObjectId")
        val (userUuid, arObjectUuid) = parseUuids(listOf(userId, arObjectId))
            ?: return@newSuspendedTransaction false

        val insertResult = UserArScansTable.insertIgnore {
            it[UserArScansTable.userId] = userUuid
            it[UserArScansTable.arObjectId] = arObjectUuid
        }
        insertResult.insertedCount > 0
    }
}