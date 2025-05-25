package com.blackcube.routes

import com.blackcube.data.repository.TourRepository
import com.blackcube.utils.JwtConfig.currentUserId
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.registerExcursionRoutes(repository: TourRepository) {
    route("/tours") {
        get {
            val userId = call.currentUserId()
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()
            call.respond(repository.getAllTours(userId, limit))
        }

        get("/{id}") {
            val tourId = call.parameters["id"]
            if (tourId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid id")
                return@get
            }
            val userId = call.currentUserId()
            repository.getTourById(userId, tourId)?.let { call.respond(it) }
                ?: call.respond(HttpStatusCode.NotFound, "Tour not found")
        }

        post("/{id}/start") {
            val tourId = call.parameters["id"]
            if (tourId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid id")
                return@post
            }
            val userId = call.currentUserId()
            if (repository.startTour(userId, tourId))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.BadRequest)
        }

        post("/{id}/finish") {
            val tourId = call.parameters["id"]
            if (tourId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid id")
                return@post
            }
            val userId = call.currentUserId()
            if (repository.finishTour(userId, tourId))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.BadRequest)
        }
    }
}