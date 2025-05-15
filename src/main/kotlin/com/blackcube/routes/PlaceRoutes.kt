package com.blackcube.routes

import com.blackcube.data.repository.PlaceRepository
import com.blackcube.models.places.PlaceModel
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.registerPlaceRoutes(repository: PlaceRepository) {
    route("/places") {
        get {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()
            val tours: List<PlaceModel> =
                if (limit != null) repository.getPlaces(limit) else repository.getPlaces()
            call.respond(tours)
        }

        get("{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid id")
                return@get
            }
            val tour = repository.getPlaceById(id)
            if (tour == null) {
                call.respond(HttpStatusCode.NotFound, "Place not found")
            } else {
                call.respond(tour)
            }
        }
    }
}