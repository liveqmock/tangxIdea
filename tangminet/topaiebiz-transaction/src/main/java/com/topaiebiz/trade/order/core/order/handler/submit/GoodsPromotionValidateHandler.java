package com.topaiebiz.trade.order.core.order.handler.submit;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderGoodsBO;
import com.topaiebiz.trade.order.core.order.context.PromotionsContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.core.order.promotion.pattern.GoodsPromotionPattern;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.PROMOTION_NOT_EXISTS;
import static com.topaiebiz.trade.order.exception.OrderSubmitExceptionEnum.PROMOTION_NOT_VALID;

/***
 * @author yfeng
 * @date 2018-01-09 17:01
 */
@Component("goodsPromotionValidateHandler")
public class GoodsPromotionValidateHandler implements OrderSubmitHandler {

    @Autowired
    private GoodsPromotionPattern goodsPromotionPattern;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        Map<Long, PromotionDTO> promotionDTOMap = PromotionsContext.get();

        for (StoreOrderBO storeOrderBO : submitContext.getStoreOrderMap().values()) {
            goodsItem:
            for (StoreOrderGoodsBO orderGoodsBO : storeOrderBO.getGoodsList()) {
                if (!MathUtil.validEntityId(orderGoodsBO.getPromotionId())) {
                    continue goodsItem;
                }
                PromotionDTO promotionDTO = promotionDTOMap.get(orderGoodsBO.getPromotionId());
                if (promotionDTO == null) {
                    throw new GlobalException(PROMOTION_NOT_EXISTS);
                }
                //校验商品是否满足此商品
                if (!goodsPromotionPattern.match(orderGoodsBO, promotionDTO)) {
                    throw new GlobalException(PROMOTION_NOT_VALID);
                }
                //将活动价格渲染到商品上去
                goodsPromotionPattern.dispatch(orderGoodsBO, promotionDTO);
                orderGoodsBO.updatePrice();
            }
            //更新店铺订单价格信息
            storeOrderBO.updatePrice();
        }
    }
}
