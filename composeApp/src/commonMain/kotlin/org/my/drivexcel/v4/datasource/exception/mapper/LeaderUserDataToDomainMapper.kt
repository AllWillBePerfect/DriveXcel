package org.my.drivexcel.v4.datasource.exception.mapper

import org.my.drivexcel.v4.data.models.LeaderUserDataModel
import org.my.drivexcel.v4.domain.model.LeaderUserDomainModel

class LeaderUserDataToDomainMapper {

    fun toDomain(dataUser: LeaderUserDataModel): LeaderUserDomainModel = LeaderUserDomainModel(
        id = dataUser.id,
        fullName = dataUser.fullName
    )

    fun toDomainList(dataList: List<LeaderUserDataModel>): List<LeaderUserDomainModel> =
        dataList.map { toDomain(it) }
}