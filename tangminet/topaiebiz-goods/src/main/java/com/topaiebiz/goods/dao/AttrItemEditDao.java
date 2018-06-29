package com.topaiebiz.goods.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.topaiebiz.goods.dto.AttrItemReq;
import com.topaiebiz.goods.entity.AttrItemEdit;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 属性名编辑表 Mapper 接口
 * </p>
 *
 * @author MMG123
 * @since 2018-05-18
 */
public interface AttrItemEditDao extends BaseMapper<AttrItemEdit> {

    /**
     * 分页查询
     * @param page
     * @param attrItemReq
     * @return
     */
    List<AttrItemEdit> queryAttrItemEdit(Page page, AttrItemReq attrItemReq);
}
