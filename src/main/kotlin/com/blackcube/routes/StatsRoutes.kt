package com.blackcube.routes

import com.blackcube.data.repository.StatsRepository
import com.blackcube.utils.JwtConfig.currentUserId
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.registerStatsRoutes(repository: StatsRepository) {
    route("/stats") {
        get {
            val userId = call.currentUserId()
            val stats = repository.getStats(userId)
            if (stats != null) {
                call.respond(HttpStatusCode.OK, stats)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}