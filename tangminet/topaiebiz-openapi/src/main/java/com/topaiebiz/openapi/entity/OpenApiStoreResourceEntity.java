package com.topaiebiz.openapi.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/3/6 15:56
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("t_openapi_store_resource")
public class OpenApiStoreResourceEntity extends BaseBizEntity<Long> {

    private static final long serialVersionUID = -382989555688088827L;

    private Long storeId;

    private String appId;

    private String appSecret;

    private String orderCreateUrl;

    private String orderRefundUrl;

    /**
     * 推单版本控制
     */
    private String orderCreateVersion;

    /**
     * 是否需要报关
     */
    private Integer needPushCustom;

    public OpenApiStoreResourceEntity(String appId, Byte deletedFlag) {
        this.appId = appId;
        this.setDeleteFlag(deletedFlag);
        this.cleanInit();
    }
}
