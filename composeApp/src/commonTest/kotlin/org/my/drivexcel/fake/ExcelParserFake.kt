package org.my.drivexcel.fake

import org.my.drivexcel.data.v3.ExcelParser
import org.my.drivexcel.domain.models.Participant

class ExcelParserFake : ExcelParser {
    override suspend fun parse(data: ByteArray): List<Participant> {
        TODO("Not yet implemented")
    }

    override suspend fun write(participants: List<Participant>): ByteArray {
        TODO("Not yet implemented")
    }
}