package com.blackcube.routes

import com.blackcube.data.repository.HistoryRepository
import com.blackcube.utils.JwtConfig.currentUserId
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.registerHistoryRoutes(repository: HistoryRepository) {
    route("/tours/histories") {
        post("/{historyId}/complete") {
            val historyId = call.parameters["historyId"]
            if (historyId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid id")
                return@post
            }
            val userId = call.currentUserId()
            if (repository.completeHistory(userId, historyId))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.BadRequest)
        }
    }
}