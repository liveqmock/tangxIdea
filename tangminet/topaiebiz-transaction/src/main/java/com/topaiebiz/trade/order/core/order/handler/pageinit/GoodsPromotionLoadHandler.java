package com.topaiebiz.trade.order.core.order.handler.pageinit;

import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.context.GoodsPromotionsContext;
import com.topaiebiz.trade.order.core.order.context.SkuIdContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.core.order.promotion.compare.PromotionComparator;
import com.topaiebiz.trade.order.core.order.promotion.pattern.GoodsPromotionPattern;
import com.topaiebiz.trade.order.facade.PromotionServiceFacade;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/***
 * @author yfeng
 * @date 2018-01-09 16:59
 */
@Component("pageInitGoodsPromotionLoadHandler")
public class GoodsPromotionLoadHandler implements OrderSubmitHandler {

    @Autowired
    private PromotionServiceFacade promotionServiceFacade;

    @Autowired
    private GoodsPromotionPattern goodsPromotionPattern;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        List<Long> skuIds = SkuIdContext.get();

        //step 1 : 加载所有商品进行中的营销活动
        Map<Long, List<PromotionDTO>> skuPromotionsMap = promotionServiceFacade.querySkuPromotionMap(skuIds);

        //step 2 : 将商品添加可用营销活动
        Map<Long, List<PromotionDTO>> matchResult = new HashMap();
        for (StoreOrderBO storeOrderBO : submitContext.getStoreOrderMap().values()) {
            for (StoreOrderGoodsBO orderGoodsBO : storeOrderBO.getGoodsList()) {
                List<PromotionDTO> promotionDTOS = skuPromotionsMap.get(orderGoodsBO.getGoods().getId());
                checkMatch(orderGoodsBO, promotionDTOS, matchResult);
            }
        }
        GoodsPromotionsContext.set(matchResult);
    }

    private void checkMatch(StoreOrderGoodsBO orderGoodsBO, List<PromotionDTO> promotionDTOS, Map<Long, List<PromotionDTO>> matchResult) {
        if (CollectionUtils.isEmpty(promotionDTOS)) {
            return;
        }
        //step 1 : 遍历所有活动，判断此商品是否满足此活动
        List<PromotionDTO> usefulPromotions = new ArrayList<>();
        for (PromotionDTO promotionDTO : promotionDTOS) {
            if (goodsPromotionPattern.match(orderGoodsBO, promotionDTO)) {
                usefulPromotions.add(promotionDTO);
            }
        }

        //step 2 : 排序选择最优活动
        if (CollectionUtils.isNotEmpty(usefulPromotions)) {
            Collections.sort(usefulPromotions, PromotionComparator.getInstance());
        }

        //step 3 : 记录进入单品可用优惠列表中
        matchResult.put(orderGoodsBO.getGoods().getId(), usefulPromotions);
    }
}