package com.topaiebiz.promotion.mgmt.dao;

import org.apache.ibatis.annotations.Mapper;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.promotion.mgmt.entity.PromotionPlatformUsageLogEntity;

/**
 * 
 * Description 平台活动使用记录表
 * 
 * 
 * Author Joe
 * 
 * Date 2017年9月27日 下午4:19:24
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Mapper
public interface PromotionPlatformUsageLogDao extends BaseDao<PromotionPlatformUsageLogEntity> {

}
