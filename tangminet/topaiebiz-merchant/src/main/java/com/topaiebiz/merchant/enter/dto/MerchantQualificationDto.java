package com.topaiebiz.merchant.enter.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.goods.dto.category.backend.BackendCategorysDTO;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;


/**
 * Description: 商家资质Dto类
 * <p>
 * Author : Anthony
 * <p>
 * Date :2017年10月9日 下午1:40:47
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class MerchantQualificationDto extends PagePO implements Serializable {

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
     * 公司所在地
     */
    private String serialName;

    public String getSerialName() {
        return serialName;
    }

    public void setSerialName(String serialName) {
        this.serialName = serialName;
    }

    /**
     * 公司详细地址
     */
    @NotNull(message = "{validation.qualification.address}")
    private String address;

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
     * 类目的List
     */
    private List<BackendCategorysDTO> backendCategorysDtos;


    private StateDto StateDto;

    /**
     * 联系人姓名
     */
    @NotNull(message = "{validation.merchantInfo.contactName}")
    private String contactName;

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
     * 联系人身份证号电子版
     */
    @NotNull(message = "{validation.qualification.idCardImage}")
    private String idCardImage;

    /**
     * 电子邮箱
     */
    @NotNull(message = "{validation.qualification.email}")
    @Email
    private String email;

    /**
     * 营业执照号
     */
    @NotNull(message = "{validation.qualification.licenseNo}")
    private String licenseNo;

    /**
     * 营业执照号所在地
     */
    @NotNull(message = "{validation.qualification.licenseLocation}")
    private String licenseLocation;

    /**
     * 营业执照有效期起始
     */
    @NotNull(message = "{validation.qualification.licenseBegin}")
    private Date licenseBegin;

    /**
     * 营业执照有效期结束
     */
    @NotNull(message = "{validation.qualification.licenseEnd}")
    private Date licenseEnd;

    /**
     * 法定经营范围
     */
    @NotNull(message = "{validation.qualification.manageScope}")
    private String manageScope;

    /**
     * 营业执照电子版
     */
    @NotNull(message = "{validation.qualification.licenseImage}")
    private String licenseImage;

    /**
     * 组织机构代码
     */
    @NotNull(message = "{validation.qualification.organCode}")
    private String organCode;

    /**
     * 一般纳税人证明
     */
    private String taxpayerImage;

    /**
     * 税务登记证号
     */
    @NotNull(message = "{validation.qualification.taxRegistNo}")
    private String taxRegistNo;

    /**
     * 纳税人识别号
     */
    @NotNull(message = "{validation.qualification.taxpayerNo}")
    private String taxpayerNo;

    /**
     * 税务登记证号电子版
     */
    @NotNull(message = "{validation.qualification.taxpayerNoImage}")
    private String taxpayerNoImage;

    /**
     * 1申请，2审核通过 3 审核不通过 4待付款 5已完成
     */
    private Integer state;

    /**
     * 需要支付的金额
     */
    private Double PaymentPrice;

    /**
     * 支付凭证图片
     */
    private String payImage;

    /**
     * 支付时间
     */
    private Date payTime;

    /** 公司名称 */
    // private String name;

    /**
     * 连锁店、直营店等暂定
     */
    @NotNull(message = "{validation.MerchantInfo.merchantType}")
    private Integer merchantType;

    /**
     * 上级商户
     */
    private Long parentMerchant;

    /**
     * 门店log
     */
    private String images;


    /**
     * 店铺的积分。和后期奖惩有关系
     */
    private Long integral;

    /**
     * 商家等级积分
     */
    private Long gradeIntegral;

    /**
     * 商家等级
     */
    private Long merchantGradeId;

    /** 所属商家 */
    // private Long merchantId;

    /**
     * 银行开户名
     */
    @NotNull(message = "${validation.merchantaccount.accountName}")
    private String accountName;

    /**
     * 公司银行帐号
     */
    @NotNull(message = "${validation.merchantaccount.account}")
    private String account;

    /**
     * 开户银行支行名称
     */
    @NotNull(message = "${validation.merchantaccount.bankName}")
    private String bankName;

    /**
     * 支行银联号
     */
    @NotNull(message = "${validation.merchantaccount.bankNum}")
    private String bankNum;

    /**
     * 开户行所在区域
     */
    @NotNull(message = "${validation.merchantaccount.districtId}")
    // private Long districtId;

    /** 是否为结算账号 */
    private Integer isSettle;

    /**
     * 开户银行许可证电子版
     */
    @NotNull(message = "${validation.merchantaccount.electronicImage}")
    private String electronicImage;

    private String merchantName;

    /**
     * 提交时间
     */
    private String createdTime;

    /**
     * 搜索（提交时间）
     */
    private String beginCreateTime;

    /**
     * 审核时间
     */
    private String examineTime;

    /**
     * 审核时间
     */
    private String beginExamineTime;

    /**
     * 审核人
     */
    private String examineAuditor;

    /**
     * 公司成立时间
     */
    private String establishTime;

}
