package com.topaiebiz.goods.favorite.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.goods.favorite.dto.GoodsFootprintDto;
import com.topaiebiz.goods.favorite.entity.GoodsFootprintEntity;
import com.topaiebiz.goods.sku.dto.ItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description 我的足迹dao
 * 
 * Author Hedda
 * 
 * Date 2017年11月16日 下午5:18:54
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@Mapper
public interface GoodsFootprintDao extends BaseDao<GoodsFootprintEntity> {

    /**
     * Description 根据会员id查询我的足迹
     * <p>
     * Author Hedda
     * @param page
     * @param goodsFootprintDto
     * @return
     */
    List<GoodsFootprintDto> selectGoodsFootprintListByMemberId(Page<GoodsFootprintDto> page, GoodsFootprintDto goodsFootprintDto);
}
