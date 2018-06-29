package com.topaiebiz.goods.dao;

import com.topaiebiz.goods.entity.AttrValue;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 属性值正式表 Mapper 接口
 * </p>
 *
 * @author MMG123
 * @since 2018-05-18
 */
public interface AttrValueDao extends BaseMapper<AttrValue> {

    /**
     * replace into
     * @param attrValue
     * @return
     */
    Integer replaceEntity(AttrValue attrValue);
}
