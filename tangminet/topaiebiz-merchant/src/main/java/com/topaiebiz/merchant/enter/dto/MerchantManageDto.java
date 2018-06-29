package com.topaiebiz.merchant.enter.dto;

import java.io.Serializable;
import java.util.List;

import com.topaiebiz.goods.dto.category.backend.BackendCategorysDTO;
import lombok.Data;

/**
 * Description: 经营类目信息所需dto
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年11月3日 下午1:27:23
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantManageDto implements Serializable {

    /**
     * 商家入驻信息的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。
     */
    private Long id;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 选择店铺类型
     */
    private String merchantType;

    /**
     * 门店log
     */
    private String imgages;

    /**
     * 类目的List
     */
    private List<BackendCategorysDTO> backendCategorysDtos;

    /**
     * 银行账号
     */
    private String settleAccount;
    /**
     * 开户人姓名
     */
    private String settleAccountName;
    /**
     * 开户银行
     */
    private String settleBankName;
    /**
     * 开户行地址
     */
    private Long settleBankDistrictId;
    /**
     * 开户支行
     */
    private String settleBankNum;

    /**
     * 结算周期:月，半月，周，5天
     */
    private String settleCycle;


}
