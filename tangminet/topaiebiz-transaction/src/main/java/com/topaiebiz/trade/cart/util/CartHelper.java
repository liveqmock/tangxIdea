package com.topaiebiz.trade.cart.util;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.transaction.cart.dao.ShoppingCartDao;
import com.topaiebiz.transaction.cart.entity.ShoppingCartEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.topaiebiz.trade.order.exception.ShoppingCartExceptionEnum.GOODSCART_NOT_EXIST;

/***
 * @author yfeng
 * @date 2018-01-09 15:48
 */
@Component
@Slf4j
public class CartHelper {

    @Autowired
    private ShoppingCartDao cartDao;

    public Boolean removeCarts(Long memberId, List<Long> cartIds) {
        if (CollectionUtils.isEmpty(cartIds)) {
            log.warn("cartIds is empty,dose not need to remove ....");
            return false;
        }
        ShoppingCartEntity update = new ShoppingCartEntity();
        //清空默认条件
        update.clearInit();
        update.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);

        EntityWrapper<ShoppingCartEntity> condition = new EntityWrapper<>();
        condition.in("id", cartIds);
        condition.eq("memberId", memberId);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        return cartDao.update(update, condition) > 0;
    }

    public List<Long> getGoodsIds(Map<Long, List<ShoppingCartEntity>> storeCartsMap) {
        if (MapUtils.isEmpty(storeCartsMap)) {
            return Collections.emptyList();
        }
        List<Long> cartIds = new ArrayList<>();
        for (List<ShoppingCartEntity> cartList : storeCartsMap.values()) {
            for (ShoppingCartEntity cart : cartList) {
                cartIds.add(cart.getGoodsId());
            }
        }
        return cartIds;
    }

    public Map<Long, List<ShoppingCartEntity>> queryCarts(Long memberId) {
        //step 1 : 查询所有购物车
        EntityWrapper<ShoppingCartEntity> cond = new EntityWrapper<>();
        cond.eq("memberId", memberId);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<ShoppingCartEntity> cartEntities = cartDao.selectList(cond);
        return groupByStoreId(cartEntities);
    }

    /**
     * 根据购物车的店铺ID进行分组
     *
     * @param cartIds 购物车ID集合
     * @return
     */
    public Map<Long, List<ShoppingCartEntity>> queryCarts(Long memberId, List<Long> cartIds) {
        if (CollectionUtils.isEmpty(cartIds)) {
            return Collections.emptyMap();
        }
        //step 1 : 查询所有购物车
        EntityWrapper<ShoppingCartEntity> cond = new EntityWrapper<>();
        cond.in("id", cartIds);
        cond.eq("memberId", memberId);
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<ShoppingCartEntity> cartEntities = cartDao.selectList(cond);
        if (CollectionUtils.isEmpty(cartEntities) || cartIds.size() != cartEntities.size()) {
            throw new GlobalException(GOODSCART_NOT_EXIST);
        }
        //step 2 : 按照店铺ID分组
        return groupByStoreId(cartEntities);
    }

    private Map<Long, List<ShoppingCartEntity>> groupByStoreId(List<ShoppingCartEntity> cartEntities) {
        //step 1 : 数据判空
        if (CollectionUtils.isEmpty(cartEntities)) {
            return Collections.emptyMap();
        }

        //step 2 : 按照店铺ID进行分组
        Map<Long, List<ShoppingCartEntity>> storeCartMap = new LinkedHashMap<>();
        for (ShoppingCartEntity cart : cartEntities) {
            List<ShoppingCartEntity> storeList = storeCartMap.get(cart.getStoreId());
            if (storeList == null) {
                storeList = new ArrayList<>();
                storeCartMap.put(cart.getStoreId(), storeList);
            }
            storeList.add(cart);
        }
        return storeCartMap;
    }

}