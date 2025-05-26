package com.blackcube.routes

import com.blackcube.data.repository.TtsRepository
import com.blackcube.utils.LoggerUtil
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.registerTtsRoutes(repository: TtsRepository) {
    route("/tts") {
        post {
            val env = dotenv()
            val ttsAuthKey = env["TTS_AUTH_KEY"] ?: error("TTS_AUTH_KEY не задан в .env")
            val ttsRequest = call.receive<String>()
            LoggerUtil.log("Received TTS request: ${ttsRequest.take(30)}...")

            val audioBytes = repository.convertTextToSpeech(ttsAuthKey, ttsRequest)
            if (audioBytes != null) {
                call.respondBytes(audioBytes, contentType = ContentType("audio", "wav"))
            } else {
                call.respond(HttpStatusCode.InternalServerError, "TTS processing failed")
            }
        }
    }
}