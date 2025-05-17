package com.blackcube.routes

import com.blackcube.models.encryption.PublicKeyModel
import com.blackcube.utils.encryption.RSAKeyProvider
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import java.util.Base64

fun Route.registerEncryptionRoutes() {
    get("/encryption/publicKey") {
        val publicKeyBytes = RSAKeyProvider.publicKey.encoded
        val publicKeyBase64 = Base64.getEncoder().encodeToString(publicKeyBytes)
        call.respond(PublicKeyModel(publicKeyBase64 = publicKeyBase64))
    }
}