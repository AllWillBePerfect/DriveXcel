package org.my.drivexcel.data.parser

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.my.drivexcel.base.domain.exception.DomainException
import org.my.drivexcel.data.models.LeaderUserDataModel
import org.my.drivexcel.domain.model.LeaderUserDomainModel
import org.my.drivexcel.domain.parser.ExcelParser
import java.io.ByteArrayInputStream

class ExcelParserImpl : ExcelParser {

    override fun parseToDomain(bytes: ByteArray): List<LeaderUserDomainModel> {
        try {
            ByteArrayInputStream(bytes).use { input ->
                val workbook = WorkbookFactory.create(input)
                val sheet = workbook.getSheetAt(0)

                val header = sheet.getRow(0)
                validateHeader(header)

                return (1..sheet.lastRowNum)
                    .mapNotNull { sheet.getRow(it) }
                    .map { it.toDomain() }
            }
        } catch (e: ExcelValidationException) {
            throw e
        } catch (e: Exception) {
            throw ExcelParseException("Invalid Excel file or corrupted content", e)
        }
    }

    override fun parseToData(bytes: ByteArray): List<LeaderUserDataModel> {
        try {
            ByteArrayInputStream(bytes).use { input ->
                val workbook = WorkbookFactory.create(input)
                val sheet = workbook.getSheetAt(0)

                val header = sheet.getRow(0)
                validateHeader(header)

                return (1..sheet.lastRowNum)
                    .mapNotNull { sheet.getRow(it) }
                    .map { it.toData() }
            }
        } catch (e: ExcelValidationException) {
            throw e
        } catch (e: Exception) {
            throw ExcelParseException("Invalid Excel file or corrupted content", e)
        }
    }

    private val expectedHeaders = listOf(
        "№",
        "ФИО",
        "Возраст",
        "Компания",
        "Должность",
        "Роль",
        "Формат участия",
        "Черная метка",
        "Дата посещения",
        "Дата заявки",
        "Статус заявки",
        "Email",
        "Телефон",
        "Город",
        "Регион",
        "Место учебы",
        "Специальность",
        "Форма обучения",
        "Основа обучения",
        "Уровень образования"
    )
    private fun validateHeader(headerRow: Row?) {
        requireNotNull(headerRow) { throw ExcelValidationException("Excel is empty") }

        val actual = (0 until expectedHeaders.size)
            .map { headerRow.getCell(it).string() }

        if (actual != expectedHeaders) {
            throw ExcelValidationException(
                "Invalid Excel structure.\nExpected: $expectedHeaders\nActual: $actual"
            )
        }
    }

    private fun Row.toDomain(): LeaderUserDomainModel =
        LeaderUserDomainModel(
            id = getCell(0).int(),
            fullName = getCell(1).string(),
            age = getCell(2).int(),
            company = getCell(3).nullableString(),
            jobTitle = getCell(4).nullableString(),
            role = getCell(5).string(),
            format = getCell(6).string(),
            blackMark = getCell(7).string(),
            dateOfVisit = getCell(8).nullableString(),
            applicationDate = getCell(9).string(),
            applicationStatus = getCell(10).string(),
            email = getCell(11).string(),
            phone = getCell(12).string(),
            city = getCell(13).string(),
            region = getCell(14).string(),
            placeOfStudy = getCell(15).string(),
            speciality = getCell(16).nullableString(),
            formOfStudy = getCell(17).nullableString(),
            studyFormat = getCell(18).nullableString(),
            educationLevel = getCell(19).nullableString()
        )

    private fun Row.toData(): LeaderUserDataModel =
        LeaderUserDataModel(
            id = getCell(0).int(),
            fullName = getCell(1).string(),
            age = getCell(2).int(),
            company = getCell(3).nullableString(),
            jobTitle = getCell(4).nullableString(),
            role = getCell(5).string(),
            format = getCell(6).string(),
            blackMark = getCell(7).string(),
            dateOfVisit = getCell(8).nullableString(),
            applicationDate = getCell(9).string(),
            applicationStatus = getCell(10).string(),
            email = getCell(11).string(),
            phone = getCell(12).string(),
            city = getCell(13).string(),
            region = getCell(14).string(),
            placeOfStudy = getCell(15).string(),
            speciality = getCell(16).nullableString(),
            formOfStudy = getCell(17).nullableString(),
            studyFormat = getCell(18).nullableString(),
            educationLevel = getCell(19).nullableString()
        )
}
private fun Cell?.string(): String =
    this?.toString()?.trim().orEmpty()

private fun Cell?.nullableString(): String? =
    this?.toString()?.trim()?.takeIf { it.isNotEmpty() }

private fun Cell?.int(): Int =
    when (this?.cellType) {
        CellType.NUMERIC -> numericCellValue.toInt()
        CellType.STRING -> stringCellValue.toIntOrNull() ?: 0
        else -> 0
    }

class ExcelParseException(message: String, cause: Throwable? = null) : DomainException(cause, message)

class ExcelValidationException(message: String) : DomainException(message = message)