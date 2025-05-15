package com.blackcube.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.blackcube.models.auth.UserModel
import io.github.cdimascio.dotenv.dotenv
import java.util.Date

object JwtConfig {
    private val env = dotenv()
    private val secret = env["JWT_SECRET"] ?: error("JWT_SECRET is not set")
    private val issuer = env["JWT_ISSUER"] ?: "com.blackcube"
    private val audience = env["JWT_AUDIENCE"] ?: "com.blackcube.app"
    private val realm = env["JWT_REALM"] ?: "Access to 'app'"
    private val expirationMs = env["JWT_EXPIRATION_MS"]?.toLong() ?: 3600000

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun generateToken(user: UserModel): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + expirationMs))
            .withClaim("email", user.email)
            .withClaim("userId", user.id)
            .sign(algorithm)
    }

    fun getRealm(): String = realm
}