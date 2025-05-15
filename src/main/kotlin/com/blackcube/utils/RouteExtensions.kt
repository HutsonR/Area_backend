package com.blackcube.utils

import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route

fun Route.secure(routeSetup: Route.() -> Unit) {
    authenticate("jwt-auth") {
        routeSetup()
    }
}