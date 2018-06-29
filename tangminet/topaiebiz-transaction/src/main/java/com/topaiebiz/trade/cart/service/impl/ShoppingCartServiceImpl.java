package com.topaiebiz.trade.cart.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.constants.ItemStatusEnum;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import com.topaiebiz.trade.cart.dto.CardPromotionDTO;
import com.topaiebiz.trade.cart.dto.CartDTO;
import com.topaiebiz.trade.cart.dto.CartGoodsDTO;
import com.topaiebiz.trade.cart.dto.CartShopDTO;
import com.topaiebiz.trade.cart.po.CartAddPO;
import com.topaiebiz.trade.cart.po.CartEditPO;
import com.topaiebiz.trade.cart.service.ShoppingCartService;
import com.topaiebiz.trade.cart.util.CartHelper;
import com.topaiebiz.trade.constants.CartStatus;
import com.topaiebiz.trade.order.exception.ShoppingCartExceptionEnum;
import com.topaiebiz.trade.order.facade.GoodsFavoriteServiceFacade;
import com.topaiebiz.trade.order.facade.GoodsSkuServiceFacade;
import com.topaiebiz.trade.order.facade.PromotionServiceFacade;
import com.topaiebiz.trade.order.facade.StoreServiceFacade;
import com.topaiebiz.trade.order.util.PromotionUtil;
import com.topaiebiz.transaction.cart.dao.ShoppingCartDao;
import com.topaiebiz.transaction.cart.entity.ShoppingCartEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.topaiebiz.trade.order.exception.ShoppingCartExceptionEnum.DUPLICAT_ADD_GOODS;

/**
 * Description 购物车接口实现层
 *
 * @author yfeng
 * @date 2017年9月8日 上午10:22:53
 */
@Slf4j
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private CartHelper cartHelper;

    @Autowired
    private ShoppingCartDao shoppingCartDao;

    @Autowired
    private GoodsSkuServiceFacade goodsSkuServiceFacade;

    @Autowired
    private GoodsFavoriteServiceFacade goodsFavoriteServiceFacade;

    @Autowired
    private StoreServiceFacade storeServiceFacade;

    @Autowired
    private PromotionServiceFacade promotionServiceFacade;

    @Autowired
    private DistLockSservice distLockSservice;

    private Comparator<PromotionDTO> promotionDTOComparator = new Comparator<PromotionDTO>() {
        private int BEFORE = -1;
        private int SAME = 0;
        private int AFTER = 1;

        @Override
        public int compare(PromotionDTO o1, PromotionDTO o2) {
            if (o1 == null || o2 == null) {
                return SAME;
            }
            if (o1.getType().getCode() < o2.getType().getCode()) {
                return BEFORE;
            }
            if (o1.getType().getCode() > o2.getType().getCode()) {
                return AFTER;
            }
            return SAME;
        }
    };

    private Comparator<PromotionDTO> freightComparator = new Comparator<PromotionDTO>() {
        private int SAME = 0;

        @Override
        public int compare(PromotionDTO o1, PromotionDTO o2) {
            if (o1 == null || o2 == null) {
                return SAME;
            }
            return o2.getCondValue().compareTo(o1.getCondValue());
        }
    };


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addCart(Long memberId, CartAddPO cartAddPO) {
        LockResult memberLock = null;
        try {
            memberLock = distLockSservice.tryLock("shopping-cart-add-", memberId);
            if (!memberLock.isSuccess()) {
                throw new GlobalException(DUPLICAT_ADD_GOODS);
            }

            // 判断skuId是否存在
            GoodsSkuDTO goodsSKU = goodsSkuServiceFacade.getGoodsSku(cartAddPO.getGoodsId());
            if (goodsSKU == null) {
                throw new GlobalException(ShoppingCartExceptionEnum.GOODSCART_GOODSATTR_NOT_NULL);
            }
            // 判断库存是否充足
            if (cartAddPO.getNum() > goodsSKU.getStockNumber()) {
                throw new GlobalException(ShoppingCartExceptionEnum.GOODSCART_STOCKNUM);
            }
            // 查询此商品是否已经在购物车中
            ShoppingCartEntity existCart = queryWithGoodsId(memberId, cartAddPO.getGoodsId());
            if (existCart == null) {
                ShoppingCartEntity entity = new ShoppingCartEntity();
                entity.setGoodsId(cartAddPO.getGoodsId());
                entity.setCreatorId(memberId);
                entity.setMemberId(memberId);
                entity.setStoreId(goodsSKU.getItem().getBelongStore());
                entity.setGoodsNum(cartAddPO.getNum());
                return shoppingCartDao.insert(entity) > 0;
            }
            if (Constants.DeletedFlag.DELETED_NO.equals(existCart.getDeletedFlag())) {
                // 购物车已存在的情况
                Long goodsNum = existCart.getGoodsNum() + cartAddPO.getNum();
                existCart.setGoodsNum(goodsNum);
                shoppingCartDao.updateById(existCart);
            } else {
                //从删除的购物车中恢复，直接覆盖
                existCart.setGoodsNum(cartAddPO.getNum());
                existCart.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
                shoppingCartDao.updateById(existCart);
            }
        } finally {
            distLockSservice.unlock(memberLock);
        }
        return true;
    }

    @Override
    public CartDTO query(Long memberId) {
        CartDTO cartDTO = new CartDTO();
        //step 1 : 个人购物车按照店铺分组
        Map<Long, List<ShoppingCartEntity>> storeCartsMap = cartHelper.queryCarts(memberId);
        if (MapUtils.isEmpty(storeCartsMap)) {
            return cartDTO;
        }

        //step 2 : 批量查询商品信息
        List<Long> goodsIds = cartHelper.getGoodsIds(storeCartsMap);
        Map<Long, GoodsSkuDTO> skuMap = goodsSkuServiceFacade.getGoodsSkuMap(goodsIds);

        //step 3 : 批量查询店铺信息
        List<Long> storeIds = storeCartsMap.entrySet().stream().map(entry -> entry.getKey()).collect(Collectors.toList());
        Map<Long, StoreInfoDetailDTO> storeMap = storeServiceFacade.getStoreMap(storeIds);

        //step 4 : 组装返回结果集
        buildCartDTO(storeIds, skuMap, storeCartsMap, storeMap, cartDTO);

        //step 5 : 计算每个店铺商品的总价
        for (CartShopDTO cartStop : cartDTO.getShops()) {
            cartStop.caculateGoodsAmout();
        }

        //step 6 : 加载商品单品/店铺优惠活动
        Map<Long, List<PromotionDTO>> goodPromotionsMap = promotionServiceFacade.querySkuPromotionMap(goodsIds);
        Map<Long, List<PromotionDTO>> storePromotionsMap = promotionServiceFacade.queryStorePromotionMap(storeIds);
        try {
            this.addPromotionInCard(goodPromotionsMap, storePromotionsMap, cartDTO);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return cartDTO;
    }


    /**
     * Description: 拼装商品单品优惠活动
     * <p>
     * Author: hxpeng
     * createTime: 2018/1/11
     *
     * @param: goodPromotionsMap 单品营销活动map;  storePromotionMap店铺营销活动map
     **/
    private void addPromotionInCard(Map<Long, List<PromotionDTO>> goodPromotionsMap, Map<Long, List<PromotionDTO>> storePromotionMap, CartDTO cartDTO) {
        for (CartShopDTO cartShopDTO : cartDTO.getShops()) {
            // 单品营销活动
            for (CartGoodsDTO cartGoodsDTO : cartShopDTO.getGoodsList()) {
                cartGoodsDTO.setPromotions(this.buildStorePromotions(goodPromotionsMap.get(cartGoodsDTO.getGoodsId())));
            }
            // 店铺营销活动
            List<PromotionDTO> storePromotionDTOS = storePromotionMap.get(cartShopDTO.getStoreId());
            if (CollectionUtils.isEmpty(storePromotionDTOS)) {
                return;
            }
            cartShopDTO.setPromotions(this.buildStorePromotions(storePromotionDTOS));
            cartShopDTO.setFreightPromotion(this.getFreightPromotion(storePromotionDTOS));
        }
    }

    private List<CardPromotionDTO> buildStorePromotions(List<PromotionDTO> promotionDTOS) {
        if (CollectionUtils.isEmpty(promotionDTOS)) {
            return Collections.emptyList();
        }
        List<CardPromotionDTO> cardPromotionDTOS = new ArrayList<>(promotionDTOS.size());

        //优惠活动排序
        Collections.sort(promotionDTOS, promotionDTOComparator);

        for (PromotionDTO promotionDTO : promotionDTOS) {
            if (PromotionTypeEnum.PROMOTION_TYPE_FREE_SHIPPING == promotionDTO.getType()) {
                continue;
            }
            CardPromotionDTO cardPromotionDTO = new CardPromotionDTO();
            BeanCopyUtil.copy(promotionDTO, cardPromotionDTO);
            cardPromotionDTO.setTypeCode(promotionDTO.getType().getCode());
            cardPromotionDTO.setTypeName(promotionDTO.getType().getValue());
            cardPromotionDTO.setName(PromotionUtil.buildPromotionName(promotionDTO));
            cardPromotionDTOS.add(cardPromotionDTO);
        }
        return cardPromotionDTOS;
    }

    public CardPromotionDTO getFreightPromotion(List<PromotionDTO> promotionDTOS) {
        List<PromotionDTO> freightPromotionDTOS = new ArrayList<>(promotionDTOS.size());

        for (PromotionDTO promotionDTO : promotionDTOS) {
            if (PromotionTypeEnum.PROMOTION_TYPE_FREE_SHIPPING == promotionDTO.getType()) {
                freightPromotionDTOS.add(promotionDTO);
            }
        }
        if (CollectionUtils.isEmpty(freightPromotionDTOS)) {
            return null;
        }
        Collections.sort(freightPromotionDTOS, freightComparator);

        PromotionDTO freightPromotion = freightPromotionDTOS.get(0);
        CardPromotionDTO cardPromotionDTO = new CardPromotionDTO();
        BeanCopyUtil.copy(freightPromotion, cardPromotionDTO);
        cardPromotionDTO.setTypeCode(freightPromotion.getType().getCode());
        cardPromotionDTO.setTypeName(freightPromotion.getType().getValue());
        cardPromotionDTO.setName(PromotionUtil.buildPromotionName(freightPromotion));
        return cardPromotionDTO;
    }

    private void buildCartDTO(List<Long> storeIds, Map<Long, GoodsSkuDTO> skuMap, Map<Long, List<ShoppingCartEntity>> storeCartsMap, Map<Long, StoreInfoDetailDTO> storeMap, CartDTO cartDTO) {
        for (Long storeId : storeIds) {
            StoreInfoDetailDTO storeInfo = storeMap.get(storeId);
            if (storeInfo == null) {
                continue;
            }
            //店铺信息
            CartShopDTO cartShopDTO = new CartShopDTO();
            cartShopDTO.setStoreId(storeInfo.getId());
            cartShopDTO.setStoreName(storeInfo.getName());
            cartDTO.getShops().add(cartShopDTO);

            //购物车集合
            List<ShoppingCartEntity> storeCarts = storeCartsMap.get(storeInfo.getId());
            if (CollectionUtils.isNotEmpty(storeCarts)) {
                for (ShoppingCartEntity cartEntity : storeCarts) {
                    GoodsSkuDTO skuDTO = skuMap.get(cartEntity.getGoodsId());
                    if (skuDTO == null) {
                        continue;
                    }
                    CartGoodsDTO cartGoods = build(cartEntity, skuDTO);
                    if (cartGoods.valid()) {
                        cartShopDTO.getGoodsList().add(cartGoods);
                    } else {
                        cartDTO.getUnusefulGoods().add(cartGoods);
                    }
                }
            }
        }

        //移除商品集合为空的店铺
        Iterator<CartShopDTO> cartShopIterator = cartDTO.getShops().iterator();
        while (cartShopIterator.hasNext()) {
            CartShopDTO cur = cartShopIterator.next();
            if (CollectionUtils.isEmpty(cur.getGoodsList())) {
                cartShopIterator.remove();
            }
        }
    }

    private CartGoodsDTO build(ShoppingCartEntity cartEntity, GoodsSkuDTO skuDTO) {
        CartGoodsDTO cartGoods = new CartGoodsDTO();
        cartGoods.setItemId(skuDTO.getItemId());
        cartGoods.setGoodsId(cartEntity.getGoodsId());
        cartGoods.setCartId(cartEntity.getId());
        cartGoods.setPrice(skuDTO.getPrice());
        cartGoods.setGoodsImg(skuDTO.getSaleImage());
        cartGoods.setGoodsName(skuDTO.getItem().getName());
        cartGoods.setNum(cartEntity.getGoodsNum());
        cartGoods.setStorage(skuDTO.getStockNumber());
        cartGoods.setSaleFieldValue(skuDTO.getSaleFieldValue());
        Integer itemStatus = skuDTO.getItem().getStatus();
        if (ItemStatusEnum.ITEM_STATUS_REMOVE.match(itemStatus)
                || ItemStatusEnum.ITEM_STATUS_NEWENTRY.match(itemStatus)) {
            cartGoods.setStatus(CartStatus.GoodsStatus.DOWN);
        } else if (ItemStatusEnum.ITEM_STATUS_INVIOLATIONODTHESHELVES.match(itemStatus)) {
            cartGoods.setStatus(CartStatus.GoodsStatus.INVIOLATIONODTHESHELVES);
        } else if (cartEntity.getGoodsNum() > skuDTO.getStockNumber()) {
            cartGoods.setStatus(CartStatus.GoodsStatus.STORAGE_LACK);
        } else if (CartStatus.GoodsFreezeFlag.GOOD_FREEZE_YES.equals(skuDTO.getItem().getFrozenFlag())) {
            cartGoods.setStatus(CartStatus.GoodsStatus.FREEZE);
        } else {
            cartGoods.setStatus(CartStatus.GoodsStatus.NORMAL);
        }
        return cartGoods;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeCarts(Long memberId, List<Long> cartIds) {
        return cartHelper.removeCarts(memberId, cartIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean editCart(Long memberId, CartEditPO editPO) {
        List<CartEditPO> editPOS = Lists.newArrayList(editPO);
        return doEditCarts(memberId, editPOS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean editCarts(Long memberId, List<CartEditPO> editPOs) {
        return doEditCarts(memberId, editPOs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean moveToFavorite(Long memberId, Long cartId) {
        ShoppingCartEntity condition = new ShoppingCartEntity();
        //清空默认条件
        condition.clearInit();
        //查询条件
        condition.setId(cartId);
        condition.setMemberId(memberId);
        condition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        ShoppingCartEntity entity = shoppingCartDao.selectOne(condition);
        //为空，直接返回失败
        if (entity == null) {
            return false;
        }

        //从购物车中移除
        entity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        shoppingCartDao.updateById(entity);

        //TODO  加入收藏
        GoodsSkuDTO skuDTO = goodsSkuServiceFacade.getGoodsSku(entity.getGoodsId());
        if (skuDTO != null && skuDTO.getItem() != null) {
            goodsFavoriteServiceFacade.addFavorite(memberId, skuDTO.getItem().getId());
        }
        return true;
    }

    private boolean doEditCarts(Long memberId, List<CartEditPO> editPOs) {
        if (CollectionUtils.isEmpty(editPOs)) {
            //无数据直接返回成功
            return true;
        }
        for (CartEditPO editPO : editPOs) {
            ShoppingCartEntity entity = queryWithCartId(memberId, editPO.getCartId(), Constants.DeletedFlag.DELETED_NO);
            if (entity == null) {
                continue;
            }
            //正常逻辑删除
            entity.setGoodsNum(editPO.getNum());
            shoppingCartDao.updateById(entity);
        }
        return true;
    }

    private ShoppingCartEntity queryWithGoodsId(Long memberId, Long goodsId) {
        ShoppingCartEntity condition = new ShoppingCartEntity();
        //清空默认条件
        condition.clearInit();

        //查询条件
        condition.setGoodsId(goodsId);
        condition.setMemberId(memberId);
        return shoppingCartDao.selectOne(condition);
    }

    private ShoppingCartEntity queryWithCartId(Long memberId, Long cartId, Byte deleteFlag) {
        ShoppingCartEntity condition = new ShoppingCartEntity();
        //清空默认条件
        condition.clearInit();

        //查询条件
        condition.setId(cartId);
        condition.setMemberId(memberId);
        condition.setDeleteFlag(deleteFlag);
        return shoppingCartDao.selectOne(condition);
    }

    private List<ShoppingCartEntity> queryMemberCarts(Long memberId) {
        ShoppingCartEntity condition = new ShoppingCartEntity();
        condition.clearInit();
        condition.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
        condition.setMemberId(memberId);
        return shoppingCartDao.selectList(new EntityWrapper<>(condition));
    }
}