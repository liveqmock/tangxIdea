package com.topaiebiz.goods.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.topaiebiz.goods.dto.AttrGroupEditDTO;
import com.topaiebiz.goods.entity.AttrGroupEdit;

import java.util.List;

/**
 * <p>
 * 属性分组编辑表 Mapper 接口
 * </p>
 *
 * @author MMG123
 * @since 2018-05-18
 */
public interface AttrGroupEditDao extends BaseMapper<AttrGroupEdit> {

    List<AttrGroupEdit> selectAddAttrGroupEditName(AttrGroupEditDTO attrGroupEditDTO);

    List<AttrGroupEdit> selectUpdateAttrGroupEditName(AttrGroupEditDTO attrGroupEditDTO);
}
