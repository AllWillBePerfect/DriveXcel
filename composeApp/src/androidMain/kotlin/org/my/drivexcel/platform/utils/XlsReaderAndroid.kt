package org.my.drivexcel.platform.utils

import android.content.Context
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class XlsReaderAndroid(
    private val context: Context
) : XlsReader {
    // Для Android лучше работать с File или URI, здесь пример с File
    override fun readExcel(filePath: String): List<LeaderUser> {
        val result = mutableListOf<LeaderUser>()
        val file = File(filePath)

        // Открываем файл из внутреннего хранилища
        context.openFileInput(file.name).use { fis ->
            val workbook = XSSFWorkbook(fis) // .xls формат
            val sheet = workbook.getSheetAt(0)

            // пропускаем заголовок
            val rows = sheet.drop(1)

            for (row in rows) {
                fun getCellString(index: Int): String? =
                    row.getCell(index)?.toString()?.trim()?.takeIf { it.isNotEmpty() }

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

            workbook.close()
        }

        return result
    }

    override fun writeExcel(filePath: String) {
        val file = File(context.filesDir, filePath)
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Sheet1")

        val row = sheet.createRow(0)
        row.createCell(0).setCellValue("Hello")
        row.createCell(1).setCellValue("World")

        FileOutputStream(file).use { fos ->
            workbook.write(fos)
        }
        workbook.close()
    }

}