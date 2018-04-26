package DAO.Excel

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder

private fun getExcelFails(patch: String): List<File> {
    val files = File(patch).listFiles()
            .asList()
            .filter {
                it.isFile
            }.filter {
                it.absolutePath.contains(FILENAME_EXTENSION)
            }
    if (files.isEmpty()) throw Exception("В по указаному пути нет файлов с расширением $FILENAME_EXTENSION")
    return files
}

private fun getWorkbookFromFile(file: File): XSSFWorkbook {
    return try {
        FileInputStream(file).use {
            XSSFWorkbook(it)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        throw e
    }
}

fun getListWorkbook(patch: String): List<XSSFWorkbook> {
    return getExcelFails(patch).map {
        getWorkbookFromFile(it)
    }.toList()
}

fun XSSFWorkbook.writeToFile(patchOut: String, fileName: String) {
    val path = StringBuilder()
            .append(patchOut)
            .append(fileName)
            .append(FILENAME_EXTENSION)
            .toString()
    File(path).delete()
    try {
        FileOutputStream(path).use {
            this.write(it)
            this.close()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun XSSFWorkbook.getListSheet() = this.asIterable().map { it as XSSFSheet }.toList()

fun XSSFSheet.getListRow(startIndex: Int = 0) = this.asIterable()
        .filter { it.rowNum >= startIndex }
        .map { it as XSSFRow }
        .toList()

fun XSSFSheet.createLastRow(): XSSFRow {
    return this.createRow(this.lastRowNum + 1)
}

fun XSSFRow.getListCell() = this.asIterable()
        .map { it as XSSFCell }
        .toList()

fun XSSFRow.createLastCell(): XSSFCell {
    return if (this.lastCellNum.toInt() <= 0) this.createCell(this.lastCellNum.toInt() + 1)
    else this.createCell(this.lastCellNum.toInt())
}

fun XSSFCell.getCellValue(): String {
    this.setCellType(CellType.STRING)
    return this.stringCellValue
}

fun XSSFRow.setNextCellValue(value: String): XSSFRow{
    this.createLastCell().setCellValue(value)
    return this
}