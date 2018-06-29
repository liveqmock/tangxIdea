package com.topaiebiz.member.repair;

import com.topaiebiz.member.repair.dto.RepairResultDTO;
import com.topaiebiz.member.repair.service.MemberRepairService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hecaifeng on 2018/6/20.
 */
@Slf4j
@Service
public class MemberBindRepairService {

    //初始值为true
    private boolean start;

    private Long startId = 0L;
    private Integer num = 1000;

    //创建线程池
    private ExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private RepairResultDTO repairResultDTO;

    @Autowired
    private MemberRepairService memberRepairService;

    public void start() {
        if (start) {
            log.warn("任务正在运行中.....");
            return;
        }
        //初始化
        start = true;

        //启动
        scheduledExecutorService.submit(run);
    }

    Runnable run = () -> {
        if (!start) {
            log.info("线程池被关闭或已结束!!!");
            return;
        }
        try {
            do {
                repairResultDTO = memberRepairService.updateMemberBindAccount(num, startId);
                startId = repairResultDTO.getLastRecordId();
            } while (repairResultDTO.getResultSize() != 0);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        start = false;
        log.info("修复完成!!! >>>>>>>>>");
        startId = 0L;

    };
}
