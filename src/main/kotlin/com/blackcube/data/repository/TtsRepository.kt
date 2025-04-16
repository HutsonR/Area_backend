package com.blackcube.data.repository

import com.blackcube.models.tts.TokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.UUID
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

private val logger = KotlinLogging.logger {}

interface TtsRepository {
    suspend fun convertTextToSpeech(ttsAuthKey: String, text: String): ByteArray?
}

class TtsRepositoryImpl : TtsRepository {
    override suspend fun convertTextToSpeech(ttsAuthKey: String, text: String): ByteArray? {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }

        try {
            // Получение токена
            val tokenResponse: TokenResponse =
                client.post("https://ngw.devices.sberbank.ru:9443/api/v2/oauth") {
                    header("RqUID", UUID.randomUUID().toString())
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.FormUrlEncoded.toString()
                    )
                    header(
                        HttpHeaders.Authorization,
                        "Bearer $ttsAuthKey"
                    )
                    setBody(FormDataContent(Parameters.build {
                        append("scope", "SALUTE_SPEECH_PERS")
                    }))
                }.body()

            logger.info { "Token received: ${tokenResponse.accessToken.take(10)}..." }

            // Синтез речи
            val ttsResponse = client.post("https://smartspeech.sber.ru/rest/v1/text:synthesize") {
                header(HttpHeaders.ContentType, "application/ssml")
                header(HttpHeaders.Authorization, "Bearer ${tokenResponse.accessToken}")
                parameter("format", "wav16")
                parameter("voice", "Bys_24000")
                setBody(text)
            }
            if (ttsResponse.status.isSuccess()) {
                val audioBytes = ttsResponse.body<ByteArray>()
                logger.info { "TTS synthesis succeeded." }
                return audioBytes
            } else {
                logger.error { "TTS synthesis failed: ${ttsResponse.status}" }
                return null
            }
        } catch (e: Exception) {
            logger.error(e) { "Error processing TTS for text: ${text.take(30)}..." }
            return null
        } finally {
            client.close()
        }
    }

    private fun createHttpClientWithCustomCert(): HttpClient {
        // Загружаем сертификат из ресурсов (например, /russiantrustedca.crt)
        val certInputStream: InputStream =
            object {}.javaClass.getResourceAsStream("/russiantrustedca.crt")
                ?: throw RuntimeException("Certificate file not found in resources")

        val certificateFactory = CertificateFactory.getInstance("X.509")
        val caCert = certificateFactory.generateCertificate(certInputStream)
        certInputStream.close()

        // Создаём пустой KeyStore и добавляем наш сертификат в него
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(null, null)
        keyStore.setCertificateEntry("russiantrusted", caCert)

        // Инициализируем TrustManagerFactory с нашим KeyStore
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(keyStore)
        val trustManagers = tmf.trustManagers
        val x509TrustManager = trustManagers.first { it is X509TrustManager } as X509TrustManager

        // Создаем HttpClient с использованием CIO-движка и кастомного trustManager
        return HttpClient(CIO) {
            engine {
                https {
                    trustManager = x509TrustManager
                }
            }
        }
    }
}