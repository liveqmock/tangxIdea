package com.topaiebiz.trade.order.core.order.handler.submit;

import com.google.common.collect.Maps;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.trade.order.core.order.context.PromotionsContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.facade.PromotionServiceFacade;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestGoods;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestStore;
import com.topaiebiz.trade.order.util.MathUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.PROMOTION_NOT_VALID;

/***
 * @author yfeng
 * @date 2018-01-09 17:01
 */
@Component("allPromotionLoadHandler")
public class AllPromotionsLoadHandler implements OrderSubmitHandler {

    @Autowired
    private PromotionServiceFacade promotionServiceFacade;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        //加载所有的活动ID
        List<Long> promotionIds = getPromotionIds(orderRequest);
        if (CollectionUtils.isEmpty(promotionIds)) {
            //将空Map放入线程上下文
            PromotionsContext.set(Maps.newHashMap());
            return;
        }

        //批量加载营销活动
        Map<Long, PromotionDTO> promotionDTOMap = promotionServiceFacade.queryPromotionMap(promotionIds);
        if (MapUtils.isEmpty(promotionDTOMap)) {
            throw new GlobalException(PROMOTION_NOT_VALID);
        }
        //将空营销数据放入线程上下文
        PromotionsContext.set(promotionDTOMap);

        //校验每个营销活动都能加载成功
        List<PromotionDTO> promotionDTOList = promotionDTOMap.values().stream().filter(item -> item != null).collect(Collectors.toList());
        if (promotionIds.size() != promotionDTOList.size()) {
            throw new GlobalException(PROMOTION_NOT_VALID);
        }
    }

    private List<Long> getPromotionIds(OrderRequest orderRequest) {
        List<Long> ids = new ArrayList<>();

        //平台活动ID
        if (MathUtil.validEntityId(orderRequest.getPlatformPromotionId())) {
            ids.add(orderRequest.getPlatformPromotionId());
        }

        //店铺和商品活动
        for (OrderRequestStore orderRequestStore : orderRequest.getOrders()) {

            //店铺满减
            if (MathUtil.validEntityId(orderRequestStore.getPromotionId())) {
                ids.add(orderRequestStore.getPromotionId());
            }

            //店铺优惠券
            if (MathUtil.validEntityId(orderRequestStore.getCouponId())){
                ids.add(orderRequestStore.getCouponId());
            }

            //包邮
            if (MathUtil.validEntityId(orderRequestStore.getFreightPromotionId())) {
                ids.add(orderRequestStore.getFreightPromotionId());
            }
            //商品
            for (OrderRequestGoods orderGoods : orderRequestStore.getGoodsList()) {
                if (MathUtil.validEntityId(orderGoods.getPromotionId())) {
                    ids.add(orderGoods.getPromotionId());
                }
            }
        }

        //单品活动ID可能重复
        ids = ids.stream().distinct().collect(Collectors.toList());
        return ids;
    }
}