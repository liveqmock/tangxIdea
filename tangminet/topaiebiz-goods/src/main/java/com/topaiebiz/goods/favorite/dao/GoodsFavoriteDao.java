package com.topaiebiz.goods.favorite.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.goods.favorite.dto.GoodsFavoriteDto;
import com.topaiebiz.goods.favorite.entity.GoodsFavoriteEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * Description 收藏夹数据库访问层（以商品最小sku单元为收藏）
 * 
 * 
 * Author zhushuyong
 * 
 * Date 2017年9月11日 上午11:23:18
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Mapper
public interface GoodsFavoriteDao extends BaseDao<GoodsFavoriteEntity> {

    /**
     * Description 根据会员id查询收藏夹列表
     * <p>
     * Author Hedda
     * @param page
     * @param memberId
     * @return
     */
    List<GoodsFavoriteDto> selectGoodsFavoriteListByMemberId(Page<GoodsFavoriteDto> page, Long memberId);
}
