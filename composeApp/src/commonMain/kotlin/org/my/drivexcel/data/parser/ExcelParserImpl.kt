package org.my.drivexcel.data.parser

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.my.drivexcel.data.models.LeaderUserDataModel
import org.my.drivexcel.domain.model.LeaderUserDomainModel
import org.my.drivexcel.domain.parser.ExcelParser
import java.io.ByteArrayInputStream

class ExcelParserImpl : ExcelParser {
    override fun parseToData(bytes: ByteArray): List<LeaderUserDataModel> {
        val result = mutableListOf<LeaderUserDataModel>()

        ByteArrayInputStream(bytes).use { input ->
            val workbook = XSSFWorkbook(input)
            val sheet = workbook.getSheetAt(0)

            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue

                val user = LeaderUserDataModel(
                    id = row.getCell(0)?.numericCellValue?.toInt() ?: 0,
                    fullName = row.getCell(1)?.stringCellValue.orEmpty(),
                    age = row.getCell(2)?.numericCellValue?.toInt() ?: 0,
                    company = row.getCell(3)?.stringCellValue,
                    jobTitle = row.getCell(4)?.stringCellValue,
                    role = row.getCell(5)?.stringCellValue.orEmpty(),
                    format = row.getCell(6)?.stringCellValue.orEmpty(),
                    blackMark = row.getCell(7)?.stringCellValue.orEmpty(),
                    dateOfVisit = row.getCell(8)?.stringCellValue,
                    applicationDate = row.getCell(9)?.stringCellValue.orEmpty(),
                    applicationStatus = row.getCell(10)?.stringCellValue.orEmpty(),
                    email = row.getCell(11)?.stringCellValue.orEmpty(),
                    phone = row.getCell(12)?.stringCellValue.orEmpty(),
                    city = row.getCell(13)?.stringCellValue.orEmpty(),
                    region = row.getCell(14)?.stringCellValue.orEmpty(),
                    placeOfStudy = row.getCell(15)?.stringCellValue.orEmpty(),
                    speciality = row.getCell(16)?.stringCellValue,
                    formOfStudy = row.getCell(17)?.stringCellValue,
                    studyFormat = row.getCell(18)?.stringCellValue,
                    educationLevel = row.getCell(19)?.stringCellValue
                )

                result.add(user)
            }
        }

        return result
    }

    override fun parseToDomain(bytes: ByteArray): List<LeaderUserDomainModel> {
        val result = mutableListOf<LeaderUserDomainModel>()

        ByteArrayInputStream(bytes).use { input ->
            val workbook = XSSFWorkbook(input)
            val sheet = workbook.getSheetAt(0)

            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue

                val user = LeaderUserDomainModel(
                    id = row.getCell(0)?.numericCellValue?.toInt() ?: 0,
                    fullName = row.getCell(1)?.stringCellValue.orEmpty(),
                    age = row.getCell(2)?.numericCellValue?.toInt() ?: 0,
                    company = row.getCell(3)?.stringCellValue,
                    jobTitle = row.getCell(4)?.stringCellValue,
                    role = row.getCell(5)?.stringCellValue.orEmpty(),
                    format = row.getCell(6)?.stringCellValue.orEmpty(),
                    blackMark = row.getCell(7)?.stringCellValue.orEmpty(),
                    dateOfVisit = row.getCell(8)?.stringCellValue,
                    applicationDate = row.getCell(9)?.stringCellValue.orEmpty(),
                    applicationStatus = row.getCell(10)?.stringCellValue.orEmpty(),
                    email = row.getCell(11)?.stringCellValue.orEmpty(),
                    phone = row.getCell(12)?.stringCellValue.orEmpty(),
                    city = row.getCell(13)?.stringCellValue.orEmpty(),
                    region = row.getCell(14)?.stringCellValue.orEmpty(),
                    placeOfStudy = row.getCell(15)?.stringCellValue.orEmpty(),
                    speciality = row.getCell(16)?.stringCellValue,
                    formOfStudy = row.getCell(17)?.stringCellValue,
                    studyFormat = row.getCell(18)?.stringCellValue,
                    educationLevel = row.getCell(19)?.stringCellValue

                )

                result.add(user)
            }
        }

        return result
    }
}