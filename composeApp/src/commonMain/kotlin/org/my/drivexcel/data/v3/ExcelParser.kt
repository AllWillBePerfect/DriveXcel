package org.my.drivexcel.data.v3

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.my.drivexcel.domain.models.Participant
import org.my.drivexcel.domain.models.ParticipantId
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

interface ExcelParser {
    suspend fun parse(data: ByteArray): List<Participant>
    suspend fun write(participants: List<Participant>): ByteArray

    class Impl() : ExcelParser {
        override suspend fun parse(data: ByteArray): List<Participant> {
            return withContext(Dispatchers.IO) {
                val result = mutableListOf<Participant>()

                ByteArrayInputStream(data).use { input ->
                    val workbook = XSSFWorkbook(input)
                    val sheet = workbook.getSheetAt(0)

                    val header = sheet.getRow(0)
                    val columns = header.associate {
                        it.stringCellValue.trim() to it.columnIndex
                    }

                    for (i in 1..sheet.lastRowNum) {
                        val row = sheet.getRow(i) ?: continue

                        val participant = Participant(
                            id = ParticipantId(
                                row.getCell(columns["ID"]!!)
                                    .numericCellValue.toInt()
                            ),
                            fullName = row.getCell(columns["Full Name"]!!)
                                .stringCellValue,
                            age = row.getCell(columns["Age"]!!)
                                .numericCellValue.toInt(),
                            company = getNullableString(row, columns["Company"]!!),
                            jobTitle = getNullableString(row, columns["Job Title"]!!),
                            role = row.getCell(columns["Role"]!!)
                                .stringCellValue,
                            format = row.getCell(columns["Format"]!!)
                                .stringCellValue,
                            blackMark = row.getCell(columns["Black Mark"]!!)
                                ?.booleanCellValue ?: false,
                            dateOfVisit = getNullableString(row, columns["Date Of Visit"]!!),
                            applicationDate = row.getCell(columns["Application Date"]!!)
                                .stringCellValue,
                            applicationStatus = row.getCell(columns["Application Status"]!!)
                                .stringCellValue,
                            email = row.getCell(columns["Email"]!!)
                                .stringCellValue,
                            phone = row.getCell(columns["Phone"]!!)
                                .stringCellValue,
                            city = row.getCell(columns["City"]!!)
                                .stringCellValue,
                            region = row.getCell(columns["Region"]!!)
                                .stringCellValue,
                            placeOfStudy = row.getCell(columns["Place Of Study"]!!)
                                .stringCellValue,
                            speciality = getNullableString(row, columns["Speciality"]!!),
                            formOfStudy = getNullableString(row, columns["Form Of Study"]!!),
                            studyFormat = getNullableString(row, columns["Study Format"]!!),
                            educationLevel = getNullableString(row, columns["Education Level"]!!)

                        )

                        result.add(participant)
                    }


                    workbook.close()
                }

                result
            }
        }

        override suspend fun write(
            participants: List<Participant>
        ): ByteArray = withContext(Dispatchers.IO) {

            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Participants")

            // ---- HEADER ----
            val headers = listOf(
                "ID",
                "Full Name",
                "Age",
                "Company",
                "Job Title",
                "Role",
                "Format",
                "Black Mark",
                "Date Of Visit",
                "Application Date",
                "Application Status",
                "Email",
                "Phone",
                "City",
                "Region",
                "Place Of Study",
                "Speciality",
                "Form Of Study",
                "Study Format",
                "Education Level"
            )

            val headerRow = sheet.createRow(0)
            headers.forEachIndexed { index, header ->
                headerRow.createCell(index).setCellValue(header)
            }

            // ---- DATA ----
            participants.forEachIndexed { rowIndex, p ->
                val row = sheet.createRow(rowIndex + 1)

                row.createCell(0).setCellValue(p.id.value.toDouble())
                row.createCell(1).setCellValue(p.fullName)
                row.createCell(2).setCellValue(p.age.toDouble())
                row.createCell(3).setCellValue(p.company ?: "")
                row.createCell(4).setCellValue(p.jobTitle ?: "")
                row.createCell(5).setCellValue(p.role)
                row.createCell(6).setCellValue(p.format)
                row.createCell(7).setCellValue(p.blackMark)
                row.createCell(8).setCellValue(p.dateOfVisit ?: "")
                row.createCell(9).setCellValue(p.applicationDate)
                row.createCell(10).setCellValue(p.applicationStatus)
                row.createCell(11).setCellValue(p.email)
                row.createCell(12).setCellValue(p.phone)
                row.createCell(13).setCellValue(p.city)
                row.createCell(14).setCellValue(p.region)
                row.createCell(15).setCellValue(p.placeOfStudy)
                row.createCell(16).setCellValue(p.speciality ?: "")
                row.createCell(17).setCellValue(p.formOfStudy ?: "")
                row.createCell(18).setCellValue(p.studyFormat ?: "")
                row.createCell(19).setCellValue(p.educationLevel ?: "")
            }

            // ---- AUTO SIZE (опционально) ----
            headers.indices.forEach { sheet.autoSizeColumn(it) }

            val outputStream = ByteArrayOutputStream()
            workbook.write(outputStream)
            workbook.close()

            outputStream.toByteArray()
        }

        private fun getNullableString(row: Row, index: Int?): String? {
            if (index == null) return null
            val cell = row.getCell(index) ?: return null
            val value = cell.toString().trim()
            return value.ifEmpty { null }
        }


    }
}