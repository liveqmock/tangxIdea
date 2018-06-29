package com.topaiebiz.trade.order.core.order.action;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;

/***
 * @auth yfeng
 * @create 2017-03-27 15:23
 */
public abstract class AbstractAction implements OrderSubmitAction {

    @Resource(name = "tradeTaskExecutor")
    private ExecutorService orderExecutor;

    protected void submitTask(Runnable runnable){
        orderExecutor.submit(runnable);
    }
}