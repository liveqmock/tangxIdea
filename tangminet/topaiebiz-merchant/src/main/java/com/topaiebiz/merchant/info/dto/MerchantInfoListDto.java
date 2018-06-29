package com.topaiebiz.merchant.info.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;

/**
 * Description: 商家管理--商家信息列表分页检索dto类
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年10月28日 下午8:46:30
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantInfoListDto extends PagePO implements Serializable {

    /**
     * 全局唯一标识符
     */
    private Long id;

    /**
     * 连锁店、直营店等暂定
     */
    private Integer merchantType;

    /**
     * 商家联系人姓名
     */
    private String contactName;

    /**
     * 公司名称
     */
    private String name;

    /**
     * 公司所在地
     */
    private String address;

    /**
     * 联系人手机号
     */
    private String contactTele;

    /**
     * 所需积分下限，达到该值就是该等级
     */
    private Long integralValue;


    /**
     * 商家等级名称
     */
    private String gradeName;

    /**
     * 商家状态
     */
    private String state;

    /**
     * 变更状态
     */
    private Long changeState;

    /**
     * 商家id
     */
    private Long merchantId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 商家等级积分
     */
    private Long gradeIntegral;


}
