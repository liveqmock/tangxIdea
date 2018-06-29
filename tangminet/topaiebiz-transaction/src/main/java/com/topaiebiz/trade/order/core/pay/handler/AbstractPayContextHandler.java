package com.topaiebiz.trade.order.core.pay.handler;

import com.topaiebiz.trade.order.core.pay.bo.PayParamContext;
import com.topaiebiz.trade.order.core.pay.context.PkgPayedContext;
import com.topaiebiz.trade.order.po.common.BuyerBO;
import com.topaiebiz.trade.order.po.pay.PayRequest;

/***
 * <p>默认的支付上下文处理器,当检测到已经有过站内支付后<p/>
 * <p>则当前handler会跳过有主程序控制执行下一个链条。<p/>
 * <p>联调也可以通过复写skipWhilePkgPayed()方法改变此逻辑<p/>
 * @author yfeng
 * @date 2018-01-18 16:57
 */
public abstract class AbstractPayContextHandler implements PayContextHandler {

    /**
     * 是否需要检查订单站内支付状态
     * 在子类中通过重写可以覆盖此开关默认值
     *
     * @return
     */
    protected boolean skipWhilePkgPayed() {
        return true;
    }

    @Override
    public void prepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest) {
        //已经完成站内支付，则直接返回，不做任何业务逻辑计算
        if (skipWhilePkgPayed() && PkgPayedContext.get()) {
            return;
        }

        doPrepare(buyer, paramContext, payRequest);
    }

    public abstract void doPrepare(BuyerBO buyer, PayParamContext paramContext, PayRequest payRequest);
}