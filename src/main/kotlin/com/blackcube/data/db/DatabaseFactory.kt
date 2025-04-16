package com.blackcube.data.db

import com.blackcube.data.db.tables.HistoriesTable
import com.blackcube.data.db.tables.PlacesTable
import com.blackcube.data.db.tables.ToursTable
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.ApplicationEnvironment
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(environment: ApplicationEnvironment) {
        val env = dotenv()
        val url = env["DB_URL"] ?: error("DB_URL не задан в .env")
        val user = env["DB_USER"] ?: error("DB_USER не задан в .env")
        val password = env["DB_PASSWORD"] ?: error("DB_PASSWORD не задан в .env")

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