package com.topaiebiz.giftcard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description: 导出工具
 * @author: Jeff Chen
 * @date: created in 下午8:27 2018/1/22
 */
public class ExportUtil {
    private static Logger logger = LoggerFactory.getLogger(ExportUtil.class);
    /**
     * CSV文件列分隔符
     */
    private static final String CSV_COLUMN_SEPARATOR = ",";

    /**
     * CSV文件列分隔符
     */
    private static final String CSV_RN = "\r\n";

    public static boolean doExport(List<Map<String, Object>> dataList, String colNames, String mapKey, OutputStream os) {
        try {
            StringBuffer buf = new StringBuffer();
            buf.append(new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}));
            String[] colNamesArr = colNames.split(CSV_COLUMN_SEPARATOR);
            String[] mapKeyArr =mapKey.split(CSV_COLUMN_SEPARATOR);

            // 完成数据csv文件的封装
            // 输出列头
            for (int i = 0; i < colNamesArr.length; i++) {
                if (i < colNamesArr.length - 1) {
                    buf.append(colNamesArr[i]).append(CSV_COLUMN_SEPARATOR);
                }else {
                    buf.append(colNamesArr[i]);
                }
            }
            buf.append(CSV_RN);
            // 输出数据
            if (null != dataList) {
                for (int i = 0; i < dataList.size(); i++) {
                    for (int j = 0; j < mapKeyArr.length; j++) {
                        if (j < mapKeyArr.length - 1) {
                            buf.append(dataList.get(i).get(mapKeyArr[j])).append(CSV_COLUMN_SEPARATOR);
                        } else {
                            buf.append(dataList.get(i).get(mapKeyArr[j]));
                        }
                    }
                    buf.append(CSV_RN);
                }
            }
            os.write(buf.toString().getBytes("UTF-8"));
            os.flush();
            return true;
        } catch (Exception e) {
            logger.error("导出文件错误",e);
        }
        return false;
    }

    public static void setRespProperties(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
        // 设置文件后缀
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fn = fileName + sdf.format(new Date()).toString() + ".csv";
        // 读取字符编码
        String utf = "UTF-8";

        // 设置响应
        response.setContentType("text/csv");
        response.setCharacterEncoding(utf);
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "max-age=30");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fn, utf));
    }
}
