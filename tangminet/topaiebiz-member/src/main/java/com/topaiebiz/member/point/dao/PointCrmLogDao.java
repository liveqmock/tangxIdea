package com.topaiebiz.member.point.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.member.point.entity.PointCrmLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description： 描述会员积分的接口，并向会员积分控制层提供相关的方法。
 * <p>
 * Author Scott.Yang
 * <p>
 * Date 2017年11月28日 下午1:37:49
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Mapper
public interface PointCrmLogDao extends BaseDao<PointCrmLogEntity> {


}
