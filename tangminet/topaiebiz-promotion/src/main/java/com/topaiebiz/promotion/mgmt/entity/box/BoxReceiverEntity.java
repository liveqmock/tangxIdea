package com.topaiebiz.promotion.mgmt.entity.box;

import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

@TableName("t_pro_promotion_box_receiver")
@Data
public class BoxReceiverEntity extends BaseBizEntity<Long> {
    /**
     * 会员ID
     */
    private Long memberId;
    /**
     * 宝箱记录ID
     */
    private Long boxRecordId;
    /**
     * 领奖人姓名
     */
    private String name;
    /**
     * 领奖人手机号
     */
    private String mobile;
    /**
     * 领奖人地址
     */
    private String address;
}
