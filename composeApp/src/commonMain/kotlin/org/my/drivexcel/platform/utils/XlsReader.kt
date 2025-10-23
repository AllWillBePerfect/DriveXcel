package org.my.drivexcel.platform.utils

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.io.OutputStream

abstract class XlsReader {
    protected abstract fun openInput(path: String): InputStream
    protected abstract fun openOutput(path: String): OutputStream

    fun readExcel(filePath: String): List<LeaderUser> {
        val result = mutableListOf<LeaderUser>()

        openInput(filePath).use { fis ->
            XSSFWorkbook(fis).use { workbook ->
                val sheet = workbook.getSheetAt(0)
                val rows = sheet.drop(1)

                for (row in rows) {
                    fun getCellString(index: Int): String? {
                        val cell = row.getCell(index)
                        return when (cell?.cellType) {
                            CellType.STRING -> cell.stringCellValue.trim()
                            CellType.NUMERIC -> cell.numericCellValue.toInt().toString()
                            CellType.BOOLEAN -> cell.booleanCellValue.toString()
                            else -> null
                        }
                    }

                    try {
                        val user = LeaderUser(
                            id = getCellString(0)?.toIntOrNull() ?: 0,
                            fullName = getCellString(1) ?: "",
                            age = getCellString(2)?.toIntOrNull() ?: 0,
                            company = getCellString(3),
                            jobTitle = getCellString(4),
                            role = getCellString(5) ?: "",
                            format = getCellString(6) ?: "",
                            blackMark = getCellString(7)?.equals("true", ignoreCase = true) == true,
                            dateOfVisit = getCellString(8),
                            applicationDate = getCellString(9) ?: "",
                            applicationStatus = getCellString(10) ?: "",
                            email = getCellString(11) ?: "",
                            phone = getCellString(12) ?: "",
                            city = getCellString(13) ?: "",
                            region = getCellString(14) ?: "",
                            placeOfStudy = getCellString(15) ?: "",
                            speciality = getCellString(16),
                            formOfStudy = getCellString(17),
                            studyFormat = getCellString(18),
                            educationLevel = getCellString(19)
                        )
                        result.add(user)
                    } catch (e: Exception) {
                        println("Ошибка при парсинге строки: ${e.message}")
                    }
                }
            }
        }

        return result
    }

    fun writeExcel(filePath: String) {
        openOutput(filePath).use { fos ->
            XSSFWorkbook().use { workbook ->
                val sheet = workbook.createSheet("Sheet1")
                val row = sheet.createRow(0)
                row.createCell(0).setCellValue("Hello")
                row.createCell(1).setCellValue("World")
                workbook.write(fos)
            }
        }
    }
}


data class LeaderUser(
    val id: Int,
    val fullName: String,
    val age: Int,
    val company: String?,
    val jobTitle: String?,
    val role: String,
    val format: String,
    val blackMark: Boolean,
    val dateOfVisit: String?,
    val applicationDate: String,
    val applicationStatus: String,
    val email: String,
    val phone: String,
    val city: String,
    val region: String,
    val placeOfStudy: String,
    val speciality: String?,
    val formOfStudy: String?,
    val studyFormat: String?,
    val educationLevel: String?
) {
    val getFirstLetter: String = fullName.firstOrNull().toString()

    companion object {
        fun createDefault() = LeaderUser(
            id = 0,
            fullName = "Лидеров Лидер Лидерович",
            age = 0,
            company = null,
            jobTitle = null,
            role = "",
            format = "",
            blackMark = false,
            dateOfVisit = null,
            applicationDate = "",
            applicationStatus = "",
            email = "",
            phone = "",
            city = "",
            region = "",
            placeOfStudy = "",
            speciality = null,
            formOfStudy = null,
            studyFormat = null,
            educationLevel = null
        )
    }
}