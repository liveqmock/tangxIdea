package com.topaiebiz.trade.order.core.order.util;

import com.nebulapaas.common.BeanCopyUtil;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.promotion.dto.PromotionGoodsDTO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.context.AddressContext;
import com.topaiebiz.trade.order.core.order.context.CartMapContext;
import com.topaiebiz.trade.order.core.order.context.GoodsPromotionsContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.dto.ordersubmit.GoodsSplitDTO;
import com.topaiebiz.trade.order.dto.ordersubmit.PromotionInfoDTO;
import com.topaiebiz.trade.order.dto.ordersubmit.StoreGoodsDTO;
import com.topaiebiz.trade.order.dto.ordersubmit.StoreItemDTO;
import com.topaiebiz.trade.order.util.PromotionUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/***
 * @author yfeng
 * @date 2018-01-12 11:28
 */
public class GoodsSplitDTOUtil {
    public static GoodsSplitDTO buildResult(OrderSubmitContext orderContext) {
        GoodsSplitDTO gsDTO = new GoodsSplitDTO();

        //step 1 : 渲染地址数据
        gsDTO.setAddress(AddressContext.get());

        //step 2 : 店铺订单数据
        for (StoreOrderBO orderBO : orderContext.getStoreOrderMap().values()) {
            StoreItemDTO storeItem = buildStoreItem(orderBO);
            gsDTO.getStores().add(storeItem);
        }

        gsDTO.setHasHaitaoOrder(orderContext.isHasHaitaoOrder());
        return gsDTO;
    }

    private static StoreItemDTO buildStoreItem(StoreOrderBO orderBO) {
        //拷贝店铺数据
        StoreItemDTO storeItem = new StoreItemDTO();
        storeItem.setStoreId(orderBO.getStore().getId());
        storeItem.setStoreName(orderBO.getStore().getName());
        storeItem.setPayPrice(orderBO.getPayPrice());
        storeItem.setFreightPrice(orderBO.getGoodsFreight());

        //拷贝店铺下商品数据
        Map<Long, Long> cartIdMap = CartMapContext.get();
        for (StoreOrderGoodsBO goodsBO : orderBO.getGoodsList()) {
            StoreGoodsDTO storeGoodsDTO = new StoreGoodsDTO();
            GoodsSkuDTO goodsSku = goodsBO.getGoods();
            Long goodsId = goodsSku.getId();

            //查找对应购物车的ID
            Long cartId = cartIdMap != null ? cartIdMap.get(goodsId) : null;
            storeGoodsDTO.setCartId(cartId);

            //商品基本信息
            storeGoodsDTO.setGoodsId(goodsId);
            storeGoodsDTO.setGoodsName(goodsSku.getItem().getName());
            storeGoodsDTO.setGoodsNum(goodsBO.getGoodsNum());
            storeGoodsDTO.setItemId(goodsSku.getItemId());
            storeGoodsDTO.setGoodsImg(goodsSku.getSaleImage());
            storeGoodsDTO.setOriginPrice(goodsSku.getPrice());
            storeGoodsDTO.setSaleFieldValue(goodsSku.getSaleFieldValue());
            loadSinglePromotions(storeGoodsDTO);
            storeItem.getGoodsList().add(storeGoodsDTO);
        }
        return storeItem;
    }

    /**
     * 下单页面初始化时计算每个单品的可用优惠活动列表
     *
     * @param storeGoodsDTO
     * @return
     */
    public static void loadSinglePromotions(StoreGoodsDTO storeGoodsDTO) {
        List<PromotionInfoDTO> promotionInfos = new ArrayList<>();
        storeGoodsDTO.setPromotions(promotionInfos);

        Map<Long, List<PromotionDTO>> skuPromotionListsMap = GoodsPromotionsContext.get();
        List<PromotionDTO> promotions = skuPromotionListsMap.get(storeGoodsDTO.getGoodsId());
        if (CollectionUtils.isEmpty(promotions)) {
            return;
        }

        for (PromotionDTO promotionDTO : promotions) {
            PromotionInfoDTO promotionInfoDTO = new PromotionInfoDTO();
            BeanCopyUtil.copy(promotionDTO, promotionInfoDTO);
            promotionInfoDTO.setTypeName(promotionDTO.getType().getValue());
            promotionInfoDTO.setTypeCode(promotionDTO.getType().getCode());
            promotionInfoDTO.setName(PromotionUtil.buildPromotionName(promotionDTO));
            PromotionGoodsDTO promotionGoodsDTO = findPromotionGoods(promotionDTO, storeGoodsDTO.getGoodsId());
            promotionInfoDTO.setGoodsPrice(promotionGoodsDTO.getPromotionPrice());
            promotionInfos.add(promotionInfoDTO);
        }
        Collections.sort(promotionInfos, new Comparator<PromotionInfoDTO>() {
            @Override
            public int compare(PromotionInfoDTO o1, PromotionInfoDTO o2) {
                if (o1.getGoodsPrice() == null || o2.getGoodsPrice() == null) {
                    return 0;
                }
                return o1.getGoodsPrice().compareTo(o2.getGoodsPrice());
            }
        });
    }

    private static PromotionGoodsDTO findPromotionGoods(PromotionDTO goodsPromotion, Long skuId) {
        if (CollectionUtils.isNotEmpty(goodsPromotion.getLimitGoods())) {
            for (PromotionGoodsDTO goodsDTO : goodsPromotion.getLimitGoods()) {
                if (goodsDTO.getGoodsSkuId().equals(skuId)) {
                    return goodsDTO;
                }
            }
        }
        return null;
    }


    public static PromotionInfoDTO buildPromotionInfo(PromotionDTO goodsPromotion) {
        PromotionInfoDTO promotionInfoDTO = new PromotionInfoDTO();
        BeanCopyUtil.copy(goodsPromotion, promotionInfoDTO);
        promotionInfoDTO.setTypeName(goodsPromotion.getType().getValue());
        promotionInfoDTO.setTypeCode(goodsPromotion.getType().getCode());
        if (CollectionUtils.isNotEmpty(goodsPromotion.getLimitGoods())) {
            PromotionGoodsDTO goodsDTO = goodsPromotion.getLimitGoods().get(0);
            promotionInfoDTO.setGoodsPrice(goodsDTO.getPromotionPrice());
            promotionInfoDTO.setLimitGoods(true);
        }
        return promotionInfoDTO;
    }

}