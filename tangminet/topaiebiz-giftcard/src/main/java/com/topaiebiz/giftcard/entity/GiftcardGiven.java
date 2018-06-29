package com.topaiebiz.giftcard.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 礼卡转赠记录
 */
@TableName("t_giftcard_given")
@Data
public class GiftcardGiven implements Serializable {

	/**
	 * id
	 * 转赠记录id
	 */
	private Long id;

	/**
	 * link_id
	 * 用于转赠链接
	 */
	private String linkId;

	/**
	 * card_no
	 * 转赠的卡号
	 */
	private String cardNo;

	/**
	 * 转赠人id
	 */
	private Long memberId;

	private String memberName;

	/**
	 * note
	 * 赠言
	 */
	private String note;

	/**
	 * donee_phone
	 * 受赠人电话
	 */
	private String doneePhone;

	/**
	 * created_time
	 * 转赠时间
	 */
	private Date givenTime;
	/**
	 * 受赠时间
	 */
	private Date doneeTime;

	/**
	 * 0未领取  1已领取
	 */
	private Integer givenStatus;

	/**
	 * 受赠人id
	 */
	@TableField(exist = false)
	private Long doneeMemberId;

	@TableField(exist = false)
	private Long cardId;
}
