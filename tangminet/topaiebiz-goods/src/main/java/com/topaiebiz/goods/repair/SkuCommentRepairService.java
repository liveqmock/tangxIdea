package com.topaiebiz.goods.repair;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryAttrDao;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryAttrEntity;
import com.topaiebiz.goods.repair.dto.RepairResultDTO;
import com.topaiebiz.goods.repair.service.CommentRepairService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @Author tangx.w
 * @Description:
 * @Date: Create in 17:26 2018/3/30
 * @Modified by:
 */
@Slf4j
@Service
public class SkuCommentRepairService {

    private boolean start;
    private Long startId;
    private Integer num = 1000;

    @Autowired
    private CommentRepairService commentRepairService;

    private ExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private BackendCategoryAttrDao backendCategoryAttrDao;

    private RepairResultDTO repairResultDTO;

    Runnable run = () -> {
        if (!start) {
            log.info("任务被关闭或已结束!!!");
            return;
        }
        startId = 0L;
        EntityWrapper<BackendCategoryAttrEntity> categoryAttrEntity = new EntityWrapper<>();
        List<BackendCategoryAttrEntity> categoryList = backendCategoryAttrDao.selectList(categoryAttrEntity);
        Map<Long, String> categoryMap = categoryList.parallelStream().collect(Collectors.toMap(BackendCategoryAttrEntity::getId, BackendCategoryAttrEntity::getName));
        try {
            do {
                repairResultDTO = commentRepairService.fixSkuCommoentData(num, startId, categoryMap);
                startId = repairResultDTO.getLastRecordId();
            } while (repairResultDTO.getResultSize() != 0);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        start = false;
        log.info("评价修复完成!!! >>>>>>>>>");
    };


    public void start() {
        if (start) {
            log.warn("任务正在运行中.....");
            return;
        }
        //初始化
        start = true;

        //启动定时任务
        scheduledExecutorService.submit(run);
    }


}
