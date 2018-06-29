package com.topaiebiz.merchant.freight.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import com.topaiebiz.merchant.enter.dto.DistrictInfoDto;
import lombok.Data;

/**
 * Description: 运费模板详情实体类
 * 
 * Author : Anthony
 * 
 * Date :2017年10月13日 上午10:14:52
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
@TableName("t_mer_freight_templete_detail")
public class FreightTempleteDetailEntity extends BaseBizEntity<Long> implements Comparable<FreightTempleteDetailEntity>{

	/** 版本序列化 */
	private static final long serialVersionUID = 5099866494757262801L;

	/** 关联的运费模板ID */
	private Long freightId;

	/** 配送方式 */
	private Integer type;

	/** 首次价格 */
	private double firstPrice;

	/** 首次件数 */
	private double firstNum;

	/** 续件价格 */
	private double addPrice;

	/** 续件件数 */
	private double addNum;

	/** 是否为默认运费 */
	private Integer isDefault;

	private String districtIdList;

	@TableField(exist = false)
	private String nameListStr;

	@Override
	public int compareTo(FreightTempleteDetailEntity o) {
			if (this.firstPrice > o.firstPrice) {
				return 1;
			} else if (this.firstPrice < o.firstPrice) {
				return -1;
			}
		return 0;
	}
}
