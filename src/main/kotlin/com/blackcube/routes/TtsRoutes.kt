package com.blackcube.routes

import com.blackcube.data.repository.TtsRepository
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.registerTtsRoutes(repository: TtsRepository) {
    routing {
        post("/tts") {
            val env = dotenv()
            val ttsAuthKey = env["TTS_AUTH_KEY"] ?: error("TTS_AUTH_KEY не задан в .env")
            val ttsRequest = call.receive<String>()
            logger.info { "Received TTS request: ${ttsRequest.take(30)}..." }

            val audioBytes = repository.convertTextToSpeech(ttsAuthKey, ttsRequest)
            if (audioBytes != null) {
                call.respondBytes(audioBytes, contentType = ContentType("audio", "wav"))
            } else {
                call.respond(HttpStatusCode.InternalServerError, "TTS processing failed")
            }
        }
    }
}