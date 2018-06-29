package com.topaiebiz.giftcard.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

/**
 * @description: 轮播
 * @author: Jeff Chen
 * @date: created in 下午8:27 2018/1/17
 */
@Data
public class GiftcardCarouselVO implements Serializable{

    /**
     * 主键id
     */
    private Long cId;
    /**
     * img_url
     * 图片url
     */
    @NotEmpty(message = "图片不能为空")
    private String imgUrl;

    @NotEmpty(message = "标题不能为空")
    private String title;

    /**
     * 类型
     */
    @Range(min = 1,max = 3,message = "类型1-3之间")
    private Integer type;

    private String linkUrl;
}
