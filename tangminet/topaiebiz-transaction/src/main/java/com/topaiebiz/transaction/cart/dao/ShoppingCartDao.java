package com.topaiebiz.transaction.cart.dao;

import com.nebulapaas.data.mybatis.common.BaseDao;
import com.topaiebiz.transaction.cart.entity.ShoppingCartEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description 购物车的数据访问层
 * <p>
 * <p>
 * Author zhushuyong
 * <p>
 * Date 2017年9月7日 下午9:55:55
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Mapper
public interface ShoppingCartDao extends BaseDao<ShoppingCartEntity> {
    /**
     * Description 修改对应的购物车中的数量
     * <p>
     * Author zhushuyong
     *
     * @param goodsNum 需要修改的数量值
     * @param memberId 传入的会员id
     * @param goodsId  传入的商品sku的id
     * @return
     */
    Integer updateGoodsNum(@Param("goodsNum") Long goodsNum,
                           @Param("memberId") Long memberId, @Param("goodsId") Long goodsId);

    /**
     * Description 根据id删除购物车中指定的商品
     * <p>
     * Author zhushuyong
     *
     * @param id
     * @return
     */
    Integer deleteGoodsCart(Long[] id);

    /**
     * Description app端根据会员id和商品id查询购物车商品
     * <p>
     * Author Hedda
     *
     * @param memberId
     * @param itemId
     * @return
     */
    List<ShoppingCartEntity> selectShoppingCartByItemIds(@Param("memberId") Long memberId, @Param("itemId") Long itemId);

    /**
     * Description 根据id删除购物车中指定的商品
     * <p>
     * Author Hedda
     *
     * @param id
     * @return
     */
    Integer deleteGoodsCartById(Long id);

    /**
     * Description 获取创建订单购物车中被购买的商品
     * <p>
     * Author hxpeng
     *
     * @param memberId
     * @param goodsId
     * @return
     */
    ShoppingCartEntity getByMemberIdAndSkuId(@Param(value = "memberId") Long memberId, @Param(value = "goodsId") Long goodsId);

}
