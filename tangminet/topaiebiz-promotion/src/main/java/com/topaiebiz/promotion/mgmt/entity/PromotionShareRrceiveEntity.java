package com.topaiebiz.promotion.mgmt.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_pro_promotion_share_receive")
public class PromotionShareRrceiveEntity extends BaseBizEntity<Long>{
    /**
     * id
     * 全局唯一标识符
     */
    private Long id;

    /**
     * memberId
     * 领取人会员ID。
     */
    private Long memberId;

    /**
     * promotionId
     * 分享活动id。
     */
    private Long promotionId;


    /**
     * shareId
     * 分享活动礼包ID。
     */
    private Long shareId;

    /**
     * receiveDetail
     * 领取内容详情。
     */
    private String receiveDetail;

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



}