package com.topaiebiz.goods.repair.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.base.Stopwatch;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.topaiebiz.goods.comment.dao.GoodsSkuCommentDao;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentDto;
import com.topaiebiz.goods.comment.entity.GoodsSkuCommentEntity;
import com.topaiebiz.goods.repair.dto.RepairResultDTO;
import com.topaiebiz.goods.repair.service.CommentRepairService;
import com.topaiebiz.goods.sku.dao.GoodsSkuDao;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by hecaifeng on 2018/3/26.
 */
@Slf4j
@Service
public class CommentRepairServiceImpl implements CommentRepairService {

    @Autowired
    private GoodsSkuCommentDao goodsSkuCommentDao;

    @Autowired
    private GoodsSkuDao goodsSkuDao;

    @Autowired
    private RedisCache redisCache;

    private Integer coreThreadNum = 5;
    private Integer maxThreadNUM = 10;
    private Integer aliveTime = 60;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private BlockingQueue queue = new LinkedBlockingQueue(1000);


    private ExecutorService threadPool = new ThreadPoolExecutor(coreThreadNum, maxThreadNUM, aliveTime, timeUnit, queue);

    @Override
    public void addCommentItemId() {
        EntityWrapper<GoodsSkuCommentEntity> cond = new EntityWrapper<>();
        List<GoodsSkuCommentEntity> goodsSkuCommentEntities = goodsSkuCommentDao.selectList(cond);

        List<Long> skuIds = goodsSkuCommentEntities.stream().map(goodsSkuCommentEntity -> goodsSkuCommentEntity.getSkuId()).collect(Collectors.toList());

        List<GoodsSkuEntity> goodsSkuEntityList = goodsSkuDao.selectSkuList(skuIds);

        Map<Long, GoodsSkuEntity> goodsSkuEntityMap =
                goodsSkuEntityList.stream().collect(Collectors.toMap(GoodsSkuEntity::getId, goodsSkuEntity -> goodsSkuEntity));
        for (GoodsSkuCommentEntity goodsSkuCommentEntity : goodsSkuCommentEntities) {
            GoodsSkuEntity goodsSkuEntity = goodsSkuEntityMap.get(goodsSkuCommentEntity.getSkuId());
            if (goodsSkuEntity != null) {
                goodsSkuCommentEntity.setItemId(goodsSkuEntity.getItemId());
                goodsSkuCommentDao.updateById(goodsSkuCommentEntity);
            }

        }

    }

    @Override
    public RepairResultDTO fixSkuCommoentData(Integer num, Long startId, Map<Long, String> categoryMap) {
        Stopwatch methodStopWatch = Stopwatch.createStarted();
        List<GoodsSkuCommentDto> skuCommentList = goodsSkuCommentDao.selectGoodsSkuCommetListByIdStep(num, startId);
        if (CollectionUtils.isEmpty(skuCommentList)) {
            return RepairResultDTO.buildEmptyResult(startId);
        }
        RepairResultDTO repairResultDTO = new RepairResultDTO();
        CountDownLatch cdl = new CountDownLatch(skuCommentList.size());
        for (GoodsSkuCommentDto skuComment : skuCommentList) {
            threadPool.submit(new Run(cdl, skuComment, categoryMap));
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        repairResultDTO.setResultSize(skuCommentList.size());
        repairResultDTO.setLastRecordId(skuCommentList.get(skuCommentList.size() - 1).getId());
        log.info("修复{}条评价记录消耗{}ms", skuCommentList.size(), methodStopWatch.elapsed(TimeUnit.MILLISECONDS));
        return repairResultDTO;
    }

    @Override
    public long removeCommentRedis() {
        String key = "goods_detalis_comment_";
        return redisCache.delKeys(key);
    }


    @AllArgsConstructor
    private class Run implements Runnable {
        CountDownLatch countDownLatch;
        GoodsSkuCommentDto GoodsSkuCommentDto;
        Map<Long, String> categoryMap;


        @Override
        public void run() {
            try {
                doFix(GoodsSkuCommentDto, categoryMap);
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    public void doFix(GoodsSkuCommentDto GoodsSkuCommentDto, Map<Long, String> categoryMap) {
        if (!StringUtils.isBlank(GoodsSkuCommentDto.getSaleFieldValue())) {
            String[] strList = GoodsSkuCommentDto.getSaleFieldValue().split(",");
            List<String> fieldList = new ArrayList<>();
            if (strList.length != 0) {
                for (String str : strList) {
                    String[] strList2 = str.split(":");
                    String name = categoryMap.get(Long.valueOf(strList2[0]));
                    if (!StringUtils.isBlank(name)) {
                        fieldList.add(name + ":" + strList2[1]);
                    } else {
                        fieldList.add(strList2[1]);
                    }
                }
            }
            String filedValue = fieldList.stream().collect(Collectors.joining(","));
            GoodsSkuCommentEntity goodsSkuCommentEntity = new GoodsSkuCommentEntity();
            goodsSkuCommentEntity.setId(GoodsSkuCommentDto.getId());
            goodsSkuCommentEntity.setSaleFieldValue(filedValue);
            goodsSkuCommentDao.updateById(goodsSkuCommentEntity);

        }

    }
}



