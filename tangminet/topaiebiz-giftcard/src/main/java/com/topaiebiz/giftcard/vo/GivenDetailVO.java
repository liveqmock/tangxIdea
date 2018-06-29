package com.topaiebiz.giftcard.vo;

import lombok.Data;

/**
 * @description: 转赠领取详情
 * @author: Jeff Chen
 * @date: created in 上午10:24 2018/1/20
 */
@Data
public class GivenDetailVO {

    /**
     * 转赠链接id
     */
    private String linkId;
    /**
     * 转赠人名称
     */
    private String memberName;
    /*** 小会员头像。*/
    private String smallIcon;
    /**
     * 赠言
     */
    private String note;
    /**
     * 转赠礼卡封面
     */
    private String cover;

}
