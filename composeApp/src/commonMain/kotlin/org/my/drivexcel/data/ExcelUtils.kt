package org.my.drivexcel.data

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

/**
 * Утилитарный интерфейс для работы с Excel-файлами (формат XLSX).
 * Предоставляет базовые операции создания рабочих книг, листов,
 * записи и чтения строк, а также сохранения файлов.
 */
interface ExcelUtils {

    /**
     * Создаёт новую пустую рабочую книгу (XSSFWorkbook).
     * @return новый экземпляр XSSFWorkbook
     */
    fun createWorkbook(): XSSFWorkbook

    /**
     * Создаёт новый лист с заданным именем в рабочей книге.
     * @param workbook рабочая книга, в которой создаётся лист
     * @param name имя нового листа
     * @return созданный лист
     */
    fun createSheet(workbook: XSSFWorkbook, name: String): org.apache.poi.ss.usermodel.Sheet

    /**
     * Добавляет шапку (header) в указанный лист.
     * @param sheet лист, в котором создаётся шапка
     * @param headers список названий колонок
     */
    fun addHeader(sheet: org.apache.poi.ss.usermodel.Sheet, headers: List<String>)

    /**
     * Записывает значения в строку листа.
     * @param sheet лист, в который пишется строка
     * @param rowIndex индекс строки (0-based)
     * @param values список значений для каждой ячейки
     */
    fun writeRow(sheet: org.apache.poi.ss.usermodel.Sheet, rowIndex: Int, values: List<Any?>)

    /**
     * Читает строку из листа и возвращает значения ячеек как список строк.
     * @param row строка для чтения
     * @param size количество ячеек для чтения
     * @return список значений ячеек (String?), null если ячейка пуста или не поддерживается
     */
    fun readRow(row: org.apache.poi.ss.usermodel.Row, size: Int): List<String?>

    /**
     * Сохраняет рабочую книгу в файл и закрывает её.
     * @param workbook рабочая книга для сохранения
     * @param file файл для записи
     */
    fun saveWorkbook(workbook: XSSFWorkbook, file: File)


    /**
     * Стандартная реализация ExcelUtils.
     */
    class Impl() : ExcelUtils {

        override fun createWorkbook(): XSSFWorkbook = XSSFWorkbook()

        override fun createSheet(workbook: XSSFWorkbook, name: String): org.apache.poi.ss.usermodel.Sheet =
            workbook.createSheet(name)

        override fun addHeader(sheet: org.apache.poi.ss.usermodel.Sheet, headers: List<String>) {
            val row = sheet.createRow(0)
            headers.forEachIndexed { i, h -> row.createCell(i).setCellValue(h) }
        }

        override fun writeRow(sheet: org.apache.poi.ss.usermodel.Sheet, rowIndex: Int, values: List<Any?>) {
            val row = sheet.createRow(rowIndex)
            values.forEachIndexed { i, v ->
                when (v) {
                    is String -> row.createCell(i).setCellValue(v)
                    is Number -> row.createCell(i).setCellValue(v.toDouble())
                    is Boolean -> row.createCell(i).setCellValue(v)
                    null -> row.createCell(i).setCellValue("")
                }
            }
        }

        override fun readRow(row: org.apache.poi.ss.usermodel.Row, size: Int): List<String?> =
            (0 until size).map { i ->
                row.getCell(i)?.let { cell ->
                    when (cell.cellType) {
                        CellType.STRING -> cell.stringCellValue.trim()
                        CellType.NUMERIC -> cell.numericCellValue.toInt().toString()
                        CellType.BOOLEAN -> cell.booleanCellValue.toString()
                        else -> null
                    }
                }
            }

        override fun saveWorkbook(workbook: XSSFWorkbook, file: File) {
            FileOutputStream(file).use { workbook.write(it) }
            workbook.close()
        }

    }
}
