package com.topaiebiz.dec.service;

import com.baomidou.mybatisplus.service.IService;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.dec.dto.TitleGoodsDto;
import com.topaiebiz.dec.dto.TitleItemDto;
import com.topaiebiz.dec.entity.TitleGoodsEntity;

import java.util.List;

/**
 * <p>
 * 标题商品详情表 服务类
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
public interface TitleGoodsService extends IService<TitleGoodsEntity> {

    void saveTitleGoodsDto(TitleGoodsDto titleGoodsDto);

    void deleteTitleGoodsDto(Long id);

    TitleGoodsDto getTitleGoodsDtos(Long titleId);

    void modifyTitleGoods(TitleGoodsDto titleGoodsDto);

    PageInfo<TitleGoodsEntity> getTitlePageList(PagePO pagePO, Long titleId);

    TitleItemDto getTitleItemDto(Long titleId);

    void refreshCache(Long titleId);
}
