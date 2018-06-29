package com.topaiebiz.trade.refund.core.executer;

import com.topaiebiz.trade.order.dao.OrderPayDao;
import com.topaiebiz.trade.order.util.OrdersQueryUtil;
import com.topaiebiz.trade.refund.core.context.RefundParamsContext;
import com.topaiebiz.trade.refund.dao.RefundOrderDao;
import com.topaiebiz.trade.refund.dao.RefundOrderDetailDao;
import com.topaiebiz.trade.refund.dao.RefundOrderLogDao;
import com.topaiebiz.trade.refund.helper.OrderRefundStateUtil;
import com.topaiebiz.trade.refund.helper.RefundOrderHelper;
import com.topaiebiz.trade.refund.helper.RefundOrderLogHelper;
import com.topaiebiz.trade.refund.helper.RefundQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description 售后处理基类
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/2/2 10:09
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */

@Component
public abstract class AbstractRefundExecuter {

    @Autowired
    protected OrderPayDao orderPayDao;

    @Autowired
    protected RefundOrderDao refundOrderDao;

    @Autowired
    protected RefundOrderHelper refundOrderHelper;

    @Autowired
    protected OrderRefundStateUtil orderRefundStateUtil;

    @Autowired
    protected OrdersQueryUtil ordersQueryUtil;

    @Autowired
    protected RefundOrderDetailDao refundOrderDetailDao;

    @Autowired
    protected RefundQueryUtil refundQueryUtil;

    @Autowired
    protected RefundOrderLogHelper refundOrderLogHelper;

    public abstract boolean execute(RefundParamsContext refundParamsContext);

}
