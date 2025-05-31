package com.blackcube.utils.configure

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.httpsredirect.HttpsRedirect

fun Application.configureHTTP() {
    install(HttpsRedirect) {
        sslPort = 8443
        permanentRedirect = true
    }
}