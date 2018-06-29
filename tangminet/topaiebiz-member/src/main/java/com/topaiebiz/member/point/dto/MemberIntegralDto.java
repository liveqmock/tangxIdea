package com.topaiebiz.member.point.dto;

import com.topaiebiz.member.member.entity.MemberEntity;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017/12/21 15:06
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public class MemberIntegralDto {

	// 用户
	private MemberEntity memberEntity;

	// 积分
	private Long integral;


	///////////// 增加
	// 获取类型
	private Long gainType;

	// 订单ID
	private Long orderId;

	/** 消费金额。*/
	private Double costMoney;

	/** 获取积分。*/
	private Long gainScore;

	/** 拉取的时候记录。*/
	private Long scoreSource;

	///////////// 扣除

	/** 使用积分 */
	private Long usageScore;

	/** 抵扣金额 */
	private Double deductibleAmount;


	/** 备注*/
	private String memo;

	public MemberEntity getMemberEntity() {
		return memberEntity;
	}

	public void setMemberEntity(MemberEntity memberEntity) {
		this.memberEntity = memberEntity;
	}

	public Long getIntegral() {
		return integral;
	}

	public void setIntegral(Long integral) {
		this.integral = integral;
	}

	public Long getGainType() {
		return gainType;
	}

	public void setGainType(Long gainType) {
		this.gainType = gainType;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Double getCostMoney() {
		return costMoney;
	}

	public void setCostMoney(Double costMoney) {
		this.costMoney = costMoney;
	}

	public Long getGainScore() {
		return gainScore;
	}

	public void setGainScore(Long gainScore) {
		this.gainScore = gainScore;
	}

	public Long getScoreSource() {
		return scoreSource;
	}

	public void setScoreSource(Long scoreSource) {
		this.scoreSource = scoreSource;
	}

	public Long getUsageScore() {
		return usageScore;
	}

	public void setUsageScore(Long usageScore) {
		this.usageScore = usageScore;
	}

	public Double getDeductibleAmount() {
		return deductibleAmount;
	}

	public void setDeductibleAmount(Double deductibleAmount) {
		this.deductibleAmount = deductibleAmount;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}
