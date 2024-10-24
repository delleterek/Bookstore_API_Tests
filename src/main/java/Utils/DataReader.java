package Utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataReader {
    public Object[][] readExcelData(String excelPath, String sheetName){
        List<Object[]> data = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(excelPath);
             XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream))
        {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            Iterator<Row> rowIterator = sheet.iterator();
            Row headerRow = rowIterator.hasNext() ? rowIterator.next() : null;
            int numberOfColumns = headerRow == null ? 0 : headerRow.getLastCellNum();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                List<Object> rowData = new ArrayList<>();

                for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
                    Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue = "";
                    switch (cell.getCellType()){
                        case STRING:
                            cellValue = cell.getStringCellValue();
                            break;
                        case BOOLEAN:
                            cellValue = String.valueOf(cell.getBooleanCellValue());
                            break;
                        case NUMERIC:
                            cell.setCellType(CellType.STRING);
                            cellValue = cell.getStringCellValue();
                            break;
                        case BLANK:
                            break;
                    }
                    rowData.add(cellValue);
                }
                data.add(rowData.toArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toArray(new Object[0][]);
    }
}
