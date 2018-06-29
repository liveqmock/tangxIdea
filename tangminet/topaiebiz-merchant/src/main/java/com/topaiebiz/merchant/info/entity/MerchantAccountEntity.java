package com.topaiebiz.merchant.info.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Description: 商家账户信息实体类
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年10月2日 下午7:50:56
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@TableName("t_mer_merchant_account")
public class MerchantAccountEntity extends BaseBizEntity<Long> {

    /**
     * 版本序列化
     */
    private static final long serialVersionUID = 4595016165430217561L;

    /**
     * 所属商家
     */
    private Long merchantId;

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
     * 开户行所在区域
     */
    private Long accountDistrictId;

    /**
     * 是否为结算账号
     */
    private Integer isSettle;

    /**
     * 开户银行许可证电子版
     */
    private String electronicImage;

    /**
     * 预留电话
     */
    private String telephone;

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

}
