package com.nebulapaas.common;

import org.apache.http.client.fluent.Request;

/***
 * @author yfeng
 * @date 2018-02-27 9:01
 */
public class HttpUtil {

    /**
     * 获取图片内容
     *
     * @param imgUrl 图片地址
     * @return 内容字节数组
     * @throws Exception
     */
    private static byte[] getImageContent(String imgUrl) throws Exception {
        int timeout = 3000;
        return Request.Get(imgUrl).connectTimeout(timeout).socketTimeout(timeout).execute().returnContent().asBytes();
    }
}
