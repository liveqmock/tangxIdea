package com.topaiebiz.giftcard.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 礼卡轮播图
 */
@TableName("t_giftcard_carousel")
@Data
public class GiftcardCarousel implements Serializable{

	private Long id;

	/**
	 * img_url
	 * 图片url
	 */
	private String imgUrl;

	/**
	 * link_url
	 * 跳转url
	 */
	private String linkUrl;

	private String title;

	private Integer type;
	/**
	 * creator
	 * 创建者
	 */
	private String creator;

	/**
	 * created_time
	 * 创建时间
	 */
	private Date createdTime;

	/**
	 * modifier
	 * 修改者
	 */
	private String modifier;

	/**
	 * modified_time
	 * 修改时间
	 */
	private Date modifiedTime;
}
