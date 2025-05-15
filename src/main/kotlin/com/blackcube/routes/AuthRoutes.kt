package com.blackcube.routes

import com.blackcube.data.models.auth.LoginRequest
import com.blackcube.data.models.auth.RegistrationRequest
import com.blackcube.data.service.AuthService
import com.blackcube.models.auth.TokenResponse
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.registerAuthRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val req = call.receive<RegistrationRequest>()
            val user = authService.register(req)
            val token = authService.generateToken(user)
            call.respond(TokenResponse(token))
        }
        post("/login") {
            val req = call.receive<LoginRequest>()
            val user = authService.authenticate(req)
            val token = authService.generateToken(user)
            call.respond(TokenResponse(token))
        }
    }
}