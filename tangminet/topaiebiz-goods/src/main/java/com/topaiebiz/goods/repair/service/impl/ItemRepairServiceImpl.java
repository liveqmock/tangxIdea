package com.topaiebiz.goods.repair.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.base.Stopwatch;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.goods.category.backend.dao.BackendMerchantCategoryDao;
import com.topaiebiz.goods.category.backend.entity.BackendMerchantCategoryEntity;
import com.topaiebiz.goods.comment.dao.GoodsSkuCommentDao;
import com.topaiebiz.goods.comment.entity.GoodsSkuCommentEntity;
import com.topaiebiz.goods.repair.dto.RepairResultDTO;
import com.topaiebiz.goods.repair.service.ItemRepairService;
import com.topaiebiz.goods.sku.dao.GoodsSkuDao;
import com.topaiebiz.goods.sku.dao.ItemDao;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;
import com.topaiebiz.goods.sku.entity.ItemEntity;
import com.topaiebiz.merchant.api.FreightTemplateMigrateApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

/***
 * @author yfeng
 * @date 2018-02-11 15:32
 */
@Slf4j
@Service
public class ItemRepairServiceImpl implements ItemRepairService {
    private boolean stoped = false;

    @Autowired
    private FreightTemplateMigrateApi freightTemplateMigrateApi;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private GoodsSkuDao skuDao;

    @Autowired
    private BackendMerchantCategoryDao merchantCategoryDao;

    @Autowired
    private GoodsSkuCommentDao goodsSkuCommentDao;

    private Random random = new Random();
    private static int poolSize = 10;
    private static long aliveTime = 5;
    private static BlockingQueue queue = new LinkedBlockingQueue(5000);
    private ExecutorService executorService = new ThreadPoolExecutor(poolSize, poolSize, aliveTime, TimeUnit.SECONDS, queue);

    private static final Cache<String, Double> commitionRateCache = CacheBuilder.newBuilder()
            .initialCapacity(50000)
            .concurrencyLevel(8)
            .expireAfterWrite(4, TimeUnit.HOURS)
            .build();

    @AllArgsConstructor
    private class ItemRepairTask implements Runnable {
        private List<ItemEntity> itemLists;
        private CountDownLatch cdl;

        @Override
        public void run() {
            try {
                if (CollectionUtils.isEmpty(itemLists)) {
                    return;
                }
                doRepair(itemLists);
            } finally {
                cdl.countDown();
            }
        }
    }

    @AllArgsConstructor
    private class CommentRepairTask implements Runnable {
        private List<GoodsSkuCommentEntity> goodsSkuCommentEntities;
        private CountDownLatch cdl;

        @Override
        public void run() {
            try {
                if (CollectionUtils.isEmpty(goodsSkuCommentEntities)) {
                    return;
                }
                doCommentRepair(goodsSkuCommentEntities);
            } finally {
                cdl.countDown();
            }
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean repair(Long itemId) {
        ItemEntity item = itemDao.selectById(itemId);
        return doRepair(item);
    }



   /* @Override
    public RepairResultDTO repare(Long startId, Long step) {
        Long endId = startId + step;
        log.info(">>>> {} - {}", startId, endId);
        Stopwatch methodStopWatch = Stopwatch.createStarted();
        if (stoped) {
            log.info(">>>>>>>>>>>>> 任务被关闭 !!!!");
            return RepairResultDTO.buildEmptyResult(startId);
        }

        //step 1 : 批量查询Item信息
        EntityWrapper<ItemEntity> cond = new EntityWrapper<>();
        cond.gt("id", startId);
        cond.le("id", endId);
        cond.eq("logisticsId",0);
        List<ItemEntity> itemLists = itemDao.selectList(cond);
        log.info("加载Item记录{}条", itemLists.size());
        if (CollectionUtils.isEmpty(itemLists)) {
            return RepairResultDTO.buildEmptyResult(startId);
        }
        RepairResultDTO repairResultDTO = new RepairResultDTO();

        Map<Long, List<ItemEntity>> storeGroupMap = itemStoreGroup(itemLists);
        CountDownLatch cdl = new CountDownLatch(storeGroupMap.size());
        for (List<ItemEntity> storeItems : storeGroupMap.values()) {
            ItemRepairTask itemRepairTask = new ItemRepairTask(storeItems, cdl);
            executorService.submit(itemRepairTask);
        }
        try {
            cdl.await();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        repairResultDTO.setResultSize(itemLists.size());
        repairResultDTO.setLastRecordId(itemLists.get(itemLists.size() - 1).getId());
        log.info("修复{}条记录消耗{}ms", itemLists.size(), methodStopWatch.elapsed(TimeUnit.MILLISECONDS));
        return repairResultDTO;
    }*/

    @Override
    public RepairResultDTO repare(Long startId, Long step) {
        Long endId = startId + step;
        log.info(">>>> {} - {}", startId, endId);
        Stopwatch methodStopWatch = Stopwatch.createStarted();
        if (stoped) {
            log.info(">>>>>>>>>>>>> 任务被关闭 !!!!");
            return RepairResultDTO.buildEmptyResult(startId);
        }

        //step 1 : 批量查询Item信息
        EntityWrapper<ItemEntity> cond = new EntityWrapper<>();
        cond.gt("id", startId);
        cond.le("id", endId);
        List<ItemEntity> itemLists = itemDao.selectList(cond);
        log.info("加载Item记录{}条", itemLists.size());
        if (CollectionUtils.isEmpty(itemLists)) {
            return RepairResultDTO.buildEmptyResult(startId);
        }
        RepairResultDTO repairResultDTO = new RepairResultDTO();

        Map<Long, List<GoodsSkuCommentEntity>> goodsSkuCommentMap = goodsSkuCommentMap(itemLists);
        CountDownLatch cdl = new CountDownLatch(goodsSkuCommentMap.size());
        for (List<GoodsSkuCommentEntity> goodsSkuCommentEntities : goodsSkuCommentMap.values()) {
            CommentRepairTask commentRepairTask = new CommentRepairTask(goodsSkuCommentEntities, cdl);
            executorService.submit(commentRepairTask);
        }
        try {
            cdl.await();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        repairResultDTO.setResultSize(itemLists.size());
        repairResultDTO.setLastRecordId(itemLists.get(itemLists.size() - 1).getId());
        log.info("修复{}条记录消耗{}ms", itemLists.size(), methodStopWatch.elapsed(TimeUnit.MILLISECONDS));
        return repairResultDTO;
    }

    private Map<Long, List<GoodsSkuCommentEntity>> goodsSkuCommentMap(List<ItemEntity> itemLists) {
        Map<Long, List<GoodsSkuCommentEntity>> goodsSkuCommentMap = new HashMap<>();
        for (ItemEntity item : itemLists) {
            EntityWrapper<GoodsSkuCommentEntity> cond = new EntityWrapper<>();
            cond.eq("itemId", item.getId());
            List<GoodsSkuCommentEntity> goodsSkuCommentList = goodsSkuCommentDao.selectList(cond);
            if (CollectionUtils.isNotEmpty(goodsSkuCommentList)) {
                goodsSkuCommentMap.put(item.getId(), goodsSkuCommentList);
            }
        }
        return goodsSkuCommentMap;
    }

    ;

    private void isCommentTime(List<GoodsSkuCommentEntity> commentLists, GoodsSkuCommentEntity comment) {
        boolean needUpdate = false;
        for (GoodsSkuCommentEntity goodsSkuComment : commentLists) {
            if (!goodsSkuComment.getId().equals(comment.getId())) {
                if (comment.getCreatedTime().equals(goodsSkuComment.getCreatedTime())) {
                    needUpdate = true;
                    break;
                }
            }
        }

        if (!needUpdate) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(comment.getCreatedTime());
        cal.add(Calendar.MINUTE, random.nextInt(40) + 10);
        cal.add(Calendar.SECOND, random.nextInt(100));
        cal.add(Calendar.DAY_OF_YEAR, -random.nextInt(10));
        GoodsSkuCommentEntity goodsSkuComment = new GoodsSkuCommentEntity();
        goodsSkuComment.cleanInit();
        goodsSkuComment.setId(comment.getId());
        goodsSkuComment.setCreatedTime(cal.getTime());
        goodsSkuCommentDao.updateById(goodsSkuComment);
    }

    @Transactional(rollbackFor = Exception.class)
    public void doCommentRepair(List<GoodsSkuCommentEntity> goodsSkuCommentList) {
        for (GoodsSkuCommentEntity goodsSkuCommentEntity : goodsSkuCommentList) {
            isCommentTime(goodsSkuCommentList, goodsSkuCommentEntity);
        }
    }

    private boolean doRepair(ItemEntity item) {
        //修复运费模板
        repairFreightTemplate(item);

        //修复积分消费比例
        // repairScoreRate(item);

        //   repairBrokerageRatio(item);

        //修复imageField
        // repairImageField(item);
        return true;
    }

    private Map<Long, List<ItemEntity>> itemStoreGroup(List<ItemEntity> itemLists) {
        Map<Long, List<ItemEntity>> storeGroupMap = new HashMap();
        for (ItemEntity item : itemLists) {
            List<ItemEntity> storeList = storeGroupMap.get(item.getBelongStore());
            if (storeList == null) {
                storeList = new ArrayList<>();
                storeGroupMap.put(item.getBelongStore(), storeList);
            }
            storeList.add(item);
        }
        return storeGroupMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doRepair(List<ItemEntity> itemLists) {
        for (ItemEntity item : itemLists) {
            doRepair(item);
        }
    }

    private void repairFreightTemplate(ItemEntity item) {
        Long freightTemplateId = null;
        //如果与其他店铺有公用，则为此店铺单独拷贝一份出来
        if (item.getLogisticsId() != null && item.getLogisticsId() > 0) {
            freightTemplateId = item.getLogisticsId();
            if (supportStoreCount(freightTemplateId, item.getBelongStore()) > 0) {
                //多个店铺公用着一个运费模板
                Long newTemplateId = freightTemplateMigrateApi.copyTemplate(freightTemplateId, item.getBelongStore());
                freightTemplateId = newTemplateId;

                //将item挂到新建的运费模板上
                updateItemTemplateId(item.getId(), newTemplateId);
                item.setLogisticsId(newTemplateId);
            }
            BigDecimal maxFixFreight = querySKUMaxFixFreight(item.getId());
            if (maxFixFreight != null) {
                //存在商品SKU使用固定运费，则修正新建一条默认运费模板详情
                freightTemplateMigrateApi.createDefaultTemplateDetail(freightTemplateId, maxFixFreight);
            }
            return;
        } else {
            BigDecimal maxFixFreight = querySKUMaxFixFreight(item.getId());
            if (maxFixFreight == null) {
                //默认10元运费
                maxFixFreight = new BigDecimal(10);
            }
            Long templateId = freightTemplateMigrateApi.createTemplate(item.getBelongStore(), maxFixFreight);
            updateItemTemplateId(item.getId(), templateId);
        }
    }

    private void updateItemTemplateId(Long itemId, Long templateId) {
        ItemEntity update = new ItemEntity();
        update.cleanInit();
        update.setId(itemId);
        update.setLogisticsId(templateId);
        itemDao.updateById(update);
    }

    private double getClassBrokerageRation(Long storeId, Long classId) {
        String key = StringUtils.join(storeId, "-", classId);
        Double val = commitionRateCache.getIfPresent(key);
        if (val == null) {
            BackendMerchantCategoryEntity cond = new BackendMerchantCategoryEntity();
            cond.cleanInit();
            cond.setCategoryId(classId);
            cond.setStoreId(storeId);
            BackendMerchantCategoryEntity conf = merchantCategoryDao.selectOne(cond);
            if (conf != null) {
                val = conf.getBrokerageRatio();
            } else {
                val = 0D;
            }
            commitionRateCache.put(key, val);
        }
        return val;
    }

    private void repairBrokerageRatio(ItemEntity item) {
        Long storeId = item.getBelongStore();
        Long classId = item.getBelongCategory();
        Double brokerageRation = getClassBrokerageRation(storeId, classId);

        ItemEntity update = new ItemEntity();
        update.cleanInit();
        update.setId(item.getId());
        update.setBrokerageRatio(brokerageRation);
        itemDao.updateById(update);
    }

    private void repairScoreRate(ItemEntity item) {

    }

    private void repairImageField(ItemEntity item) {

    }

    private Integer supportStoreCount(Long templateId, Long storeId) {
        return itemDao.countStore(templateId, storeId);
    }

    private BigDecimal querySKUMaxFixFreight(Long itemId) {
        EntityWrapper<GoodsSkuEntity> cond = new EntityWrapper<>();
        cond.eq("itemId", itemId);
        cond.gt("goodsFreight", 0);
        List<GoodsSkuEntity> skuLists = skuDao.selectList(cond);
        if (CollectionUtils.isEmpty(skuLists)) {
            return null;
        }
        BigDecimal freight = BigDecimal.ZERO;
        for (GoodsSkuEntity sku : skuLists) {
            if (sku.getGoodsfreight().compareTo(freight) > 0) {
                freight = sku.getGoodsfreight();
            }
        }
        return freight;
    }

    @Override
    public boolean stop() {
        this.stoped = true;
        return false;
    }

    @Override
    public void updateSaleVolume() {
        EntityWrapper<ItemEntity> itemEntityEntity = new EntityWrapper<>();
        itemEntityEntity.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<ItemEntity> itemEntities = itemDao.selectList(itemEntityEntity);
        for (ItemEntity itemEntity : itemEntities) {
            Long salesVolome = itemDao.selectSalesVolomeById(itemEntity.getId());
            itemEntity.setSalesVolume(salesVolome);
            itemDao.updateById(itemEntity);
        }
    }
}