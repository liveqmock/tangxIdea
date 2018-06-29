package com.topaiebiz.promotion.mgmt.dto.box;

import com.topaiebiz.promotion.mgmt.dto.box.content.CardBoxDTO;
import com.topaiebiz.promotion.mgmt.dto.box.content.CouponBoxDTO;
import com.topaiebiz.promotion.mgmt.dto.box.content.ResBoxDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 中奖记录(C端)
 */
@Data
public class AwardRecordDTO implements Serializable{
    private static final long serialVersionUID = -4905851119182785687L;
    /**
     * 中奖宝箱ID
     */
    private Long id;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 会员姓名
     */
    private String memberName;

    /**
     * 活动名称
     */
    private String promotionName;

    /**
     * 中奖时间
     */
    private Date createdTime;

    /**
     * 奖品名称
     */
    private String awardName;

    /**
     * 奖品类型（1-优惠券，2-美礼卡，3-实物奖）
     */
    private Integer awardType;

    /**
     * 宝箱领取状态。（0 未领取，1 已领取）
     */
    private Integer state;
    /**
     * 优惠券宝箱内容
     */
    private CouponBoxDTO couponBox;
    /**
     * 美礼卡宝箱内容
     */
    private CardBoxDTO cardBox;
    /**
     * 实物宝箱内容
     */
    private ResBoxDTO resBox;
}