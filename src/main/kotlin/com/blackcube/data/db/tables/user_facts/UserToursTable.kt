package com.blackcube.data.db.tables.user_facts

import com.blackcube.data.db.tables.ToursTable
import com.blackcube.data.db.tables.UsersTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object UserToursTable : Table("user_tours") {
    val userId = reference("user_id", UsersTable.id)
    val tourId = reference("tour_id", ToursTable.id)
    val startedAt = timestamp("started_at").defaultExpression(CurrentTimestamp)
    val finishedAt = datetime("finished_at").nullable()
    override val primaryKey = PrimaryKey(userId, tourId)
}