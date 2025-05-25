package com.blackcube.data.db.tables.user_facts

import com.blackcube.data.db.tables.HistoriesTable
import com.blackcube.data.db.tables.UsersTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object UserHistoryProgressTable : Table("user_history_progress") {
    val userId = reference("user_id", UsersTable.id)
    val historyId = reference("history_id", HistoriesTable.id)
    val completedAt = timestamp("completed_at").defaultExpression(CurrentTimestamp)
    override val primaryKey = PrimaryKey(userId, historyId)
}