package com.blackcube.data.db

import com.blackcube.data.db.tables.HistoriesTable
import com.blackcube.data.db.tables.PlacesTable
import com.blackcube.data.db.tables.ToursTable
import io.ktor.server.application.ApplicationEnvironment
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(environment: ApplicationEnvironment) {
        val url = environment.config.property("postgres.url").getString()
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()

        connectToPostgres(environment, url, user, password)

        transaction {
            SchemaUtils.create(ToursTable, HistoriesTable, PlacesTable)
        }
    }

    private fun connectToPostgres(
        environment: ApplicationEnvironment,
        url: String,
        user: String,
        password: String
    ) {
        environment.log.info("Connecting to PostgreSQL at $url")

        Database.connect(
            url = url,
            driver = "org.postgresql.Driver",
            user = user,
            password = password
        )
    }
}