package org.my.drivexcel.domain.parser

import org.my.drivexcel.domain.model.LeaderUserDomainModel
import org.my.drivexcel.data.models.LeaderUserDataModel

interface ExcelParser {
    fun parseToData(bytes: ByteArray): List<LeaderUserDataModel>
    fun parseToDomain(bytes: ByteArray): List<LeaderUserDomainModel>
}