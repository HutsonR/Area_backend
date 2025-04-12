package com.blackcube.api

import com.blackcube.models.tours.TourModel
import com.blackcube.repository.TourRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.registerExcursionRoutes(repository: TourRepository) {
    routing {
        get("/tours") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()
            val tours: List<TourModel> =
                if (limit != null) repository.getTours(limit) else repository.getTours()
            call.respond(tours)
        }

        get("/tours/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid id")
                return@get
            }
            val tour = repository.getTourById(id)
            if (tour == null) {
                call.respond(HttpStatusCode.NotFound, "Tour not found")
            } else {
                call.respond(tour)
            }
        }

        put("/tours/{id}") {
            val id = call.parameters["id"]
            if (id.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid id")
                return@put
            }

            val updatedTour = call.receive<TourModel>()
            val updated = repository.updateTour(id, updatedTour)
            if (updated) {
                call.respond(HttpStatusCode.OK, "Tour updated successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Tour not found")
            }
        }
    }
}