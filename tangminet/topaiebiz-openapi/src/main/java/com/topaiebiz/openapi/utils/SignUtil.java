package com.topaiebiz.openapi.utils;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.openapi.contants.OpenApiContants;
import com.topaiebiz.openapi.dto.RequestParamDTO;
import com.topaiebiz.openapi.entity.OpenApiStoreResourceEntity;
import com.topaiebiz.openapi.enumdata.ApiMethodEnum;
import com.topaiebiz.openapi.exception.OpenApiExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/2 13:12
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Slf4j
@Component
public class SignUtil {

    private static final String EQUALS = "=";

    private static final String AND = "&";

    private static final String SIGN = "sign";

    @Autowired
    private StoreResourceUtil storeResourceUtil;

    /**
     * Description: 生成签名
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/1
     *
     * @param:
     **/
    public static String generateSign(Map<String, String> paramMap, String appSecret) {
        Set<String> keySet = paramMap.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(SIGN)) {
                continue;
            }
            // 参数值为空，则不参与签名
            if (paramMap.get(k).trim().length() > 0) {
                sb.append(k).append(EQUALS).append(paramMap.get(k).trim()).append(AND);
            }
        }
        String waitSignStr = StringUtils.join(appSecret, AND, sb.toString(), appSecret);
        String sign = parseStrToMd5U32(waitSignStr);
        log.info("----------create sign, params:{}, sign:{}", waitSignStr, sign);
        return sign;
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("appId", "hxpeng1234567");
        map.put("method", "mmg.order.express.add");
//        map.put("method", "mmg.order.page.query");
//        map.put("method", "mmg.good.stock.update");
        map.put("params", "{'expressNo':'3101624653800','expressCompanyCode':'yunda','mmgOrderId':993687517740179457}");
        map.put("timestamp", "2018-05-08 10:56:10");
        map.put("version", OpenApiContants.OrderCreateVersion.VERSION_ONE);
        map.put("sign", generateSign(map, "OxTNiiS9PjlWIDD1KEgU71ZjZQHNxh"));
        System.out.println(JSON.toJSONString(map));
    }


    /**
     * Description: 验证签名
     * <p>
     * Author: hxpeng
     * createTime: 2018/3/2
     *
     * @param:
     **/
    private static boolean validSign(Map<String, String> paramMap, String appSecret) {
        if (!paramMap.containsKey(SIGN)) {
            return false;
        }

        String sign = paramMap.get(SIGN);
        return generateSign(paramMap, appSecret).equals(sign);
    }


    public void valid(RequestParamDTO requestParamDTO) {
        String version = requestParamDTO.getVersion();
        if (StringUtils.isBlank(version)) {
            throw new GlobalException(OpenApiExceptionEnum.VERSION_CANT_BE_NULL);
        }
        String method = requestParamDTO.getMethod();
        if (StringUtils.isBlank(method)) {
            throw new GlobalException(OpenApiExceptionEnum.METHOD_CANT_BE_NULL);
        }
        if (!ApiMethodEnum.contains(method)) {
            throw new GlobalException(OpenApiExceptionEnum.METHOD_NAME_IS_ILLEGAL);
        }
        if (StringUtils.isBlank(requestParamDTO.getTimestamp())) {
            throw new GlobalException(OpenApiExceptionEnum.TIMESTAMP_CANT_BE_NULL);
        }
        if (StringUtils.isBlank(requestParamDTO.getSign())) {
            throw new GlobalException(OpenApiExceptionEnum.SIGN_CANT_BE_NULL);
        }
        String appId = requestParamDTO.getAppId();
        if (StringUtils.isBlank(appId)) {
            throw new GlobalException(OpenApiExceptionEnum.APP_ID_CANT_BE_NULL);
        }
        OpenApiStoreResourceEntity storeResourceEntity = storeResourceUtil.getByAppId(appId);
        requestParamDTO.setStoreId(storeResourceEntity.getStoreId());
        String appSecret = storeResourceEntity.getAppSecret();

        Map<String, String> map = new HashMap<>();
        map.put("appId", appId);
        map.put("method", method);
        map.put("params", requestParamDTO.getParams());
        map.put("timestamp", requestParamDTO.getTimestamp());
        map.put("sign", requestParamDTO.getSign());
        map.put("version", requestParamDTO.getVersion());
        if (!validSign(map, appSecret)) {
            throw new GlobalException(OpenApiExceptionEnum.VERIFY_SIGNATURE_FAILURE);
        }
    }

    /**
     * 32位小写MD5
     *
     * @param str
     * @return
     */
    private static String parseStrToMd5L32(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                int bt = b & 0xff;
                if (bt < 16) {
                    sb.append(0);
                }
                sb.append(Integer.toHexString(bt));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error(".parseStrToMd5L32() Exception={},param={}", e, str,
                    e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 32位大写MD5
     *
     * @param str
     * @return
     */
    private static String parseStrToMd5U32(String str) {
        return parseStrToMd5L32(str).toUpperCase();
    }


    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }
}
