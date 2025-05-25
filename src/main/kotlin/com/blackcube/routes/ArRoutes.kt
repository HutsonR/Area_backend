package com.blackcube.routes

import com.blackcube.data.repository.ArRepository
import com.blackcube.utils.JwtConfig.currentUserId
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.registerArRoutes(repository: ArRepository) {
    route("/tours/ar") {
        post("/{arObjectId}/scan") {
            val arObjectId = call.parameters["arObjectId"]
            if (arObjectId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid id")
                return@post
            }
            val userId = call.currentUserId()
            if (repository.scanArObject(userId, arObjectId))
                call.respond(HttpStatusCode.OK)
            else
                call.respond(HttpStatusCode.BadRequest)
        }
    }
}