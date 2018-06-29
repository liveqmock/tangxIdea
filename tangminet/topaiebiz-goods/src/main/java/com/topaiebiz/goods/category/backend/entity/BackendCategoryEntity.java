package com.topaiebiz.goods.category.backend.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.nebulapaas.data.mybatis.common.BaseBizEntity;
import lombok.Data;

import java.util.Date;

/**
 * Description 商品类目 。
 * 
 * Author Hedda
 * 
 * Date 2017年8月23日 下午4:56:48
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@TableName("t_goo_backend_category")
@Data
public class BackendCategoryEntity extends BaseBizEntity<Long> {

	/**
	 * 序列化版本号。
	 */
	@TableField(exist = false)
	private static final long serialVersionUID = -1501890616022477798L;

	/**
	 * 类目名称。
	 */
	private String name;
	/**
	 * 类目等级 (1 一级 2 二级 3 三级)。
	 */
	private Integer level;

	/**
	 * 类目排序号。
	 */
	private Integer sortNo;

	/**
	 * 父类目。
	 */
	private Long parentId;

	/**
	 * 备注。用于备注其他信息。
	 */
	private String memo;

	private Integer isLeaf;

	private Date lastSyncTime;

    public void clearInit() {
		this.setVersion(null);
		this.setCreatedTime(null);
		this.setDeleteFlag(null);
    }
}