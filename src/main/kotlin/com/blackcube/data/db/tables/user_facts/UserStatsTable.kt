package com.blackcube.data.db.tables.user_facts

import com.blackcube.data.db.tables.UsersTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserStatsTable : Table("user_stats") {
    val userId = reference("user_id", UsersTable.id)
    val toursStartedCount = integer("tours_started_count").default(0)
    val toursFinishedCount = integer("tours_finished_count").default(0)
    val historiesDoneCount = integer("histories_done_count").default(0)
    val arScansCount = integer("ar_scans_count").default(0)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(userId)
}
