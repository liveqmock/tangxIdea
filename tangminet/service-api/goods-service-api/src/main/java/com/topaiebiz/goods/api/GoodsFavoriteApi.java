package com.topaiebiz.goods.api;

import java.util.List;

/**
 * Created by dell on 2018/1/8.
 */
public interface GoodsFavoriteApi {

    /**
     * Description 将商品添加到收藏夹
     * <p>
     * Author Hedda
     *
     * @param memberId 会员id
     * @param itemIds  商品itemId
     * @return
     */
    Integer addFavorite(Long memberId, List<Long> itemIds);
}
