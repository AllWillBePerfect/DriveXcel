package org.my.drivexcel.v4.data.models

import kotlinx.serialization.Serializable

@Serializable
data class HistoryDataModel(
    val list: List<HistoryEntry>
)
@Serializable
data class HistoryEntry(
    val id: String,
    val timestamp: Long,
    val actions: List<HistoryAction>
)

@Serializable
sealed interface HistoryAction {

    @Serializable
    data class Add(
        val userId: Int,
        val fullName: String
    ) : HistoryAction

    @Serializable

    data class Remove(
        val userId: Int,
        val fullName: String
    ) : HistoryAction

    @Serializable
    data class Replace(
        val oldUserId: Int,
        val newUserId: Int,
        val oldFullName: String,
        val newFullName: String
    ) : HistoryAction
}

fun buildHistoryActions(
    old: List<LeaderUserDataModel>,
    new: List<LeaderUserDataModel>
): List<HistoryAction> {

    val oldMap = old.associateBy { it.id }
    println("oldMap: $oldMap")
    val newMap = new.associateBy { it.id }
    println("newMap: $newMap")
    val actions = mutableListOf<HistoryAction>()

    for ((id, newUser) in newMap) {
        if (id !in oldMap) {
            actions += HistoryAction.Add(
                userId = id,
                fullName = newUser.fullName
            )
        }
    }

    for ((id, oldUser) in oldMap) {
        if (id !in newMap) {
            actions += HistoryAction.Remove(
                userId = id,
                fullName = oldUser.fullName
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

