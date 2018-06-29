package com.topaiebiz.goods.repair;

import com.alibaba.fastjson.JSON;
import com.topaiebiz.goods.repair.dto.RepairResultDTO;
import com.topaiebiz.goods.repair.service.ItemRepairService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * @author yfeng
 * @date 2018-02-11 15:27
 */
@Slf4j
@Service
public class ItemRecoverService {

    private boolean start;

    @Autowired
    private ItemRepairService itemRepairService;

    private Integer bathSize = 5000;

    private ExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private Runnable job = () -> {
        if (!start) {
            log.info("任务被关闭或已结束!!!");
            return;
        }
        try {
            Long lastId = 0L;
            Long step = 5000L;
            Long maxId = 220000L;

            RepairResultDTO resultDTO = itemRepairService.repare(lastId,  step);
            lastId = lastId + step;
            while (lastId < maxId) {
                resultDTO = itemRepairService.repare(lastId, step);
                log.info(">>>> {}", JSON.toJSONString(resultDTO));
                lastId = lastId + step;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        log.info("任务完成!!! >>>>>>>>>");
    };

    public void start() {
        if (start) {
            log.warn("任务正在运行中.....");
            return;
        }
        //初始化
        start = true;

        //启动定时任务
        scheduledExecutorService.submit(job);
    }
}