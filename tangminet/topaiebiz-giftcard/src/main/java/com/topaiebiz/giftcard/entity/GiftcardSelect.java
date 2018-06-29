package com.topaiebiz.giftcard.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 礼卡精选，用于C端展示
 */
@TableName("t_giftcard_select")
@Data
public class GiftcardSelect implements Serializable{

	/**
	 * id
	 * 专家id
	 */
	private Long id;

	/**
	 * issue_id
	 * 关联发行的卡id
	 */
	private Long batchId;

	private Integer seq;

	/**
	 * del_flag
	 * 0正常 1删除
	 */
	private Integer delFlag;

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
	 * 修改人
	 */
	private String modifier;

	/**
	 * modified_time
	 * 修改时间
	 */
	private Date modifiedTime;
}
