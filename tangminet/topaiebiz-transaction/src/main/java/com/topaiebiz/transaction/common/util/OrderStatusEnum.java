package com.topaiebiz.transaction.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Description 订单状态枚举类 
 * 
 * Author Aaron.Xue 
 *    
 * Date 2017年10月23日 下午3:45:37 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public enum OrderStatusEnum {

	/**
	 * 订单取消
	 */
	ORDER_CANCELLATION(0, "订单取消"),

	/**
	 * 待支付
	 */
	UNPAY(10, "待支付"),

	/**
	 * 待发货
	 */
	PENDING_DELIVERY(20, "待发货"),

	/**
	 * 待收货
	 */
	PENDING_RECEIVED(30, "待收货"),

	/**
	 * 已收货
	 */
	HAVE_BEEN_RECEIVED(40, "已收货"),

	/**
	 * 订单已完成
	 */
	ORDER_COMPLETION(50, "订单完成"),

    /**
     * 退款成功之后的订单状态
     */
    ORDER_CLOSE(60, "订单关闭"),

    /**
     * 待评价 = (已收货 or 已完成) and commentFlag = 0
     * 此枚举值仅用于列表查询条件
     */
    WAIT_EVALUATION(70, "待评价")
	;


	/**
	 * 状态码
	 */
	private Integer code;
	
	/**
	 * 释意
	 */
	private String desc;

	OrderStatusEnum(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	/**
	 * Description 获取状态码
	 * 
	 * Author Aaron.Xue   
	 * 
	 * @return
	 */
	public Integer getCode() {
		return code;
	}


	/**
	 * Description 获取释意
	 * 
	 * Author Aaron.Xue   
	 * 
	 * @return
	 */
	public String getDesc() {
		return desc;
	}


	/**
	 *
	 * Description: 查看状态码是否在枚举中
	 *
	 * Author: hxpeng
	 * createTime: 2017/12/1
	 *
	 * @param:
	 **/
	public static boolean isCodeInEnums(Integer code){
		for (OrderStatusEnum orderStatusEnum : OrderStatusEnum.values()){
			if(orderStatusEnum.getCode().equals(code)){
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * Description: 根绝code 获取枚举
	 *
	 * Author: hxpeng
	 * createTime: 2018/1/20
	 *
	 * @param:
	 **/
    public static OrderStatusEnum getByCode(Integer code){
        for (OrderStatusEnum orderStatusEnum : OrderStatusEnum.values()) {
            if (orderStatusEnum.getCode().equals(code)){
                return orderStatusEnum;
            }
        }
        throw new NullPointerException(StringUtils.join("----------无效的枚举类型",code));
    }

    /**
    *
    * Description: 获取枚举描述
    *
    * Author: hxpeng
    * createTime: 2018/3/8
    *
    * @param:
    **/
    public static String getDescByCode(Integer code){
        for (OrderStatusEnum orderStatusEnum : OrderStatusEnum.values()) {
            if (orderStatusEnum.getCode().equals(code)){
                return orderStatusEnum.getDesc();
            }
        }
        throw new NullPointerException(StringUtils.join("----------无效的枚举类型",code));
    }

}
