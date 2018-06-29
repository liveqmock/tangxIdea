package com.topaiebiz.trade.order.core.order.handler.submit;

import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.member.dto.address.MemberAddressDto;
import com.topaiebiz.promotion.dto.PromotionDTO;
import com.topaiebiz.trade.order.core.order.bo.StoreOrderBO;
import com.topaiebiz.trade.order.core.order.context.AddressContext;
import com.topaiebiz.trade.order.core.order.context.PromotionsContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitContext;
import com.topaiebiz.trade.order.core.order.handler.OrderSubmitHandler;
import com.topaiebiz.trade.order.core.order.promotion.pattern.FreightPromotionPattern;
import com.topaiebiz.trade.order.facade.FreightTemplateServiceFacade;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequest;
import com.topaiebiz.trade.order.po.ordersubmit.OrderRequestStore;
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
@Component("freightPromotionValidateHandler")
public class FreightPromotionValidateHandler implements OrderSubmitHandler {

    @Autowired
    private FreightTemplateServiceFacade freightTemplateServiceFacade;

    @Autowired
    private FreightPromotionPattern freightPromotionPattern;

    @Override
    public void handle(OrderSubmitContext submitContext, OrderRequest orderRequest) {
        MemberAddressDto addressDto = AddressContext.get();
        //请求无地址参数
        if (addressDto == null) {
            return;
        }

        Map<Long, PromotionDTO> promotionDTOMap = PromotionsContext.get();
        Map<Long, StoreOrderBO> storeOrderMap = submitContext.getStoreOrderMap();

        //遍历每一个店铺订单，校验包邮活动
        for (OrderRequestStore orderRequestStore : orderRequest.getOrders()) {
            Long freightPromotionId = orderRequestStore.getFreightPromotionId();
            if (!MathUtil.validEntityId(freightPromotionId)) {
                continue;
            }
            //店铺订单信息
            StoreOrderBO orderBO = storeOrderMap.get(orderRequestStore.getStoreId());

            //包邮活动
            PromotionDTO promotionDTO = promotionDTOMap.get(freightPromotionId);
            if (promotionDTO == null) {
                throw new GlobalException(PROMOTION_NOT_EXISTS);
            }
            //校验当前店铺订单是否满足包邮活动，若满足则进行优惠分摊
            if (!freightPromotionPattern.match(orderBO, promotionDTO)) {
                throw new GlobalException(PROMOTION_NOT_VALID);
            }
            freightPromotionPattern.dispatch(orderBO, promotionDTO);
        }
    }
}