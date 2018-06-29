package com.topaiebiz.member.repair.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.base.Stopwatch;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.member.repair.PhpSerialUtil;
import com.topaiebiz.member.repair.dto.RepairResultDTO;
import com.topaiebiz.member.repair.service.MemberRepairService;
import com.topaiebiz.member.reserve.dao.MemberBindAccountDao;
import com.topaiebiz.member.reserve.entity.MemberBindAccountEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by hecaifeng on 2018/6/20.
 */
@Service
@Slf4j
public class MemberRepairServiceImpl implements MemberRepairService {

    @Autowired
    private MemberBindAccountDao memberBindAccountDao;

    private Integer coreThreadNum = 5;
    private Integer maxThreadNUM = 8;
    private Integer aliveTime = 60;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private BlockingQueue queue = new LinkedBlockingQueue(1000);

    private ExecutorService threadPool = new ThreadPoolExecutor(coreThreadNum, maxThreadNUM, aliveTime, timeUnit, queue);

    @Override
    public RepairResultDTO updateMemberBindAccount(Integer num, Long startId) {
        Stopwatch methodStopWatch = Stopwatch.createStarted();
        EntityWrapper<MemberBindAccountEntity> cond = new EntityWrapper<>();
        cond.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        cond.gt("id", startId);
        cond.last("limit" + " " + num);
        List<MemberBindAccountEntity> memberBindAccountEntities = memberBindAccountDao.selectList(cond);
        if (CollectionUtils.isEmpty(memberBindAccountEntities)) {
            return RepairResultDTO.buildEmptyResult(startId);
        }
        RepairResultDTO repairResultDTO = new RepairResultDTO();
        CountDownLatch cdl = new CountDownLatch(memberBindAccountEntities.size());
        for (MemberBindAccountEntity memberBindAccountEntity : memberBindAccountEntities) {
            threadPool.submit(new Run(cdl, memberBindAccountEntity));
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        repairResultDTO.setResultSize(memberBindAccountEntities.size());
        repairResultDTO.setLastRecordId(memberBindAccountEntities.get(memberBindAccountEntities.size() - 1).getId());
        log.info("修复{}条会员第三方记录消耗{}ms", memberBindAccountEntities.size(), methodStopWatch.elapsed(TimeUnit.MILLISECONDS));
        return repairResultDTO;
    }

    @AllArgsConstructor
    private class Run implements Runnable {
        CountDownLatch countDownLatch;
        MemberBindAccountEntity memberBindAccountEntity;


        @Override
        public void run() {
            try {
                doFix(memberBindAccountEntity);
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    private void doFix(MemberBindAccountEntity memberBindAccountEntity) {
        if (memberBindAccountEntity.getThirdDesc() != null) {
            String newThirdDesc = PhpSerialUtil.php2Json(memberBindAccountEntity.getThirdDesc());
            MemberBindAccountEntity memberBindAccount = new MemberBindAccountEntity();
            memberBindAccount.cleanInit();
            memberBindAccount.setMemo("1");
            memberBindAccount.setId(memberBindAccountEntity.getId());
            memberBindAccount.setThirdDesc(newThirdDesc);
            memberBindAccountDao.updateById(memberBindAccount);
        }
    }
}
