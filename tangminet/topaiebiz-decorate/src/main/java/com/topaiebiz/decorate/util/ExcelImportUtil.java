package com.topaiebiz.decorate.util;

public class ExcelImportUtil {

    //验证是否是2003的excel
    public static boolean is2003Excel(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    //验证是否是2007的excel
    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    //校验excel文件
    public static boolean validateExcel(String filePath) {
        if (filePath == null || !(is2003Excel(filePath) || isExcel2007(filePath))) {
            return false;
        }
        return true;
    }
}
