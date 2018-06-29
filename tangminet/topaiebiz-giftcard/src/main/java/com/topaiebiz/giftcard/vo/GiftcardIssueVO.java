package com.topaiebiz.giftcard.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: 礼卡批次信息
 * @author: Jeff Chen
 * @date: created in 上午9:23 2018/1/16
 */
@Data
public class GiftcardIssueVO implements Serializable {

    /**
     * 发行id
     */
    private Long batchId;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 卡片名称
     */
    @NotEmpty(message = "礼卡名称不能为空")
    private String cardName;

    /**
     * 副标题
     */
    private String subtitle;

    /**
     * 0实体卡 4电子卡
     */
    @NotNull(message = "介质类型不能为空")
    private Integer medium;

    /**
     * 标签id：参考t_giftcard_label
     */
    private Long labelId;

    /**
     * 标签名称
     */
    private String labelName;
    /**
     * 卡号前缀
     */
    @NotEmpty(message = "卡号前缀要指定")
    @Length(min = 4,max = 4,message = "卡号前缀为4位")
    private String prefix;
    /**
     * 面值
     */
    @NotNull(message = "面值不能为空")
    private BigDecimal faceValue;

    /**
     * 售价
     */
    @NotNull(message = "售价不能为空")
    private BigDecimal salePrice;

    /**
     * 适用范围：1 全部平台 2部分店铺可用 3 部分店铺不可用
     */
    @NotNull(message = "应用范围不能为空")
    private Integer applyScope;

    /**
     * 店铺id
     */
    private String storeIds;

    /**
     * 卡属性：1 普通卡 2 联名卡 3 活动卡
     * card_attr
     */
    private Integer cardAttr;

    /**
     * 贴现总额
     */
    private BigDecimal discountAmount;

    /**
     * 平台贴现
     */
    private BigDecimal platformDiscount;

    /**
     * 店铺贴现
     */
    private BigDecimal storeDiscount;

    /**
     * 是否可转赠：0否 1是
     */
    @NotNull(message = "指定是否可以转赠")
    private Integer givenFlag;

    /**
     * 发行数量
     */
    @NotNull(message = "发行数量不能为空")
    private Integer issueNum;

    /**
     * 电子卡售出数量，实体卡生产数量
     */
    private Integer outNum;

    /**
     * 有效天数
     */
    @NotNull(message = "有效天数不能为空")
    private Integer validDays;

    /**
     * 限购数量：-1不限制
     */
    @NotNull(message = "限购数量不能为空")
    private Integer limitNum;

    /**
     * 封面图片url
     */
    @NotEmpty(message = "封面不能为空")
    private String cover;

    /**
     * 卡描述
     */
    @NotEmpty(message = "卡描述不能为空")
    private String spec;

    /**
     * 状态：0-待审核，1-审核通过（待上架/待生产），2-未通过，3-上架，4-待入库，5-已入库，6-已出库
     */
    private Integer issueStatus;

    /**
     * 从1-10结算优先级逐级提升，即优先级高的美礼卡先结算
     */
    @NotNull(message = "优先级不能为空")
    private Integer priority;
    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 卡号起始
     */
    @NotNull(message = "卡号起始不能为空")
    private Long noStart;
    /**
     * 卡号结束
     */
    @NotNull(message = "卡号结束不能为空")
    private Long noEnd;
    /**
     * 操作日志列表
     */
    List<CardOpLogVO> opLogList;

    /**
     * 使用情况
     */
    CardBatchUsageVO usageVO;

    /**
     * 商品id列表
     */
    private List<Long> goodsIds;
}
