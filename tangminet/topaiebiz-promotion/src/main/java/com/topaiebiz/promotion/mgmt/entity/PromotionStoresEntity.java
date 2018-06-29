package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@TableName("t_pro_promotion_stores")
@Data
public class PromotionStoresEntity extends BaseBizEntity<Long> {
    /**
     * id
     * 全局唯一标识符。12270613
     */
    private Long id;

    /**
     * promotionId
     * 营销活动ID。
     */
    private Long promotionId;

    /**
     * storeId
     * 商品所属店铺。
     */
    private Long storeId;

    /**
     * name
     * 店铺名称。
     */
    private String name;

    /**
     * merchantName
     * 公司名称。
     */
    private String merchantName;

    /**
     * 入驻时间
     */
    private Date entryTime;

    /**
     * discountType
     * 优惠类型（1.折扣  2减价）。
     */
    private Integer discountType;

    /**
     * discountValue
     * 优惠值。折扣直接写小数。减价写价钱  包邮则不写。
     */
    private BigDecimal discountValue;

    /**
     * giveawayGoods
     * 优惠赠品。没有可不填。
     */
    private Long giveawayGoods;

    /**
     * platformPrice
     * 平台补贴金额，根据补贴比例算出来。
     */
    private BigDecimal platformPrice;

    /**
     * state
     * 状态。1 已通过  2 未通过   0未审核
     */
    private Integer state;

    /**
     * memo
     * 备注
     */
    private String memo;

    /**
     * creatorId
     * 创建人编号。取值为创建人的全局唯一主键标识符。
     */
    private Long creatorId;

    /**
     * createdTime
     * 创建时间。取值为系统的当前时间。
     */
    private Date createdTime;

    /**
     * lastModifierId
     * 最后修改人编号。取值为最后修改人的全局唯一主键标识符。
     */
    private Long lastModifierId;

    /**
     * lastModifiedTime
     * 最后修改时间。取值为系统的当前时间。
     */
    private Date lastModifiedTime;

    /**
     * 是否为发布进行中老数据 0-不是  1-是
     */
    private Byte isReleaseData;


}