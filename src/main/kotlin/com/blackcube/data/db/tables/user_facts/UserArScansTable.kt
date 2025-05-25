package com.blackcube.data.db.tables.user_facts

import com.blackcube.data.db.tables.ArObjectsTable
import com.blackcube.data.db.tables.UsersTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object UserArScansTable : Table("user_ar_scans") {
    val userId = reference("user_id", UsersTable.id)
    val arObjectId = reference("ar_object_id", ArObjectsTable.id)
    val scannedAt = timestamp("scanned_at").defaultExpression(CurrentTimestamp)
    override val primaryKey = PrimaryKey(userId, arObjectId)
}