package com.topaiebiz.decorate.component;

import lombok.Data;

import java.util.List;

/**
 * 营销活动VO
 *
 * @author huzhenjia
 * @since 2018/03/30
 */
@Data
public class ActivityVO {

    private String notReceiveImage;//未领取图片

    private String receivedImage;//已领取图片

    private String broughtOutImage;//已领完图片

    private List<CouponVO> couponVOS;
}
