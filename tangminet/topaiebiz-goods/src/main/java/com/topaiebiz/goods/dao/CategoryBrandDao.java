package com.topaiebiz.goods.dao;

import com.topaiebiz.goods.entity.CategoryBrand;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 后台类目关联品牌正式表 Mapper 接口
 * </p>
 *
 * @author MMG123
 * @since 2018-05-18
 */
public interface CategoryBrandDao extends BaseMapper<CategoryBrand> {

    /**
     * replace into
     * @param categoryBrand
     * @return
     */
    Integer replaceEntity(CategoryBrand categoryBrand);
}
