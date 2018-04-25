import DAO.Excel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook


fun main(args: Array<String>) {

    val wb = XSSFWorkbook()
    val sheet = wb.createSheet()
    getListWorkbook(PATCH_IN)
            .first()
            .getListSheet()
            .first()
            .getListRow(1)
            .map {
                Pair(it.getListCell()[0].getCellValue(), it.getListCell()[1].getCellValue())
            }.distinct()
            .forEach {
                val r = sheet.createLastRow()
                r.createLastCell().setCellValue(it.first)
                r.createLastCell().setCellValue(it.second)
            }
    wb.writeToFile(PATCH_OUT, FILE_NAME_OUT)

}



