package com.topaiebiz.promotion.common.util;

import com.topaiebiz.promotion.mgmt.component.ExcelRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;

public class ReadExcelUtil {
    private static final String EXTENSION_XLS = "xls";
    private static final String EXTENSION_XLSX = "xlsx";
    private static ExcelRow excelRow;

    private static void setExcelRow(ExcelRow row) {
        excelRow = row;
    }

    /***
     * <pre>
     * 取得Workbook对象(xls和xlsx对象不同,不过都是Workbook的实现类)
     *   xls:HSSFWorkbook
     *   xlsx：XSSFWorkbook
     * @param file
     * @return
     * @throws IOException
     * </pre>
     * @throws InvalidFormatException
     */
    private static Workbook getWorkbook(MultipartFile file) throws IOException, InvalidFormatException {
        Workbook workbook = null;
        InputStream is = file.getInputStream();
        if (!is.markSupported()) {
            //回推
            is = new PushbackInputStream(is, 8);
        }

        String fileName = file.getOriginalFilename();
        if (fileName.endsWith(EXTENSION_XLS)) {
            workbook = new HSSFWorkbook(is);
        } else if (fileName.endsWith(EXTENSION_XLSX)) {
            //获取文件读写权限
            workbook = new XSSFWorkbook(OPCPackage.open(is));
        }
        return workbook;
    }

    /**
     * 读取excel文件内容
     *
     * @param file
     * @throws Exception
     */
    public static <T> List<T> readList(MultipartFile file, ExcelRow<T> row) throws Exception {
        //注入实现类
        setExcelRow(row);
        //获取workbook对象
        Workbook workbook = null;
        List<T> list = new ArrayList<>();
        //sheet异常
        int sheetException = 0;
        int rowException = 0;
        try {
            workbook = getWorkbook(file);
            // 读文件 一个sheet一个sheet地读取
            for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
                Sheet sheet = workbook.getSheetAt(numSheet);
                if (sheet == null) {
                    continue;
                }

                int firstRowIndex = sheet.getFirstRowNum();
                int lastRowIndex = sheet.getLastRowNum();

                // 读取数据行
                for (int rowIndex = firstRowIndex + 1; rowIndex <= lastRowIndex; rowIndex++) {
                    Row currentRow = sheet.getRow(rowIndex);// 当前行
                    if (currentRow == null) {
                        break;
                    }
                    //异常数据
                    sheetException = numSheet + 1;
                    rowException = rowIndex + 1;
                    T t = (T) excelRow.readRow(currentRow);
                    list.add(t);
                }
            }
        } catch (Exception e) {
            //提示错误
            throw new Exception("第" + sheetException + "个sheet，第" + rowException + "行数据格式不正确！错误信息：" + e.getMessage());
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    /**
     * 取单元格的值
     *
     * @param cell       单元格对象
     * @param treatAsStr 为true时，当做文本来取值 (取到的是文本，不会把“1”取成“1.0”)
     * @return
     */
    public static String getCellValue(Cell cell, boolean treatAsStr) {
        if (cell == null) {
            return "";
        }

        if (treatAsStr) {
            // 虽然excel中设置的都是文本，但是数字文本还被读错，如“1”取成“1.0”
            // 加上下面这句，临时把它当做文本来读取
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }

        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return String.valueOf(cell.getStringCellValue());
        }
    }

}