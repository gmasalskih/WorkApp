import DAO.Web.WebHelper.RestApi
import Util.Const.PATCH_IN
import io.reactivex.Observable
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.HashMap


val wb = getWB(PATCH_IN)
val listRow = getListRow(wb)
val wbOUT = XSSFWorkbook()
val sheet = wbOUT.createSheet()

fun main(args: Array<String>) {
    init()
//    App.go()
//    start()
    listRow.forEach {
        println("${it.rowNum} - ${it.getCell(0).stringCellValue} - ${getIdGroup(it.getCell(2).stringCellValue)}")
    }
    Observable.fromIterable(listRow)
            .blockingSubscribe { row ->

                var fullName = row.getCell(0).stringCellValue.split(" ")
                var map = HashMap<String, String>()
                map.put("plastname", fullName[0])
                map.put("pfirstname", fullName[1])
                map.put("psurname", fullName[2])
                map.put("rspostidname", row.getCell(1).stringCellValue)
                map.put("pstatus", "0")
                map.put("isuser", "1")



                RestApi.webApi.getUsers(map)
                        .flatMap { Observable.fromIterable(it) }
                        .filter { it.rspostidname == "ПМ" || it.rspostidname == "Бизнес-менеджер" }
                        .blockingSubscribe {
                            var r =sheet.createRow(sheet.lastRowNum + 1)
                            r.createCell(r.lastCellNum + 1).setCellValue(row.getCell(0).stringCellValue)
                            r.createCell(r.lastCellNum.toInt()).setCellValue(row.getCell(1).stringCellValue)
                            r.createCell(r.lastCellNum.toInt()).setCellValue(row.getCell(2).stringCellValue)
                            r.createCell(r.lastCellNum.toInt()).setCellValue(getIdGroup(row.getCell(2).stringCellValue))
                            r.createCell(r.lastCellNum.toInt()).setCellValue(it.personid)
                            r.createCell(r.lastCellNum.toInt()).setCellValue("${it.plastname} ${it.pfirstname} ${it.psurname}")
                            r.createCell(r.lastCellNum.toInt()).setCellValue(it.rspostidname)
                            RestApi.webApi.addPersonToGroup(getIdGroup(row.getCell(2).stringCellValue), it.personid.toString())
                                    .blockingSubscribe {
                                        println(it)
                                    }
                        }

            }
    writeWB(wbOUT)

}

private fun init() {
    File("./src/main/resources/out/res.xlsx").delete()
}

private fun getWB(patch: String) = File(patch).listFiles()
        .filter { it.absolutePath.contains(".xlsx") }
        .mapNotNull { getWorkbookFromFile(it) }
        .first()

private fun getListRow(wb: XSSFWorkbook): List<XSSFRow> {
    return wb.flatMap { it.asIterable() }
            .filter { it.rowNum > 0 }
            .map { it as XSSFRow }
            .toList()
}


private fun start() {
    val listWB = File("./src/main/resources/in").listFiles()
            .filter { it.absolutePath.contains(".xlsx") }
            .map { getWorkbookFromFile(it) }
            .toList()
    val listSheets = listWB
            .flatMap { it!!.asIterable() }
            .map {
                var list = it.asIterable()
                        .map { it as XSSFRow }
                        .filter { it.rowNum > 1 }
                        .map { my ->
                            Pair(it.sheetName, my)
                        }
                list
            }.flatMap {
                it
            }

    val wb = XSSFWorkbook()
    val sh = wb.createSheet()


    listSheets.forEach { pair ->
        //        val row = sh.createRow(sh.lastRowNum + 1)
//        row.createCell(row.lastCellNum + 1).setCellValue(pair.first)

        val map = HashMap<String, String>()
        map.put("plastname", pair.second.getCell(0).stringCellValue.split(" ")[0])
        map.put("pfirstname", pair.second.getCell(0).stringCellValue.split(" ")[1])
        map.put("rspostidname", "Бизнес-менеджер")
        map.put("isuser", "1")
        map.put("pstatus", "0")


        RestApi.webApi.getUsers(map)
                .filter {
                    it.isNotEmpty()
                }.flatMap {
                    Observable.fromIterable(it)
                }.filter {
                    pair.second.getCell(0).stringCellValue == "${it.plastname} ${it.pfirstname} ${it.psurname}"
                }
                .blockingSubscribe {
                    val row = sh.createRow(sh.lastRowNum + 1)
                    row.createCell(row.lastCellNum + 1).setCellValue(pair.first)
                    row.createCell(row.lastCellNum.toInt()).setCellValue(it.personid)
                    row.createCell(row.lastCellNum.toInt()).setCellValue(it.plastname)
                    row.createCell(row.lastCellNum.toInt()).setCellValue(it.pfirstname)
                    row.createCell(row.lastCellNum.toInt()).setCellValue(it.psurname)
                    row.createCell(row.lastCellNum.toInt()).setCellValue(it.caidname)
                    row.createCell(row.lastCellNum.toInt()).setCellValue(it.rspostidname)
                    println(it)
                    RestApi.webApi.addPersonToGroup("1039", it.personid!!).blockingSubscribe {
                        println(it)
                    }
                }

    }


}

fun writeWB(wb: XSSFWorkbook) {
    try {
        FileOutputStream("./src/main/resources/out/res.xlsx").use {
            wb.write(it)
            wb.close()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

private fun getIdGroup(groupName: String) = when (groupName) {
    "Доп_230 ФЗ" -> "1041"
    "Доп_Кари" -> "1042"
    "Доп_Коробки" -> "1043"
    "Доп_Мобильное приложение" -> "1044"
    "Доп_Мотивация БМ" -> "1045"
    "Доп_Мотивация ПМ" -> "1046"
    "Доп_Оформление" -> "1047"
    "Доп_Сборы" -> "1048"
    "Доп_Совесть" -> "1049"
    else -> ""
}


private fun getWorkbookFromFile(file: File): XSSFWorkbook? {
    return try {
        FileInputStream(file).use {
            XSSFWorkbook(it)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}