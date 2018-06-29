package com.topaiebiz.goods.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.goods.dto.AttrItemDTO;
import com.topaiebiz.goods.dto.AttrItemReq;
import com.topaiebiz.goods.entity.AttrItem;
import com.topaiebiz.goods.entity.AttrItemEdit;

/**
 * @description: 属性项服务：属性项和属性值，操作正式表和编辑表
 * @author: Jeff Chen
 * @date: created in 上午10:27 2018/5/18
 */
public interface AttrItemService {

    /**
     * 添加属性项
     * @param attrItemDTO
     * @return
     */
    boolean saveAttrItemEdit(AttrItemDTO attrItemDTO);

    /**
     * 获取属性详情
     * @param attrId
     * @return
     */
    AttrItemDTO getAttrItemEditById(Long attrId);

    /**
     * 删除指定属性
     * @param attrItemEdit
     * @return
     */
    boolean deleteAttrItemEdit(AttrItemEdit attrItemEdit);

    /**
     * 放弃删除
     * @param attrItemEdit
     * @return
     */
    boolean abortDel(AttrItemEdit attrItemEdit);

    /**
     * 更新属性编辑项
     * @param attrItemDTO
     * @return
     */
    boolean updateAttrItemEdit(AttrItemDTO attrItemDTO);

    /**
     * 更新属性排序
     * @param attrItemDTO
     * @return
     */
    boolean updateAttrItemEditSort(AttrItemDTO attrItemDTO);

    /**
     * 分页查询属性编辑项
     * @param attrItemReq
     * @return
     */
    PageInfo<AttrItemDTO> selectAttrItemEdit(AttrItemReq attrItemReq);

    /**
     * 分页查询属性正式列表
     * @param attrItemReq
     * @return
     */
    PageInfo<AttrItemDTO> selectAttrItemFormal(AttrItemReq attrItemReq);
}
