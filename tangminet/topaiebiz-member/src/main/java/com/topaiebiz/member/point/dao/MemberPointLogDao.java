package com.topaiebiz.member.point.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.member.point.entity.MemberPointLogEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2017/12/21 20:33
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface MemberPointLogDao extends BaseDao<MemberPointLogEntity> {

	/**
	*
	* Description: 自定义条件 查询用户的添加积分记录
	*
	* Author: hxpeng
	* createTime: 2017/12/22
	*
	* @param:
	**/
	List<MemberPointLogEntity> findByParamsMap(@Param("paramsMap") Map<String, Object> paramsMap);


	/**
	*
	* Description: 查询用户查询今天的积分领取记录
	*
	* Author: hxpeng
	* createTime: 2017/12/22
	*
	* @param:
	**/
	MemberPointLogEntity findByMemberIdAndGainTypeInToday(@Param("memberId") Long memberId, @Param("gainType") Long gainType);
}
