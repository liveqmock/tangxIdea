package com.topaiebiz.promotion.mgmt.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 首页秒杀
 * Created by 54938 on 2018/2/8.
 */
@Data
public class HomeSeckillDto {

    /**
     * 活动id
     */
    private Long id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 显示标题
     */
    private String showTitle;

    /**
     * 显示类型
     */
    private Integer showType;

    /**
     * 活动开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 当前服务器时间
     */
    private Date nowTime;

    /**
     * 活动状态
     */
    private Integer marketState;

    /**
     * 活动所属商品集合
     */
    private List<HomeSeckillGoodsDTO> promotionGoodsDtos;

}
