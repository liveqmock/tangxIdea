package com.topaiebiz.goods.repair.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.base.Stopwatch;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.nebulapaas.base.contants.Constants;
import com.topaiebiz.goods.category.backend.dao.BackendCategoryAttrDao;
import com.topaiebiz.goods.category.backend.entity.BackendCategoryAttrEntity;
import com.topaiebiz.goods.repair.PhpSerialUtil;
import com.topaiebiz.goods.repair.dao.AttrRepairDao;
import com.topaiebiz.goods.repair.dto.AttrDto;
import com.topaiebiz.goods.repair.dto.RepairResultDTO;
import com.topaiebiz.goods.repair.service.SKURepairService;
import com.topaiebiz.goods.sku.dao.GoodsSkuDao;
import com.topaiebiz.goods.sku.entity.GoodsSkuEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by hecaifeng on 2018/2/8.
 */
@Service
@Slf4j
public class SKURepairServiceImpl implements SKURepairService {

    private Logger cacheLogger = LoggerFactory.getLogger("globalPerformance");

    private boolean stoped = false;

    @Autowired
    private AttrRepairDao attrRepairDao;

    @Autowired
    private BackendCategoryAttrDao backendCategoryAttrDao;

    @Autowired
    private GoodsSkuDao skuDao;

    private static int phpCoreSize = 10;
    private static int repairCoreSize = 15;
    private static int phpTaskAlive = 1;
    private static BlockingQueue<Runnable> phpQueue = new ArrayBlockingQueue<Runnable>(25000);
    private static BlockingQueue<Runnable> repairQueue = new ArrayBlockingQueue<Runnable>(5000);
    private ExecutorService phpSerialExecutor = new ThreadPoolExecutor(phpCoreSize, phpCoreSize, phpTaskAlive, TimeUnit.SECONDS, phpQueue);
    private ExecutorService skuRepairExecutor = new ThreadPoolExecutor(repairCoreSize, repairCoreSize, phpTaskAlive, TimeUnit.SECONDS, repairQueue);

    private static final Cache<String, BackendCategoryAttrEntity> saleAttributeCache = CacheBuilder.newBuilder()
            .initialCapacity(50000)
            .concurrencyLevel(8)
            .expireAfterWrite(4, TimeUnit.HOURS)
            .build();

    private static final Cache<String, BackendCategoryAttrEntity> unsaleAttributeCache = CacheBuilder.newBuilder()
            .initialCapacity(50000)
            .concurrencyLevel(8)
            .expireAfterWrite(4, TimeUnit.HOURS)
            .build();
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            //  System.out.println("--------------------");
            //  String specName = "{\"1\":\"颜色\",\"8\":\"尺码\"}";
            String specValues = "{\"10013\":\"\\u6d45\\u68d5\",\"9954\":\"39\"}";
            // List<AttrDto> saleFields = parseSaleAttributes(specName);
            List<AttrDto> saleValues = parseSaleAttributes(specValues);
            //   System.out.println(JSON.toJSON(saleFields));
            System.out.println(JSON.toJSON(saleValues));
        }

    }
    @Data
    @AllArgsConstructor
    private class PhpSeriaTask implements Runnable {
        private int taskNum;
        private CountDownLatch cdl;
        private GoodsSkuEntity skuEntity;

        @Override
        public void run() {
            dealPHPSerial(skuEntity);
            cdl.countDown();
        }
    }

    @Data
    @AllArgsConstructor
    private class SKURepairTask implements Runnable {
        private CountDownLatch cdl;
        private List<GoodsSkuEntity> skuList;

        @Override
        public void run() {
            try {
                batchRepairSKU(skuList);
            } finally {
                cdl.countDown();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRepairSKU(List<GoodsSkuEntity> skuList) {
        for (GoodsSkuEntity sku : skuList) {
            doRepairSKU(sku);
        }
    }

    private void dealPHPSerial(GoodsSkuEntity skuEntity) {
        String saleFeildValue = PhpSerialUtil.php2Json(skuEntity.getSpecName());
        String memo = PhpSerialUtil.php2Json(skuEntity.getMemo());
        String goodsAttr = PhpSerialUtil.php2Json(skuEntity.getGoodsAttr());
        String goodsCustom = PhpSerialUtil.php2Json(skuEntity.getGoodsCustom());
        skuEntity.setSpecName(saleFeildValue);
        skuEntity.setMemo(memo);
        skuEntity.setGoodsAttr(goodsAttr);
        skuEntity.setGoodsCustom(goodsCustom);
    }

    private void dealPhpSerial(List<GoodsSkuEntity> skuLists) {
        //step 2 : 批量修复商品php序列化属性值
        Stopwatch stopwatch = Stopwatch.createStarted();
        int size = skuLists.size();
        final CountDownLatch cdl = new CountDownLatch(size);
        int taskNum = 0;
        for (GoodsSkuEntity item : skuLists) {
            phpSerialExecutor.submit(new PhpSeriaTask(++taskNum, cdl, item));
        }
        try {
            cdl.await();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        log.info("{}条数据php解序列化消耗{}ms", size, stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    private void dealBatchSKURepari(List<GoodsSkuEntity> skuLists) {
        int skuSize = skuLists.size();
        /**
         * 将sku按照后端
         */
        Map<Long, List<GoodsSkuEntity>> skuGroup = categoryGroup(skuLists);
        log.info("sku按照gcId分组:{}组", skuGroup.size());
        Stopwatch stopwatch = Stopwatch.createStarted();
        final CountDownLatch cdl = new CountDownLatch(skuGroup.size());
        for (List<GoodsSkuEntity> categoryList : skuGroup.values()) {
            skuRepairExecutor.submit(new SKURepairTask(cdl, categoryList));
        }
        try {
            cdl.await();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        log.info("{}条sku修复消耗{}ms", skuSize, stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RepairResultDTO repareGoods(Long categoryId, Long lastId, Integer taskSize) {
        Stopwatch methodStopWatch = Stopwatch.createStarted();
        RepairResultDTO repairResultDTO = new RepairResultDTO();
        if (stoped) {
            log.info(">>>>>>>>>>>>> 任务被关闭 !!!!");
            return RepairResultDTO.buildEmptyResult(lastId);
        }

        //step 1 : 批量查询SKU信息
        long queryStart = System.currentTimeMillis();
        EntityWrapper<GoodsSkuEntity> cond = new EntityWrapper<>();
        cond.eq("gcId", categoryId);
        cond.lt("id", lastId);
        cond.last(" limit " + taskSize);
        //降序
        cond.orderBy("id", false);
        List<GoodsSkuEntity> skuList = skuDao.selectList(cond);
        log.info(" >>>>>> 加载SKU记录{}条，耗时:{}ms", skuList.size(), (System.currentTimeMillis() - queryStart));
        if (CollectionUtils.isEmpty(skuList)) {
            return RepairResultDTO.buildEmptyResult(lastId);
        }

        //step 2 : 处理字段的PHP序列化内容
        dealPhpSerial(skuList);

        //step 3 : 修复销售属性和非销售属性
        for (GoodsSkuEntity sku : skuList) {
            doRepairSKU(sku);
        }

        repairResultDTO.setLastRecordId(skuList.get(skuList.size() - 1).getId());
        repairResultDTO.setResultSize(skuList.size());
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>> {} spend {} ms", JSON.toJSONString(repairResultDTO), methodStopWatch.elapsed(TimeUnit.MILLISECONDS));
        return repairResultDTO;
    }

    private Map<Long, List<GoodsSkuEntity>> categoryGroup(List<GoodsSkuEntity> skuLists) {
        Map<Long, List<GoodsSkuEntity>> result = new HashMap();
        for (GoodsSkuEntity sku : skuLists) {
            List<GoodsSkuEntity> list = result.get(sku.getGcId());
            if (list == null) {
                list = new ArrayList<>();
                result.put(sku.getGcId(), list);
            }
            list.add(sku);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean repairSKU(Long skuId) {
        GoodsSkuEntity skuEntity = skuDao.selectById(skuId);
        dealPHPSerial(skuEntity);
        doRepairSKU(skuEntity);
        return true;
    }



    private void doRepairSKU(GoodsSkuEntity item) {
        if (StringUtils.isBlank(item.getSpecName()) && StringUtils.isBlank(item.getMemo())
                && StringUtils.isBlank(item.getGoodsAttr()) && StringUtils.isBlank(item.getGoodsCustom())) {
            return;
        }

        //step 3.1 : 销售属性修复
        List<AttrDto> saleFields = parseSaleAttributes(item.getSpecName());
        List<AttrDto> saleValues = parseSaleAttributes(item.getMemo());
        if (CollectionUtils.isNotEmpty(saleFields) && CollectionUtils.isNotEmpty(saleValues)) {
            int i = 0;
            for (AttrDto attr : saleFields) {
                String name = attr.getName();
                Long gcId = item.getGcId();

                //查询或创建
                AttrDto attrDto = findOneAttr(gcId, name);
                AttrDto saleFieldValue = saleValues.get(i);
                saleFieldValue.setId(attrDto.getId());
                i++;
            }
            item.setSaleFieldValue(buildSaleFieldValue(saleValues));
        }

        //step 3.2 : 非销售属性修复
        List<Pair<String, String>> unsaleAttrs = parseGoodsAttr(item.getGoodsAttr());
        List<String> attriTextItems = new ArrayList<>();
        for (Pair<String, String> attr : unsaleAttrs) {
            AttrDto unsaleAttr = findNotSaleAttr(item.getGcId(), attr.getLeft(), attr.getRight());
            attriTextItems.add(StringUtils.join(attr.getLeft(), ":", attr.getRight()));
        }
        item.setBaseFieldValue(StringUtils.join(attriTextItems, ","));
        updateSKU(item);
    }

    @Override
    public boolean stop() {
        this.stoped = true;
        return false;
    }

    private void updateSKU(GoodsSkuEntity sku) {
        GoodsSkuEntity update = new GoodsSkuEntity();
        update.cleanInit();
        update.setId(sku.getId());
        update.setSaleFieldValue(sku.getSaleFieldValue());
        update.setBaseFieldValue(sku.getBaseFieldValue());

        update.setSpecName(sku.getSpecName());
        update.setMemo(sku.getMemo());
        update.setGoodsCustom(sku.getGoodsCustom());
        update.setGoodsAttr(sku.getGoodsAttr());
        skuDao.updateById(update);
    }

    public String buildSaleFieldValue(List<AttrDto> saleValues) {
        List<String> items = new ArrayList<>();
        for (AttrDto attrDto : saleValues) {
            items.add(StringUtils.join(attrDto.getId(), ":", attrDto.getName()));
        }
        return StringUtils.join(items, ",");
    }

    public static List<Pair<String, String>> parseGoodsAttr(String json) {
        if (StringUtils.isBlank(json)) {
            return Lists.newArrayList();
        }
        Map<Long, Map<String, String>> attrMap = JSON.parseObject(json, new TypeReference<Map<Long, Map<String, String>>>() {
        });
        if (MapUtils.isEmpty(attrMap)) {
            return Lists.newArrayList();
        }

        List<Pair<String, String>> results = new ArrayList<>();
        for (Map<String, String> item : attrMap.values()) {
            String name = null;
            String value = null;
            for (Map.Entry<String, String> itemEnty : item.entrySet()) {
                if ("name".equals(itemEnty.getKey())) {
                    name = itemEnty.getValue();
                } else {
                    value = itemEnty.getValue();
                }
            }
            results.add(new ImmutablePair<>(name, value));
        }
        return results;
    }


    public static List<AttrDto> parseSaleAttributes(String json) {
        if (StringUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        LinkedMap<Long, String> saleField = JSON.parseObject(json, new TypeReference<LinkedMap<Long, String>>() {
        });
        List<AttrDto> attrs = new ArrayList<>();
        saleField.entrySet().forEach(item -> {
            AttrDto attrDto = new AttrDto();
            attrDto.setId(item.getKey());
            attrDto.setName(item.getValue());
            attrs.add(attrDto);
        });
        return attrs;
    }

    private AttrDto findNotSaleAttr(Long gcId, String name, String value) {
        AttrDto attrDto1 = new AttrDto();

        //优先从缓存加载
        String key = StringUtils.join("unsale-", gcId, "-", name);
        BackendCategoryAttrEntity categoryAttr = unsaleAttributeCache.getIfPresent(key);
        if (categoryAttr == null) {
            long start = System.currentTimeMillis();
            BackendCategoryAttrEntity attrCond = new BackendCategoryAttrEntity();
            attrCond.clearInit();
            attrCond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            attrCond.setBelongCategory(gcId);
            attrCond.setName(name);
            categoryAttr = attrRepairDao.selectOne(attrCond);
            //放入缓存
            if (categoryAttr != null) {
                unsaleAttributeCache.put(key, categoryAttr);
            }
            cacheLogger.info("unsaleAttribute缓存未命中,从数据库中加载耗时{}ms", (System.currentTimeMillis() - start));
        } else {
            cacheLogger.info("unsaleAttribute缓存命中");
        }


        if (categoryAttr == null) {
            categoryAttr = new BackendCategoryAttrEntity();
            categoryAttr.setName(name);
            categoryAttr.setBelongCategory(gcId);
            categoryAttr.setIsSale(0);
            categoryAttr.setIsCustom(0);
            categoryAttr.setIsMust(0);
            categoryAttr.setSortNo(0);
            categoryAttr.setCreatedTime(new Date());
            categoryAttr.setValueList(value);
            attrRepairDao.insert(categoryAttr);

            //写入缓存
            unsaleAttributeCache.put(key, categoryAttr);
        } else {
            boolean addValue = updateAttributeValues(categoryAttr, value);
            if (addValue) {
                attrRepairDao.updateById(categoryAttr);

                //更新缓存
                unsaleAttributeCache.put(key, categoryAttr);
            }
        }
        attrDto1.setId(categoryAttr.getId());
        return attrDto1;
    }

    private static boolean updateAttributeValues(BackendCategoryAttrEntity categoryAttr, String newValue) {
        if (StringUtils.isBlank(categoryAttr.getValueList())) {
            categoryAttr.setValueList(newValue);
            return true;
        }
        String[] valueArray = categoryAttr.getValueList().split(",");
        List<String> valueList = Lists.newArrayList(valueArray);
        if (valueList.contains(newValue)) {
            return false;
        }
        valueList.add(newValue);
        String newListText = StringUtils.join(valueList, ",");
        categoryAttr.setValueList(newListText);
        return true;
    }

    public AttrDto findOneAttr(Long gcId, String name) {
        AttrDto attrDto = new AttrDto();

        //优先从缓存加载
        String key = StringUtils.join("sale-", gcId, "-", name);
        BackendCategoryAttrEntity categoryAttr = saleAttributeCache.getIfPresent(key);
        if (categoryAttr == null) {
            long start = System.currentTimeMillis();
            BackendCategoryAttrEntity attrCond = new BackendCategoryAttrEntity();
            attrCond.clearInit();
            attrCond.setDeleteFlag(Constants.DeletedFlag.DELETED_NO);
            attrCond.setBelongCategory(gcId);
            attrCond.setName(name);
            categoryAttr = attrRepairDao.selectOne(attrCond);

            //放入缓存
            if (categoryAttr != null) {
                saleAttributeCache.put(key, categoryAttr);
            }
            cacheLogger.info("saleAttribute缓存未命中,从数据库中加载耗时{}ms", (System.currentTimeMillis() - start));
        } else {
            cacheLogger.info("saleAttribute缓存命中");
        }

        if (categoryAttr == null) {
            categoryAttr = new BackendCategoryAttrEntity();
            categoryAttr.setName(name);
            categoryAttr.setBelongCategory(gcId);
            categoryAttr.setIsSale(1);
            categoryAttr.setIsCustom(1);
            categoryAttr.setIsMust(1);
            categoryAttr.setSortNo(0);
            categoryAttr.setCreatedTime(new Date());
            attrRepairDao.insert(categoryAttr);

            //放入缓存
            saleAttributeCache.put(key, categoryAttr);
        }
        attrDto.setId(categoryAttr.getId());
        return attrDto;
    }
}