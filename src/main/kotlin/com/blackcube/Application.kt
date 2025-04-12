package com.blackcube

import com.blackcube.api.registerExcursionRoutes
import com.blackcube.api.registerPlaceRoutes
import com.blackcube.repository.PlaceRepositoryImpl
import com.blackcube.repository.TourRepositoryImpl
import com.blackcube.utils.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureHTTP()

    val excursionRepository = TourRepositoryImpl()
    registerExcursionRoutes(excursionRepository)

    val placeRepository = PlaceRepositoryImpl()
    registerPlaceRoutes(placeRepository)
}
