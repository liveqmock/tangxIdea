package com.topaiebiz.goods.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.topaiebiz.goods.dto.AttrItemReq;
import com.topaiebiz.goods.entity.AttrItem;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 属性名正式表 Mapper 接口
 * </p>
 *
 * @author MMG123
 * @since 2018-05-18
 */
public interface AttrItemDao extends BaseMapper<AttrItem> {
    /**
     * 分页查询
     * @param page
     * @param attrItemReq
     * @return
     */
    List<AttrItem> queryAttrItemFormal(Page page, AttrItemReq attrItemReq);

    /**
     * replace into
     * @param attrItem
     * @return
     */
    Integer replaceEntity(AttrItem attrItem);
}
