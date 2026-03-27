package org.my.drivexcel.v4.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UsersDataModel(
    val users: List<LeaderUserDataModel>,
    val changesVersion: Int,
    val changeDate: String
)


