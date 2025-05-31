package com.blackcube

import com.blackcube.data.db.DatabaseFactory
import com.blackcube.data.repository.ArRepositoryImpl
import com.blackcube.data.repository.HistoryRepositoryImpl
import com.blackcube.data.repository.PlaceRepositoryImpl
import com.blackcube.data.repository.StatsRepositoryImpl
import com.blackcube.data.repository.TourRepositoryImpl
import com.blackcube.data.repository.TtsRepositoryImpl
import com.blackcube.data.repository.UserRepositoryImpl
import com.blackcube.data.service.AuthService
import com.blackcube.routes.registerArRoutes
import com.blackcube.routes.registerAuthRoutes
import com.blackcube.routes.registerEncryptionRoutes
import com.blackcube.routes.registerExcursionRoutes
import com.blackcube.routes.registerHistoryRoutes
import com.blackcube.routes.registerPlaceRoutes
import com.blackcube.routes.registerStatsRoutes
import com.blackcube.routes.registerTtsRoutes
import com.blackcube.utils.configure.configureAuthorization
import com.blackcube.utils.configure.configureHTTP
import com.blackcube.utils.configure.configureSerialization
import com.blackcube.utils.encryption.RSAKeyProvider
import com.blackcube.utils.secure
import io.github.cdimascio.dotenv.dotenv
import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.server.application.Application
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import org.slf4j.LoggerFactory
import java.io.File

fun main() {
    embeddedServer(Netty, applicationEnvironment { log = LoggerFactory.getLogger("ktor.application") }, {
        envConfig()
    }, module = Application::module).start(wait = true)
}

private fun ApplicationEngine.Configuration.envConfig() {
    val env = dotenv()
    val keyStoreFile = File("certs/keystore.jks")
    val keyStore = buildKeyStore {
        certificate(env["KEYSTORE_ALIAS_NAME"]) {
            password = env["KEYSTORE_PASSWORD"]
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, env["KEY_PASSWORD"])

    connector {
        port = 8080
    }
    sslConnector(
        keyStore = keyStore,
        keyAlias = env["KEYSTORE_ALIAS_NAME"],
        keyStorePassword = { env["KEYSTORE_PASSWORD"].toCharArray() },
        privateKeyPassword = { env["KEY_PASSWORD"].toCharArray() }) {
        port = 8443
        keyStorePath = keyStoreFile
    }
}

fun Application.module() {
    DatabaseFactory.init(environment = environment)

    RSAKeyProvider.init()

    configureHTTP()
    configureSerialization()
    configureAuthorization()

    val userRepository = UserRepositoryImpl()
    val authService = AuthService(userRepository)
    val tourRepository = TourRepositoryImpl()
    val historyRepository = HistoryRepositoryImpl()
    val arRepository = ArRepositoryImpl()
    val placeRepository = PlaceRepositoryImpl()
    val ttsRepository = TtsRepositoryImpl()
    val statsRepository = StatsRepositoryImpl()

    routing {
        registerEncryptionRoutes()
        registerAuthRoutes(authService)
        secure {
            registerExcursionRoutes(tourRepository)
            registerHistoryRoutes(historyRepository)
            registerArRoutes(arRepository)
            registerPlaceRoutes(placeRepository)
            registerTtsRoutes(ttsRepository)
            registerStatsRoutes(statsRepository)
        }
    }
}
