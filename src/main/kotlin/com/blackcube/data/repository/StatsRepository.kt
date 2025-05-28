package com.blackcube.data.repository

import com.blackcube.data.db.tables.ArObjectsTable
import com.blackcube.data.db.tables.HistoriesTable
import com.blackcube.data.db.tables.ToursTable
import com.blackcube.data.db.tables.UsersTable
import com.blackcube.data.db.tables.user_facts.UserArScansTable
import com.blackcube.data.db.tables.user_facts.UserHistoryProgressTable
import com.blackcube.data.db.tables.user_facts.UserToursTable
import com.blackcube.data.utils.parseUuid
import com.blackcube.models.profile.StatsModel
import com.blackcube.utils.LoggerUtil
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface StatsRepository {
    suspend fun getStats(userId: String): StatsModel?
}

class StatsRepositoryImpl : StatsRepository {
    override suspend fun getStats(userId: String): StatsModel? = newSuspendedTransaction(Dispatchers.IO) {
        LoggerUtil.log("Getting stats for user: $userId")
        val userUuid = parseUuid(userId) ?: return@newSuspendedTransaction null

        // 1. Общие количества в системе
        val totalUsers = UsersTable.selectAll().count()
        val totalTours = ToursTable.selectAll().count()
        val totalArObjects = ArObjectsTable.selectAll().count()
        val totalHistories = HistoriesTable.selectAll().count()

        // 2. Показатели конкретного пользователя
        val userFinishedTours = UserToursTable
            .selectAll()
            .where { (UserToursTable.userId eq userUuid) and UserToursTable.finishedAt.isNotNull() }
            .count()
        val userScannedAr = UserArScansTable
            .selectAll()
            .where { UserArScansTable.userId eq userUuid }
            .count()
        val userDoneHistories = UserHistoryProgressTable
            .selectAll()
            .where { UserHistoryProgressTable.userId eq userUuid }
            .count()

        // 3. Сколько людей прошло (без повтора)
        val tourUsersCount = UserToursTable
            .selectAll()
            .map { it[UserToursTable.userId] }
            .distinct()
            .count()
        val arUsersCount = UserArScansTable
            .selectAll()
            .map { it[UserArScansTable.userId] }
            .distinct()
            .count()
        val histUsersCount = UserHistoryProgressTable
            .selectAll()
            .map { it[UserHistoryProgressTable.userId] }
            .distinct()
            .count()

        fun pctBetter(totalUsers: Long, totalCompleted: Long): Double {
            return if (totalCompleted != 0L) {
                val better = totalUsers - totalCompleted
                better.toDouble() / totalUsers.toDouble() * 100.0
            } else {
                0.0
            }
        }

        val toursPctAbove = if (userFinishedTours > 0)
            pctBetter(totalUsers, tourUsersCount.toLong())
        else 0.0

        val arPctAbove = if (userScannedAr > 0)
            pctBetter(totalUsers, arUsersCount.toLong())
        else 0.0

        val histPctAbove = if (userDoneHistories > 0)
            pctBetter(totalUsers, histUsersCount.toLong())
        else 0.0

        StatsModel(
            totalTours = totalTours,
            userFinishedTours = userFinishedTours,
            toursPctAbove = toursPctAbove,
            totalArObjects = totalArObjects,
            userScannedAr = userScannedAr,
            arPctAbove = arPctAbove,
            totalHistories = totalHistories,
            userDoneHistories = userDoneHistories,
            histPctAbove = histPctAbove
        )
    }
}