package org.my.drivexcel.domain.usecase

import org.my.drivexcel.domain.model.LeaderUserDomainModel
import org.my.drivexcel.domain.parser.ExcelParser
import org.my.drivexcel.domain.repository.EventRepository
import org.my.drivexcel.base.domain.wrapper.DomainResult
import org.my.drivexcel.base.domain.ext.runCatchingDomainResult
import org.my.drivexcel.data.models.HistoryAction
import org.my.drivexcel.data.models.HistoryEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.collections.iterator

class SaveLeaderUsersUseCase(
    private val repo: EventRepository,
    private val parser: ExcelParser,
) {
    suspend fun invokeFromXls(
        id: String,
        xlsList: List<ByteArray>
    ): DomainResult<Unit> =
        runCatchingDomainResult {
            val parsed = xlsList.flatMap { parser.parseToDomain(it) }

            val uniqueUsers = parsed
                .groupBy { it.id }
                .map { (_, users) ->
                    users.maxByOrNull { it.dateOfVisit.toComparableDate() }!!
                }

            saveInternal(id, uniqueUsers)
        }

    suspend fun invokeFromUsers(
        id: String,
        uniqueUsers: List<LeaderUserDomainModel>
    ): DomainResult<Unit> =
        runCatchingDomainResult {
            saveInternal(id, uniqueUsers)
        }

    private suspend fun saveInternal(
        id: String,
        incomingUsers: List<LeaderUserDomainModel>
    ) {
        val oldUsers = repo.getUsers(id)

        val mergedUsers = (oldUsers + incomingUsers)
            .groupBy { it.id }
            .map { (_, users) ->
                users.maxByOrNull { it.dateOfVisit.toComparableDate() }!!
            }

        val actions = buildHistoryActions(oldUsers, mergedUsers)

        val historyEntry = HistoryEntry(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            actions = actions
        )

        repo.saveUsers(id, mergedUsers)
        repo.appendHistory(id, historyEntry)
    }
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    private fun String?.toComparableDate(): LocalDate {
        return try {
            this?.let { LocalDate.parse(it, formatter) }
                ?: LocalDate.MIN
        } catch (e: Exception) {
            LocalDate.MIN
        }
    }

    private fun buildHistoryActions(
        old: List<LeaderUserDomainModel>,
        new: List<LeaderUserDomainModel>
    ): List<HistoryAction> {

        val oldMap = old.associateBy { it.id }
        val newMap = new.associateBy { it.id }
        val actions = mutableListOf<HistoryAction>()

        for ((id, newUser) in newMap) {
            if (id !in oldMap) {
                actions += HistoryAction.Add(
                    userId = id,
                    fullName = newUser.fullName
                )
            }
        }

        for ((id, newUser) in newMap) {
            val oldUser = oldMap[id] ?: continue

            if (oldUser.fullName != newUser.fullName) {
                actions += HistoryAction.Replace(
                    oldUserId = id,
                    newUserId = id,
                    oldFullName = oldUser.fullName,
                    newFullName = newUser.fullName
                )
            }
        }

        return actions
    }

}