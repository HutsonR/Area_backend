package com.blackcube.data.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object UsersTable : UUIDTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val name = varchar("name", 200)
    val passwordHash = varchar("password_hash", 60)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
}