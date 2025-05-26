package com.blackcube.data.utils

import com.blackcube.utils.LoggerUtil
import java.util.UUID

fun parseUuid(id: String): UUID? =
    runCatching { UUID.fromString(id) }
        .onFailure { e -> LoggerUtil.log("Invalid UUID format for id: $id\n Error: $e") }
        .getOrNull()

fun parseUuids(ids: List<String>): List<UUID>? =
    runCatching { ids.map { UUID.fromString(it) } }
        .onFailure { e -> LoggerUtil.log("Invalid UUID in list $ids\n Error: $e") }
        .getOrNull()