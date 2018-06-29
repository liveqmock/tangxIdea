package com.topaiebiz.settlement.api;

import com.google.common.base.Stopwatch;
import com.nebulapaas.common.DateUtils;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.api.StoreApi;
import com.topaiebiz.merchant.constants.MerchantConstants;
import com.topaiebiz.merchant.dto.store.StoreInfoDetailDTO;
import com.topaiebiz.settlement.service.SettlementJobService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import static com.topaiebiz.settlement.exception.StoreSettlementExceptionEnum.CREATE_SETTLEMENT_FAILURE;

/**
 * *
 *
 * @author yfeng
 * @date 2018-03-22 20:35
 */
@Slf4j
@Component
public class SettlementApiImpl implements SettlementApi,InitializingBean {
    private static final Integer FILE_DAY = 5;
    private static final Integer HALF_MONTH = 15;

    @Autowired
    private StoreApi storeApi;
    @Autowired
    private SettlementJobService settlementJobService;

    private Executor executor;
    private int taskCount = 4;
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(5);
    private Long startId = 0L;

    @Override
    public void afterPropertiesSet() throws Exception {
        executor = new ThreadPoolExecutor(taskCount, taskCount, 1, TimeUnit.MILLISECONDS, queue);
    }

    @AllArgsConstructor
    private class SettlementTask implements Runnable {
        private StoreInfoDetailDTO storeDTO;
        private CountDownLatch cdl;

        @Override
        public void run() {
            try {
                Date now = new Date();
                if(storeDTO.getNextSettleDate() == null) {
                    if(StringUtils.isBlank(storeDTO.getSettleCycle())) {
                        log.error("店铺{}结算周期不能为空", storeDTO.getId());
                        return;
                    }
                    //更新店铺下次结算时间
                    Date nextSettlementDate = settlementJobService.getNextSettlementDate(storeDTO, new Date());
                    storeApi.updateNextSettleDate(storeDTO.getId(), nextSettlementDate);
                    return;
                }

                if (DateUtils.notSameDay(storeDTO.getNextSettleDate(), now)) {
                    log.warn("店铺{}下次结算时间{}与当前时间不匹配，跳过结算任务", storeDTO.getId(), storeDTO.getNextSettleDate());
                    return;
                }
                settlementJobService.createSettlement(storeDTO);
            } catch (Exception ex) {
                log.error("店铺{}生成结算单失败", storeDTO.getId());
                log.error(ex.getMessage(), ex);
            } finally {
                cdl.countDown();
            }
        }
    }

    @Override
    public boolean startSettlementJob() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Long offsetId = startId;
        while (true) {
            List<StoreInfoDetailDTO> storeDTOs = storeApi.queryStores(offsetId, taskCount);
            if (CollectionUtils.isEmpty(storeDTOs)) {
                log.info("店铺结算认为结束,耗时{}秒", stopwatch.elapsed(TimeUnit.SECONDS));
                break;
            }
            int storeCount = storeDTOs.size();
            offsetId = storeDTOs.get(storeCount - 1).getId();

            // 并发执行任务
            CountDownLatch cdl = new CountDownLatch(storeCount);
            for (StoreInfoDetailDTO storeDTO : storeDTOs) {
                executor.execute(new SettlementTask(storeDTO, cdl));
            }

            // 等待所有任务完成，再进入下次循环
            try {
                cdl.await();
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return true;
    }

}