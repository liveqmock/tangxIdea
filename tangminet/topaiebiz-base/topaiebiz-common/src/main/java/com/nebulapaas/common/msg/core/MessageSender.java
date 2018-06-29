package com.nebulapaas.common.msg.core;

import com.alibaba.fastjson.JSON;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/***
 * @author yfeng
 * @date 2018-02-01 20:41
 */
@Slf4j
@Component
public class MessageSender implements InitializingBean,DisposableBean {
    private int coreSize = 2;
    private int maxSize = 5;
    private int keepAliveTime = 1;
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(500);

    @Autowired
    private List<MessageListener> listeners;

    private ExecutorService executorService;

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService = new ThreadPoolExecutor(coreSize, maxSize, keepAliveTime, TimeUnit.SECONDS, queue);
        if (CollectionUtils.isEmpty(listeners)) {
            log.info(":"+listeners.size());
            log.error("消息监听器为空 !!!!!!!");
            return;
        }
        listeners.forEach(listener -> {
            if (CollectionUtils.isEmpty(listener.getTargetMessageTypes())) {
                log.error("{} 消息监听器 没有目标消息类型 !!!!!!!", listener.getClass().getName());
            }
        });
    }

    public void publicMessage(MessageDTO msg) {
        log.info("发布消息: {}", JSON.toJSONString(msg));
        listeners.forEach(listener -> {
            Set<MessageTypeEnum> msgTypes = listener.getTargetMessageTypes();
            if (msgTypes.contains(msg.getType())) {
                executorService.submit(() -> {
                    listener.onMessage(msg);
                });
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdown();
    }
}