package com.topaiebiz.merchant.info.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

/**
 * Description: 商家资质实体类
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年10月2日 下午7:40:32
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@TableName("t_mer_merchant_qualification")
public class MerchantQualificationEntity extends BaseBizEntity<Long> {

    /**
     * 版本序列化
     */
    private static final long serialVersionUID = 5766432792237052428L;

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
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactTele;

    /**
     * 联系人身份证号
     */
    private String idCard;

    /**
     * 联系人身份证号电子版
     */
    private String idCardImage;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 营业执照号
     */
    private String licenseNo;

    /**
     * 营业执照所在区域。
     */
    private Long licenseRegionId;

    /**
     * 营业执照号所在地
     */
    private String licenseLocation;

    /**
     * 营业执照有效期起始
     */
    private Date licenseBegin;

    /**
     * 营业执照有效期结束
     */
    private Date licenseEnd;

    /**
     * 法定经营范围
     */
    private String manageScope;

    /**
     * 营业执照电子版
     */
    private String licenseImage;

    /**
     * 组织机构代码
     */
    private String organCode;

    /**
     * 一般纳税人证明
     */
    private String taxpayerImage;

    /**
     * 税务登记证号
     */
    private String taxRegistNo;

    /**
     * 纳税人识别号
     */
    private String taxpayerNo;

    /**
     * 税务登记证号电子版
     */
    private String taxpayerNoImage;

    /**
     * 1申请，2审核通过 3 审核不通过 4待付款 5已完成
     */
    private Integer state;

    /**
     * 需要支付的金额
     */
    private BigDecimal paymentPrice;

    /**
     * 支付凭证图片
     */
    private String payImage;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 门店数量
     */
    private Long storeNumber;

    private String memo;

    /**
     * 审核时间
     */
    private Date examineTime;

    /**
     * 审核人
     */
    private String examineAuditor;

    /**
     * 公司成立时间
     */
    private Date establishTime;


}
