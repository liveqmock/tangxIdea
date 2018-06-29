package com.topaiebiz.goods.favorite.service;

import com.nebulapaas.web.exception.GlobalException;

/**
 * Created by dell on 2018/1/5.
 */
public interface GoodsShareService {

    /**
     * Description app端将商品分享
     * <p>
     * Author Hedda
     *
     * @param memberId 会员id
     * @param itemIds 商品itemId
     * @return
     * @throws GlobalException
     */
    Integer saveGoodsSharing(Long memberId, Long[] itemIds);
}
