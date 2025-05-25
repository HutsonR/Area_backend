package com.blackcube

import com.blackcube.data.db.DatabaseFactory
import com.blackcube.data.repository.ArRepositoryImpl
import com.blackcube.data.repository.HistoryRepositoryImpl
import com.blackcube.data.repository.PlaceRepositoryImpl
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
import com.blackcube.routes.registerTtsRoutes
import com.blackcube.utils.configure.configureAuthorization
import com.blackcube.utils.configure.configureSerialization
import com.blackcube.utils.encryption.RSAKeyProvider
import com.blackcube.utils.secure
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(environment = environment)

    RSAKeyProvider.init()

    configureSerialization()
    configureHTTP()
    configureAuthorization()

    val userRepository = UserRepositoryImpl()
    val authService = AuthService(userRepository)
    val tourRepository = TourRepositoryImpl()
    val historyRepository = HistoryRepositoryImpl()
    val arRepository = ArRepositoryImpl()
    val placeRepository = PlaceRepositoryImpl()
    val ttsRepository = TtsRepositoryImpl()

    routing {
        registerEncryptionRoutes()
        registerAuthRoutes(authService)
        secure {
            registerExcursionRoutes(tourRepository)
            registerHistoryRoutes(historyRepository)
            registerArRoutes(arRepository)
            registerPlaceRoutes(placeRepository)
            registerTtsRoutes(ttsRepository)
        }
    }
}
