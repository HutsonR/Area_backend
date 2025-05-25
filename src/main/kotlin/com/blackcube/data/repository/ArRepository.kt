package com.blackcube.data.repository

import com.blackcube.data.db.tables.user_facts.UserArScansTable
import com.blackcube.data.utils.parseUuids
import kotlinx.coroutines.Dispatchers
import mu.KotlinLogging
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

private val logger = KotlinLogging.logger {}

interface ArRepository {
    suspend fun scanArObject(userId: String, arObjectId: String): Boolean
}

class ArRepositoryImpl : ArRepository {
    override suspend fun scanArObject(userId: String, arObjectId: String): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        logger.info { "Scanning AR with ID: $arObjectId" }
        val (userUuid, arObjectUuid) = parseUuids(listOf(userId, arObjectId))
            ?: return@newSuspendedTransaction false

        val insertResult = UserArScansTable.insertIgnore {
            it[UserArScansTable.userId] = userUuid
            it[UserArScansTable.arObjectId] = arObjectUuid
        }
        insertResult.insertedCount > 0
    }
}