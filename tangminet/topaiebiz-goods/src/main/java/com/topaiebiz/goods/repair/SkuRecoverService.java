package com.topaiebiz.goods.repair;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryDao;
import com.topaiebiz.goods.repair.dto.RepairResultDTO;
import com.topaiebiz.goods.repair.service.SKURepairService;
import com.topaiebiz.goods.sku.dao.GoodsSkuDao;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

/***
 * @author yfeng
 * @date 2018-02-08 20:03
 */
@Data
@Slf4j
@Service
public class SkuRecoverService {
    private boolean start;

    @Autowired
    private BackendCategoryDao backendCategoryDao;

    @Autowired
    private GoodsSkuDao skuDao;

    @Autowired
    private SKURepairService attrRepairService;

    private Integer bathSize = 2000;
    private Long maxSkuId = 641221L;
    private static int repairCoreSize = 8;
    private static int repairTaskAlive = 1;
    private static BlockingQueue<Runnable> repairQueue = new ArrayBlockingQueue<Runnable>(1500);

    private ExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private ExecutorService skuRepairExecutor = new ThreadPoolExecutor(repairCoreSize, repairCoreSize, repairTaskAlive, TimeUnit.SECONDS, repairQueue);

    @AllArgsConstructor
    private class CategoryRepairTask implements Runnable {
        private Long categoryId;
        private CountDownLatch cdl;

        @Override
        public void run() {
            try {
                long start = System.currentTimeMillis();
                Long lastId = maxSkuId + 1L;
                Long resutCount = 0L;
                RepairResultDTO resultDTO = attrRepairService.repareGoods(categoryId, lastId, bathSize);
                lastId = resultDTO.getLastRecordId();
                resutCount += resultDTO.getResultSize();

                while (resultDTO.getResultSize() > 0) {
                    resultDTO = attrRepairService.repareGoods(categoryId, lastId, bathSize);
                    lastId = resultDTO.getLastRecordId();
                    resutCount += resultDTO.getResultSize();
                }
                log.info("类别{}修复完成，sku个数{}，耗时{}ms", categoryId, resutCount, (System.currentTimeMillis() - start));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                log.info("类别{}修复失败", categoryId);
            } finally {
                cdl.countDown();
            }
        }
    }

    private Runnable repairJob = () -> {
        if (!start) {
            log.info("任务被关闭或已结束!!!");
            return;
        }
        EntityWrapper<GoodsSkuEntity> gcIdCond = new EntityWrapper<>();
        gcIdCond.setSqlSelect("DISTINCT(gcId)");
        List<Object> gcIds = skuDao.selectObjs(gcIdCond);
        log.info("result --->>>> {}", JSON.toJSONString(gcIds));

        int categoryCount = gcIds.size();
        CountDownLatch cdl = new CountDownLatch(categoryCount);
        log.info("加载三级类别{}个", categoryCount);

        for (int i = 0; i < categoryCount; i++) {
            Object gcIdObj = gcIds.get(i);
            if (gcIdObj == null) {
                continue;
            }
            Integer gcId = (Integer) gcIdObj;
            skuRepairExecutor.submit(new CategoryRepairTask(gcId.longValue(), cdl));
        }
        try {
            cdl.await();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        log.info("任务完成!!! >>>>>>>>>");
    };

    public void addCategoryTask(Long categoryId) {
        CountDownLatch cdl = new CountDownLatch(1);
        skuRepairExecutor.submit(new CategoryRepairTask(categoryId, cdl));
        try {
            cdl.await();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        log.info("任务完成 - 类目: {}", categoryId);
    }

    public void start() {
        if (start) {
            log.warn("任务正在运行中.....");
            return;
        }
        //初始化
        start = true;

        //启动定时任务
        scheduledExecutorService.submit(repairJob);
    }
}