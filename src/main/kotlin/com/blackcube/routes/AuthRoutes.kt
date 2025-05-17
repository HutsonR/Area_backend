package com.blackcube.routes

import com.blackcube.data.models.auth.LoginRequest
import com.blackcube.data.models.auth.RegistrationRequest
import com.blackcube.data.service.AuthService
import com.blackcube.models.auth.TokenResponse
import com.blackcube.utils.encryption.RSAKeyProvider
import com.blackcube.utils.encryption.RsaUtil
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.registerAuthRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val req = call.receive<RegistrationRequest>()
            val decryptedReq = req.copy(password = decryptString(req.password))

            val user = authService.register(decryptedReq)
            val token = authService.generateToken(user)
            call.respond(TokenResponse(token))
        }
        post("/login") {
            val req = call.receive<LoginRequest>()
            val decryptedReq = req.copy(password = decryptString(req.password))

            val user = authService.authenticate(decryptedReq)
            val token = authService.generateToken(user)
            call.respond(TokenResponse(token))
        }
    }
}

private fun decryptString(encryptedText: String): String {
    val privateKey = RSAKeyProvider.privateKey
    val decryptedPasswordBase64: ByteArray = RsaUtil.decrypt(encryptedText, privateKey)
    return String(decryptedPasswordBase64, Charsets.UTF_8)
}