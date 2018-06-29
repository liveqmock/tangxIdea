package com.topaiebiz.openapi.utils;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.openapi.contants.OpenApiContants;
import com.topaiebiz.openapi.entity.OpenApiStoreResourceEntity;
import com.topaiebiz.openapi.exception.OpenApiExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ward on 2018-03-01.
 */


@Slf4j
@Component
public class MmgOpenApiUtil {

    @Autowired
    private StoreResourceUtil storeResourceUtil;

    public Map<String, String> buildParams(String jsonParams, Long storeId, String method) {
        OpenApiStoreResourceEntity openApiStoreResourceEntity = storeResourceUtil.getByStoreId(storeId);
        if (openApiStoreResourceEntity == null) {
            throw new GlobalException(OpenApiExceptionEnum.STORE_RESOURCE_IS_NOT_FOUND);
        }

        Map<String, String> map = new HashMap<>();
        map.put("appId", openApiStoreResourceEntity.getAppId());
        map.put("method", method);
        map.put("params", jsonParams);
        map.put("timestamp", SignUtil.getCurrentDate());
        map.put("sign", SignUtil.generateSign(map, openApiStoreResourceEntity.getAppSecret()));
        return map;
    }

    public String postOrderMessage(Long storeId, String params) throws IOException {
        OpenApiStoreResourceEntity openApiStoreResourceEntity = storeResourceUtil.getByStoreId(storeId);
        if (null == openApiStoreResourceEntity) {
            throw new GlobalException(OpenApiExceptionEnum.STORE_RESOURCE_IS_NOT_FOUND);
        }

        Map<String, String> map = new HashMap<>();
        map.put("appId", openApiStoreResourceEntity.getAppId());
        map.put("method", OpenApiContants.Method.ORDER_CREATE_METHOD);
        map.put("params", params);
        map.put("timestamp", SignUtil.getCurrentDate());
        map.put("version", StringUtils.isBlank(openApiStoreResourceEntity.getOrderCreateVersion()) ? OpenApiContants.OrderCreateVersion.VERSION_ONE : openApiStoreResourceEntity.getOrderCreateVersion());
        map.put("sign", SignUtil.generateSign(map, openApiStoreResourceEntity.getAppSecret()));
        return this.doPost(JSON.toJSONString(map), openApiStoreResourceEntity.getOrderCreateUrl());
    }

    private String doPost(String params, String url) throws IOException {
        // 创建HttpPost对象
        HttpPost httpRequst = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(4000).setConnectionRequestTimeout(4000)
                .setSocketTimeout(4000).build();
        httpRequst.setConfig(requestConfig);
        //new UrlEncodedFormEntity(params,"UTF-8")
        StringEntity postEntity = new StringEntity(params, "UTF-8");
        postEntity.setContentType("application/json");
        postEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        httpRequst.setEntity(postEntity);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpclient.execute(httpRequst);
        System.out.println(httpResponse.getStatusLine().getStatusCode());
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity);
    }


}
