package com.blackcube.data.repository

import com.blackcube.data.db.tables.UsersTable
import com.blackcube.models.auth.UserModel
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

interface UserRepository {
    suspend fun create(email: String, name: String, passwordHash: String): UUID
    suspend fun findByEmail(email: String): UserModel?
}

class UserRepositoryImpl : UserRepository {
    override suspend fun create(email: String, name: String, passwordHash: String): UUID =
        newSuspendedTransaction {
            val insertStatement = UsersTable.insert {
                it[UsersTable.email] = email
                it[UsersTable.name] = name
                it[UsersTable.passwordHash] = passwordHash
            }
            insertStatement[UsersTable.id].value
        }

    override suspend fun findByEmail(email: String): UserModel? =
        newSuspendedTransaction {
            UsersTable
                .selectAll()
                .where { UsersTable.email eq email }
                .singleOrNull()
                ?.toUserModel()
        }

    private fun ResultRow.toUserModel() = UserModel(
        id = this[UsersTable.id].value.toString(),
        email = this[UsersTable.email],
        name = this[UsersTable.name],
        passwordHash = this[UsersTable.passwordHash],
        createdAt = this[UsersTable.createdAt]
    )
}