package com.topaiebiz.goods.favorite.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.goods.favorite.entity.GoodsShareEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Description 商品分享dao
 * 
 * Author Hedda
 * 
 * Date 2017年10月29日 下午7:52:10
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@Mapper
public interface GoodsShareDao extends BaseDao<GoodsShareEntity> {

}
