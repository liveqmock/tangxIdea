/**
 * 
 */
package com.topaiebiz.merchant.freight.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.merchant.freight.entity.FreightTempleteDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description： 运费模板详情dao接口
 * 
 * 
 * Author hxpeng
 * 
 * Date 2017年10月24日 上午9:32:21
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Mapper
public interface FreightTempleteDetailDao extends BaseDao<FreightTempleteDetailEntity> {

    List<FreightTempleteDetailEntity> selectByFreightId(@Param("freightId") Long freightId);
}