package com.topaiebiz.trade.order.core.cancel;

import com.topaiebiz.trade.order.core.cancel.action.CancelAction;
import com.topaiebiz.trade.order.core.cancel.action.OrderCancelAction;
import com.topaiebiz.trade.order.core.cancel.handler.AbstractHandler;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.topaiebiz.trade.order.core.config.OrderSubmitConfig.THREAD_POOL_BEAN;

/***
 * @author yfeng
 * @date 2018-01-21 17:50
 */
@Component
public class OrderCancelChain implements InitializingBean {

    @Autowired
    private List<AbstractHandler> handlers;

    @Autowired
    private ApplicationContext springContext;

    /**
     * 异步取消操作列表
     */
    private List<CancelAction> asynActions;

    @Autowired
    private OrderCancelAction orderCancelAction;

    @Resource(name = THREAD_POOL_BEAN)
    public ExecutorService executorService;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<String> actionNames = new ArrayList<>();
        actionNames.add("skuStorageBackAction");
        actionNames.add("cardCancelAction");
        actionNames.add("scoreBackAction");
        actionNames.add("promotionCancelAction");
        this.asynActions = getAsynActionBeans(actionNames);
    }

    private List<CancelAction> getAsynActionBeans(List<String> beanNames) {
        List<CancelAction> actions = new ArrayList<>();
        for (String beanName : beanNames) {
            CancelAction action = springContext.getBean(beanName, CancelAction.class);
            actions.add(action);
        }
        return actions;
    }

    public Boolean cancel(BuyerBO buyerBO, Long payId) {
        CancelParamContext context = new CancelParamContext();
        for (AbstractHandler handler : handlers) {
            handler.handle(buyerBO, payId, context);
        }
        boolean orderCancel = orderCancelAction.action(buyerBO, context);
        if (orderCancel) {
            //本地事务成功，异步提交其他模块事务
            for (CancelAction action : asynActions) {
                executorService.submit(() -> action.action(buyerBO, context));
            }
        }
        return orderCancel;
    }
}