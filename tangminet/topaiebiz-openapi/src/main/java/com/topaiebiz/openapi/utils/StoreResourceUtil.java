package com.topaiebiz.openapi.utils;

import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.openapi.dao.OpenApiStoreResourceDao;
import com.topaiebiz.openapi.entity.OpenApiStoreResourceEntity;
import com.topaiebiz.openapi.exception.OpenApiExceptionEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/6 23:48
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Component
public class StoreResourceUtil {

    @Autowired
    private OpenApiStoreResourceDao openApiStoreResourceDao;



    public OpenApiStoreResourceEntity getByAppId(String appId){
        if (StringUtils.isBlank(appId)){
            throw new GlobalException(OpenApiExceptionEnum.STORE_RESOURCE_IS_NOT_FOUND);
        }

        OpenApiStoreResourceEntity condition = new OpenApiStoreResourceEntity();
        condition.cleanInit();
        condition.setAppId(appId);
        condition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);

        OpenApiStoreResourceEntity entity = openApiStoreResourceDao.selectOne(condition);
        if (null == entity){
            throw new GlobalException(OpenApiExceptionEnum.STORE_RESOURCE_IS_NOT_FOUND);
        }
        return entity;
    }

    public OpenApiStoreResourceEntity getByStoreId(Long storeId){
        if (null == storeId){
            return null;
        }
        OpenApiStoreResourceEntity condition = new OpenApiStoreResourceEntity();
        condition.cleanInit();
        condition.setStoreId(storeId);
        condition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);

        return openApiStoreResourceDao.selectOne(condition);
    }



}
