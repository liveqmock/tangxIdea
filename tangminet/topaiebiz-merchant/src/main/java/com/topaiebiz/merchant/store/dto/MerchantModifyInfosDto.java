package com.topaiebiz.merchant.store.dto;

import com.nebulapaas.base.po.PagePO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Aurthor:zhaoxupeng
 * @Description:
 * @Date 2018/3/30 0030 下午 2:22
 */
@Data
public class MerchantModifyInfosDto extends PagePO implements Serializable {

    /**
     * 商家id
     */
    private Long merchantId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 再次审核状态 0 审核通过，1审核待审核，2审核未通过
     */
    private Integer status;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 公司名称
     */
    private String merchantName;


    /**
     * 公司所在地
     */
    private Long districtId;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactTele;
    /**
     * 商家等级id
     */
    private Long merchantGradeId;
    /**
     * 商家等级
     */
    private String gradeName;
    /**
     * 提交时间(创建时间）
     */
    private String createdTime;

    /**
     * 用于搜索 提交时间
     */
    private String beginCreatedTime;
    /**
     * 审核人
     */
    private String examineAuditor;
    /**
     * 审核时间
     */
    private String examineTime;
    /**
     * 审核时间 用于搜索
     */
    private String beginExamineTime;

    /**
     * 商家等级积分
     */
    private Long gradeIntegral;

    /**
     * 省市县
     */
    private String serialName;

    /**
     * 审核结果
     */
    private Integer auditResult;

    /**
     * 再次审核id
     */
    private Long modifyId;


}
