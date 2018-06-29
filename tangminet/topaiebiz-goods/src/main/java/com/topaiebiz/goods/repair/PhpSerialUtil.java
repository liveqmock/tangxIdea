package com.topaiebiz.goods.repair;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Stopwatch;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/***
 * @author yfeng
 * @date 2018-02-08 19:55
 */
@Slf4j
public class PhpSerialUtil {

    private static String phpJsonHost = "http://114.55.252.85:9504/";
    private static Charset utf8 = Charset.forName("UTF-8");
    private static int timeout = 3000;
    private static String empty = " ";

    private static final Cache<String, String> phpSerialCache = CacheBuilder.newBuilder()
            .initialCapacity(50000)
            .concurrencyLevel(8)
            .expireAfterWrite(4, TimeUnit.HOURS)
            .build();

    public static void main(String[] args) {
        String str = "{\"6910\":\"\\u6d45\\u84dd\",\"4543\":\"\\u5747\\u7801\\uff08\\u5934\\u56f448-50cm\\uff09\"}";
        for (int i = 0; i < 1000; i++) {
            System.out.println(transformJSON(str));
        }
    }

    public static String php2Json(String str) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            String result = doPhp2JSON(str);
            return result;
        } finally {
            //log.info("serial spend {} ms with content : {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), str);
        }
    }

    public static String transformJSON(String str) {
       /* LinkedHashMap jsonObj = JSON.parseObject(str,new TypeReference<LinkedHashMap<Long,String>>(){});
        return JSON.toJSONString(jsonObj);*/
       return str;
    }

    private static String doPhp2JSON(String str) {
        if (StringUtils.isBlank(str)) {
            return empty;
        }
        if ("N;".equals(str)) {
            return empty;
        }
        //若是Json字符串，直接返回
        try {
            Object jsonObj = JSON.parse(str);
            //log.info("{} 是JSON串，不需要解序列化", str);
            return str;
        } catch (Exception ex) {
            //log.info("{} 不是JSON串，需要解序列化", str);
        }

        String cacheVal = phpSerialCache.getIfPresent(str);
        if (cacheVal != null) {
            return cacheVal;
        }

        try {
            String content = Request.Post(phpJsonHost)
                    .bodyForm(Form.form()
                            .add("str", str)
                            .build(), utf8)
                    .connectTimeout(timeout)
                    .socketTimeout(timeout)
                    .execute()
                    .returnContent()
                    .asString(utf8);
            if (StringUtils.isBlank(content)) {
                return empty;
            }
            String result = transformJSON(content);
            phpSerialCache.put(str, result);
            return result;
        } catch (Exception ex) {
            log.error("{} 接序列化失败", str);
            return "";
        }
    }
}