/**
 * 
 */
package com.topaiebiz.merchant.enter.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Description： 区域 数据Dto
 * 
 * 
 * Author Aaron.Xue
 * 
 * Date 2017年10月19日 下午2:50:40
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Data
public class DistrictInfoDto implements Serializable {
	
	private List<DistrictInfoDto> childList;

	private Long id;

	/**
	 * 区域信息的唯一编码。
	 */
	private String code;

	/**
	 * 区域的全名。
	 */
	private String fullName;

	/**
	 * 区域的简称。
	 */
	private String shortName;

	/**
	 * 区域的类型。
	 */
	private Long districtTypeId;

	/**
	 * 区域的类型名称
	 */
	private String districtTypeName;

	/**
	 * 区域的父区域的编号
	 */
	private Long parentDistrictId;

	/**
	 * 区域的父区域的名称。注意：取值自父区域的区域名称。冗余字段，方便显示。
	 */
	private String parentDistrictName;

	/**
	 * 区域的ISO两字母代码
	 */
	private String isoTwoAlphabetCode;

	/**
	 * 区域的ISO三字母代码
	 */
	private String isoThreeAlphabetCode;

	/**
	 * 区域的ISO三数字代码
	 */
	private Long isoThreeNumericCode;

	/**
	 * 区域的序列号。由父区域序列号+“.”+当前区域的全局唯一主键构成。冗余字段，方便查询。
	 */
	private String serialNo;

	/**
	 * 区域的序列名。由父区域序列名+“ -> ”+当前区域的简称构成。冗余字段，方便显示
	 */
	private String serialName;

	/**
	 * 区域的ISO语言代码
	 */
	private String isoLanguageCode;

	/**
	 * 区域的ISO语言名称
	 */
	private String isoLanguageName;

	/**
	 * 区域的电话区号
	 */
	private String phoneAreaCode;

	/**
	 * 区域的描述信息
	 */
	private String description;

	/**
	 * 备注。用于备注其他信息。
	 */
	private String memo;

	/**
	 * 重写hashCode方法 用来判断重复
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * 重写equals方法 用来判断重复
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DistrictInfoDto other = (DistrictInfoDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
