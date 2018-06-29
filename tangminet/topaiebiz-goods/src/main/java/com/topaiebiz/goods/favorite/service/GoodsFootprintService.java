package com.topaiebiz.goods.favorite.service;

import com.nebulapaas.base.model.PageInfo;
import com.topaiebiz.goods.favorite.dto.GoodsFootprintDto;

/**
 * Created by dell on 2018/1/5.
 */
public interface GoodsFootprintService {

    /**
     * Description 我的足迹列表
     * <p>
     * Author Hedda
     *
     * @param goodsFootprintDto
     * @param memberId 会员id
     * @return
     */
    PageInfo<GoodsFootprintDto> getGoodsFootprintListByMemberId(GoodsFootprintDto goodsFootprintDto, Long memberId);

    /**
     * Description 删除足迹
     * <p>
     * Author Hedda
     *
     * @param id 商品id
     * @return
     */
    Integer removelGoodsFootprint(Long[] id);

    /**
     * Description app端我的足迹添加
     * <p>
     * Author Hedda
     *
     * @param memberId 会员id
     * @param itemIds  商品id
     * @return
     */
    boolean addGoodsFootprint(Long memberId, Long[] itemIds);
}
