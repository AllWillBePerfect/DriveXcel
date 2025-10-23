package org.my.drivexcel.data

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.my.drivexcel.platform.utils.LeaderUser
import java.io.File
import java.io.FileInputStream

/**
 * Сервис для работы с Excel-файлами лидеров.
 * Использует [ExcelUtils] для базовых операций с Excel.
 */
interface LeaderExcelService {

    /**
     * Создаёт новый пустой Excel-файл с шапкой для таблицы лидеров.
     * @param file файл, который будет создан
     */
    fun createEmptyTable(file: File)

    /**
     * Читает таблицу лидеров из Excel-файла.
     * Повреждённые строки пропускаются.
     * @param file файл для чтения
     * @return список пользователей [LeaderUser]
     */
    fun readTable(file: File): List<LeaderUser>

    /**
     * Записывает список пользователей в Excel-файл.
     * @param file файл для записи
     * @param users список пользователей
     */
    fun writeTable(file: File, users: List<LeaderUser>)

    /**
     * Объединяет несколько Excel-файлов с лидерами в один.
     * Каждый исходный файл записывается на отдельный лист.
     * @param files список файлов для объединения
     * @param outFile результирующий файл
     */
    fun mergeTables(files: List<File>, outFile: File)

    /**
     * Стандартная реализация сервиса с использованием [ExcelUtils].
     */
    class Impl(
        private val excelUtils: ExcelUtils
    ) : LeaderExcelService {

        private val headers = listOf(
            "ID","Full Name","Age","Company","Job Title",
            "Role","Format","Black Mark","Date of Visit",
            "Application Date","Application Status","Email",
            "Phone","City","Region","Place of Study",
            "Speciality","Form of Study","Study Format",
            "Education Level"
        )

        override fun createEmptyTable(file: File) {
            val wb = excelUtils.createWorkbook()
            val sheet = excelUtils.createSheet(wb, "Leaders")
            excelUtils.addHeader(sheet, headers)
            excelUtils.saveWorkbook(wb, file)
        }

        override fun readTable(file: File): List<LeaderUser> {
            if (!file.exists()) return emptyList()

            val users = mutableListOf<LeaderUser>()
            FileInputStream(file).use { fis ->
                val workbook = XSSFWorkbook(fis)
                val sheet = workbook.getSheetAt(0)
                val rows = sheet.drop(1) // пропускаем заголовок

                rows.forEach { row ->
                    val cells = excelUtils.readRow(row, headers.size)
                    try {
                        val user = LeaderUser(
                            id = cells[0]?.toIntOrNull() ?: 0,
                            fullName = cells[1] ?: "",
                            age = cells[2]?.toIntOrNull() ?: 0,
                            company = cells[3],
                            jobTitle = cells[4],
                            role = cells[5] ?: "",
                            format = cells[6] ?: "",
                            blackMark = cells[7]?.equals("true", ignoreCase = true) == true,
                            dateOfVisit = cells[8],
                            applicationDate = cells[9] ?: "",
                            applicationStatus = cells[10] ?: "",
                            email = cells[11] ?: "",
                            phone = cells[12] ?: "",
                            city = cells[13] ?: "",
                            region = cells[14] ?: "",
                            placeOfStudy = cells[15] ?: "",
                            speciality = cells[16],
                            formOfStudy = cells[17],
                            studyFormat = cells[18],
                            educationLevel = cells[19]
                        )
                        users.add(user)
                    } catch (_: Exception) {
                        // можно логировать ошибки, но пропускаем поврежденные строки
                    }
                }
                workbook.close()
            }

            return users
        }


        override fun writeTable(file: File, users: List<LeaderUser>) {
            val wb = excelUtils.createWorkbook()
            val sheet = excelUtils.createSheet(wb, "Leaders")
            excelUtils.addHeader(sheet, headers)

            users.forEachIndexed { i, u ->
                excelUtils.writeRow(sheet, i + 1, listOf(
                    u.id, u.fullName, u.age, u.company ?: "", u.jobTitle ?: "",
                    u.role, u.format, u.blackMark, u.dateOfVisit ?: "",
                    u.applicationDate, u.applicationStatus, u.email, u.phone,
                    u.city, u.region, u.placeOfStudy, u.speciality ?: "",
                    u.formOfStudy ?: "", u.studyFormat ?: "", u.educationLevel ?: ""
                ))
            }

            excelUtils.saveWorkbook(wb, file)
        }

        override fun mergeTables(files: List<File>, outFile: File) {
            val wb = excelUtils.createWorkbook()

            files.forEachIndexed { index, f ->
                val sheetName = "Merge ${index + 1}"
                val users = readTable(f)
                val sheet = excelUtils.createSheet(wb, sheetName)
                excelUtils.addHeader(sheet, headers)

                users.forEachIndexed { i, u ->
                    excelUtils.writeRow(sheet, i + 1, listOf(
                        u.id, u.fullName, u.age, u.company ?: "", u.jobTitle ?: "",
                        u.role, u.format, u.blackMark, u.dateOfVisit ?: "",
                        u.applicationDate, u.applicationStatus, u.email, u.phone,
                        u.city, u.region, u.placeOfStudy, u.speciality ?: "",
                        u.formOfStudy ?: "", u.studyFormat ?: "", u.educationLevel ?: ""
                    ))
                }
            }

            excelUtils.saveWorkbook(wb, outFile)
        }

    }
}

