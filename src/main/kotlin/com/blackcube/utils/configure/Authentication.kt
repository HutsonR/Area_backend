package com.blackcube.utils.configure

import com.blackcube.utils.JwtConfig
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureAuthorization() {
    install(Authentication) {
        jwt("jwt-auth") {
            realm = JwtConfig.getRealm()
            verifier(JwtConfig.verifier)
            validate { credential ->
                if (credential.payload.getClaim("email").asString().isNotBlank())
                    JWTPrincipal(credential.payload)
                else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, typeInfo = null)
            }
        }
    }
}