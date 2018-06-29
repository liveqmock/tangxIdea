package com.nebulapaas.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * @description: 导出工具
 * @author: Jeff Chen
 * @date: created in 下午8:27 2018/1/22
 */
@Slf4j
public class ExportUtil {

    /**
     * CSV文件列分隔符
     */
    private static final String CSV_COLUMN_SEPARATOR = ",";

    /**
     * CSV文件列分隔符
     */
    private static final String CSV_RN = "\r\n";

    /**
     * Description: 评价excel 头部列
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/8
     *
     * @param:
     **/
    public static String buildExcelHeadColumn(String colNames) {
        StringBuilder buf = new StringBuilder();
        String[] colNamesArr = colNames.split(CSV_COLUMN_SEPARATOR);
        for (int i = 0; i < colNamesArr.length; i++) {
            if (i < colNamesArr.length - 1) {
                buf.append(colNamesArr[i]).append(CSV_COLUMN_SEPARATOR);
            } else {
                buf.append(colNamesArr[i]);
            }
        }
        buf.append(CSV_RN);
        return buf.toString();
    }

    /**
     * Description: 根绝class 获取excel正文列
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/8
     *
     * @param:
     **/
    public static <T> String buildExcelBodyColumn(Collection<?> list, Class<?> obj) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        if (CollectionUtils.isEmpty(list) || null == obj) {
            return null;
        }
        for (Object o : list) {
            StringBuilder sb = new StringBuilder();
            Field[] fields = o.getClass().getDeclaredFields();
            for (Field field : fields) {
                addFieldValue(field, o, sb);
            }
            stringBuilder.append(sb.substring(0, sb.length() - 1));
            stringBuilder.append(CSV_RN);
        }
        return stringBuilder.toString();
    }

    private static void addFieldValue(Field field, Object obj, StringBuilder buffer) throws Exception {
        String fieldName = field.getName();
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(obj.getClass(), fieldName);

        Method readMethod = pd.getReadMethod();
        Object value = readMethod.invoke(obj);
        if (value == null) {
            buffer.append(CSV_COLUMN_SEPARATOR);
            return;
        }

        Class valueType = value.getClass();
        if (value instanceof String) {
            value = replaceComma((String) value);
        }
        buffer.append(value);
        if (!valueType.isAssignableFrom(BigDecimal.class) && !valueType.isAssignableFrom(Integer.class)) {
            buffer.append("\t");
        }
        buffer.append(CSV_COLUMN_SEPARATOR);
    }

    public static boolean doExport(String headColumn, String bodyColumn, OutputStream os, String charType) throws IOException {
        return doExport(StringUtils.join(headColumn, CSV_RN, bodyColumn), os, charType);
    }


    public static boolean doExport(String headColumn, String bodyColumn, OutputStream os) throws IOException {
        return doExport(StringUtils.join(headColumn, CSV_RN, bodyColumn), os);
    }

    private static boolean doExport(String outPutStr, OutputStream os) throws IOException {
        return doExport(outPutStr, os, null);
    }

    private static boolean doExport(String outPutStr, OutputStream os, String charType) throws IOException {
        if (StringUtils.isBlank(charType)) {
            charType = "UTF-8";
        }
        os.write(outPutStr.getBytes(charType));
        os.flush();
        return true;
    }

    private static String replaceComma(String str) {
        if (str.contains(CSV_COLUMN_SEPARATOR)) {
            return str.replace(CSV_COLUMN_SEPARATOR, " ");
        }
        return str;
    }


    public static void setRespProperties(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
        // 设置文件后缀
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String fn = StringUtils.join(fileName, sdf.format(new Date()), ".csv");
        // 读取字符编码
        String utf = "UTF-8";

        // 设置响应
        response.setContentType("application/csv;numberformat:@");
        response.setCharacterEncoding(utf);
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "max-age=30");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fn, utf));
    }
}
