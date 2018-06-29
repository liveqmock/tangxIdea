package com.topaiebiz.merchant.dto.store;

import lombok.Data;

import java.io.Serializable;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/3/28 0028 上午 10:25
 */
@Data
public class MerchantAccountDTO implements Serializable {

    private static final long serialVersionUID = -6767105451878130791L;
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
