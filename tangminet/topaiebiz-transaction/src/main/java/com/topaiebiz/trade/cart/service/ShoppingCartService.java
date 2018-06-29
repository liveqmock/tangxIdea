package com.topaiebiz.trade.cart.service;

import com.topaiebiz.trade.cart.dto.CartDTO;
import com.topaiebiz.trade.cart.po.CartAddPO;
import com.topaiebiz.trade.cart.po.CartEditPO;

import java.util.List;

/**
 * Description 购物车接口
 * <p>
 * <p>
 * Author zhushuyong
 * <p>
 * Date 2017年9月8日 上午10:19:16
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface ShoppingCartService {

    CartDTO query(Long memberId);

    Boolean addCart(Long memberId, CartAddPO cartAddPO);

    Boolean removeCarts(Long memberId, List<Long> cartIds);

    Boolean editCart(Long memberId, CartEditPO editPO);

    Boolean editCarts(Long memberId, List<CartEditPO> editPOs);

    Boolean moveToFavorite(Long memberId, Long cartId);
}