package com.topaiebiz.merchant.enter.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Description:商家基本信息
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年11月2日 下午11:52:14
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantbasicInfo implements Serializable {

    /**
     * 商家入驻信息的全局唯一主键标识符。本字段是业务无关性的，仅用于关联。
     */
    private Long id;

    /**
     * 商家信息
     */
    private Long merchantId;

    /**
     * 公司名称
     */
    private String name;

    /**
     * 公司所在地
     */
    private String districtId;

    /**
     * 公司详细地址
     */
    private String address;

    /**
     * 门店数量
     */
    private Long storeNumber;

    /**
     * 公司电话
     */
    private String telephone;

    /**
     * 员工总数
     */
    private Long staffNo;

    /**
     * 注册资金，单位万元
     */
    private String capital;

    /**
     * 联系人电话
     */
    private String contactTele;

    /**
     * 联系人身份证号
     */
    private String idCard;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 联系人身份证号电子版
     */
    private String idCardImage;

    /**
     * 营业执照号
     */
    private String licenseNo;

    /**
     * 营业执照电子版
     */
    private String licenseImage;

    /**
     * 营业执照号所在地
     */
    private String licenseLocation;

    /**
     * 营业执照有效期起始
     */
    private String licenseBegin;

    /**
     * 营业执照有效期结束
     */
    private String licenseEnd;

    /**
     * 银行开户名
     */
    private String accountName;

    /**
     * 公司银行帐号
     */
    private String account;

    /**
     * 开户银行支行名称
     */
    private String bankName;

    /**
     * 支行银联号
     */
    private String bankNum;

    /**
     * 开户银行许可证电子版
     */
    private String electronicImage;

    /**
     * 法定经营范围
     */
    private String manageScope;

    /**
     * 营业执照所在区域
     */
    private Long licenseRegionId;

    /**
     * 开户行所在区域
     */
    private String accountDistrictId;

    /**
     * 结算周期:月，半月，周，5天
     */
    private String settleCycle;

    /**
     * 公司成立时间
     */
    private String establishTime;

    /**
     * 联系人姓名
     */
    private String contactName;

}
