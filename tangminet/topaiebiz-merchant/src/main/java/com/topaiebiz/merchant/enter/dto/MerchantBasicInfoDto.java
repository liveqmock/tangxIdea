package com.topaiebiz.merchant.enter.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description: 商家入驻流程--基本信息填写（公司及联系人信息）信息的dto
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年10月18日 下午4:40:37
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantBasicInfoDto implements Serializable {

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
    @NotNull(message = "{validation.merchantInfo.name}")
    private String name;

    /**
     * 公司所在地
     */
    @NotNull(message = "{validation.qualification.districtId}")
    private String districtId;

    /**
     * 公司详细地址
     */
    @NotNull(message = "{validation.qualification.address}")
    private String address;

    /**
     * 门店数量
     */
    @NotNull(message = "{validation.qualification.storeNumber}")
    private Long storeNumber;

    /**
     * 公司电话
     */
    @NotNull(message = "{validation.qualification.telephone}")
    private String telephone;

    /**
     * 员工总数
     */
    @NotNull(message = "{validation.qualification.staffNo}")
    private Long staffNo;

    /**
     * 注册资金，单位万元
     */
    @NotNull(message = "{validation.qualification.capital}")
    private String capital;

    /**
     * 联系人电话
     */
    @NotNull(message = "{validation.merchantInfo.contactTele}")
    @Length(min = 11, max = 11)
    private String contactTele;

    /**
     * 联系人身份证号
     */
    @NotNull(message = "{validation.qualification.idCard}")
    private String idCard;

    /**
     * 电子邮箱
     */
    @NotNull(message = "{validation.qualification.email}")
    @Email
    private String email;

    /**
     * 联系人身份证号电子版
     */
    @NotNull(message = "{validation.qualification.idCardImage}")
    private String idCardImage;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺log
     */
    private String imgages;

    /**
     * 备注
     */
    private String memo;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 等级id
     */
    private Long merchantGradeId;

    /**
     * 商家等级积分。
     */
    private Long gradeIntegral;

    /**
     * 商户类型
     */
    private Long merchantType;

    /**
     * 店铺积分
     */
    private Long integral;

    /**
     * 商家变更状态
     */
    private Integer changeState;

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
