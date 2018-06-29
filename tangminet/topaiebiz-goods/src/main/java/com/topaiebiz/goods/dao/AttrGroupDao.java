package com.topaiebiz.goods.dao;

import com.topaiebiz.goods.entity.AttrGroup;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 属性分组正式表 Mapper 接口
 * </p>
 *
 * @author MMG123
 * @since 2018-05-18
 */
public interface AttrGroupDao extends BaseMapper<AttrGroup> {


    /**
     * replace into
     * @param attrGroup
     * @return
     */
    Integer replaceEntity(AttrGroup attrGroup);

}
