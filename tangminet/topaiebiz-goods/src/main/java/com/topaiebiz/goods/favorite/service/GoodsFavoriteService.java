package com.topaiebiz.goods.favorite.service;


import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.favorite.dto.GoodsFavoriteDto;
import com.topaiebiz.goods.sku.dto.ItemDto;

/**
 * Description 收藏夹数据库访问层（以商品最小sku单元为收藏）业务接口
 * <p>
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年9月11日 上午11:28:06
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface GoodsFavoriteService {

    /**
     * Description app端收藏夹列表
     * <p>
     * Author Hedda
     *
     * @param pagePO
     * @return
     */
    PageInfo<GoodsFavoriteDto> getGoodsFavoriteListByMemberId(PagePO pagePO, Long memberId);

    /**
     * Description app端删除收藏夹
     * <p>
     * Author Hedda
     * @param id
     * @return
     * @throws GlobalException
     */
    Integer removelGoodsFavorite(Long id,Long memberId) throws GlobalException;

    /**
     * Description 根据会员id，sku最小单元商品id，查询当前会员收藏夹中是否存在此商品<p>
     * Author zhushuyong
     *
     * @param memberId
     * @param goodsId
     * @return
     */
    Boolean findGoodsFavorite(Long memberId, Long goodsId);

}