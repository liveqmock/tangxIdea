package com.topaiebiz.goods.service;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.dto.AttrGroupDTO;
import com.topaiebiz.goods.dto.AttrGroupEditDTO;
import com.topaiebiz.goods.dto.AttrGroupSortNoDTO;
import com.topaiebiz.goods.dto.CategoryIdDTO;

import java.util.List;

/**
 * @description: 属性分组服务：操作正式表和编辑表
 * @author: Jeff Chen
 * @date: created in 上午10:26 2018/5/18
 */
public interface AttrGroupService {

    /**
     * Description 属性分组添加(操作)
     * <p>
     * Author Hedda
     *
     * @param attrGroupEditDTO 属性分组
     * @return
     * @throws GlobalException
     */
    boolean saveAttrGroupEdit(AttrGroupEditDTO attrGroupEditDTO);

    /**
     * Description 属性分组修改(操作)
     * <p>
     * Author Hedda
     *
     * @param attrGroupEditDTO 属性分组
     * @return
     * @throws GlobalException
     */
    boolean modifyAttrGroupEdit(AttrGroupEditDTO attrGroupEditDTO);

    /**
     * Description 属性分组列表
     * <p>
     * Author Hedda
     *
     * @param categoryIdDTO 分页参数
     * @return
     */
    PageInfo<AttrGroupEditDTO> getAttrGroupList(CategoryIdDTO categoryIdDTO);

    /**
     * Description 属性分组删除
     * <p>
     * Author Hedda
     *
     * @param id 属性id
     * @return
     */
    boolean removeAttrGroupEdit(Long id);

    /**
     * Description 属性分组修改排序号(操作)
     * <p>
     * Author Hedda
     *
     * @param attrGroupSortNoDTO
     * @return
     */
    boolean modifyAttrGroupSortNo(AttrGroupSortNoDTO attrGroupSortNoDTO);

    /**
     * Description 属性分组放弃删除(操作)
     * <p>
     * Author Hedda
     *
     * @param id
     * @return
     */
    boolean giveUpAttrGroupEdit(Long id);

    /**
     * @param id
     * @return
     */
    AttrGroupEditDTO findAttrGroupEdit(Long id);

    /**
     * Description 属性分组下拉(生产)
     * <p>
     * Author Hedda
     *
     * @return
     */
    List<AttrGroupEditDTO> queryAttrGroups(Long categoryId);

    /**
     * Description 属性分组列表(生产)
     * <p>
     * Author Hedda
     *
     * @param categoryIdDTO
     * @return
     */
    PageInfo<AttrGroupDTO> getAttrGroups(CategoryIdDTO categoryIdDTO);
}
