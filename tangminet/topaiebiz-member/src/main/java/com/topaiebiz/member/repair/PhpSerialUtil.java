package com.topaiebiz.member.repair;

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

    private static String phpJsonHost = "http://101.37.96.211:9504/";
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
        //LinkedHashMap jsonObj = JSON.parseObject(str, new TypeReference
        // <LinkedHashMap<Long, String>>() {
        //});//
        // return JSON.toJSONString(jsonObj);
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
            String result = unicodeToUtf8(content);
            phpSerialCache.put(str, result);
            return result;
        } catch (Exception ex) {
            log.error("{} 接序列化失败", str);
            return "";
        }
    }

    public static String unicodeToUtf8(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }
}