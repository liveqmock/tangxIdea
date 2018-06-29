package com.topaiebiz.giftcard.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 礼卡标签
 */
@TableName("t_giftcard_label")
@Data
public class GiftcardLabel implements Serializable{

	/**
	 * id
	 * 标签id
	 */
	private Long id;

	/**
	 * label_name
	 * 标签名称
	 */
	private String labelName;

	/**
	 * sample_pic
	 * 样本图片
	 */
	private String samplePic;

	/**
	 * remark
	 * 备注
	 */
	private String remark;

	/**
	 * del_flag
	 * 0未删除 1删除
	 */
	private Integer delFlag;

	/**
	 * creator
	 * 创建者
	 */
	private String creator;

	/**
	 * created_time
	 * 添加时间
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
