package com.blackcube.data.utils

import mu.KotlinLogging
import java.util.UUID

private val logger = KotlinLogging.logger {}

fun parseUuid(id: String): UUID? =
    runCatching { UUID.fromString(id) }
        .onFailure { e -> logger.error(e) { "Invalid UUID format for id: $id" } }
        .getOrNull()

fun parseUuids(ids: List<String>): List<UUID>? =
    runCatching { ids.map { UUID.fromString(it) } }
        .onFailure { e -> logger.error(e) { "Invalid UUID in list $ids" } }
        .getOrNull()