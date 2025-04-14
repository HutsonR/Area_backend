package com.blackcube

import com.blackcube.api.registerExcursionRoutes
import com.blackcube.api.registerPlaceRoutes
import com.blackcube.data.db.DatabaseFactory
import com.blackcube.data.repository.PlaceRepositoryImpl
import com.blackcube.data.repository.TourRepositoryImpl
import com.blackcube.utils.configureSerialization
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(environment = environment)

    configureSerialization()
    configureHTTP()

    val excursionRepository = TourRepositoryImpl()
    registerExcursionRoutes(excursionRepository)

    val placeRepository = PlaceRepositoryImpl()
    registerPlaceRoutes(placeRepository)
}
