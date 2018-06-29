package com.topaiebiz.promotion.mgmt.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.BeanCopyUtil;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.basic.api.ConfigApi;
import com.topaiebiz.goods.api.BackendCategoryApi;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.api.GoodsSkuApi;
import com.topaiebiz.goods.constants.GoodsConstants;
import com.topaiebiz.goods.dto.sku.GoodsSkuDTO;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.mgmt.dao.PromotionDao;
import com.topaiebiz.promotion.mgmt.dao.PromotionEntryDao;
import com.topaiebiz.promotion.mgmt.dao.PromotionGoodsDao;
import com.topaiebiz.promotion.mgmt.dao.PromotionStoresDao;
import com.topaiebiz.promotion.mgmt.dto.HomeSeckillDto;
import com.topaiebiz.promotion.mgmt.dto.HomeSeckillGoodsDTO;
import com.topaiebiz.promotion.mgmt.dto.PromotionDto;
import com.topaiebiz.promotion.mgmt.dto.PromotionGoodsDto;
import com.topaiebiz.promotion.mgmt.dto.sec.kill.PromotionGoodsDTO;
import com.topaiebiz.promotion.mgmt.entity.PromotionEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionEntryEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionGoodsEntity;
import com.topaiebiz.promotion.mgmt.entity.PromotionStoresEntity;
import com.topaiebiz.promotion.mgmt.exception.PromotionExceptionEnum;
import com.topaiebiz.promotion.mgmt.service.PromotionGoodsService;
import com.topaiebiz.promotion.promotionEnum.PromotionTypeEnum;
import com.topaiebiz.promotion.util.SaleRateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.topaiebiz.promotion.constants.PromotionConstants.CacheKey.*;
import static com.topaiebiz.promotion.promotionEnum.PromotionStateEnum.PROMOTION_STATE_NOT_START;
import static com.topaiebiz.promotion.promotionEnum.PromotionStateEnum.PROMOTION_STATE_ONGOING;

/**
 * Description 营销活动商品
 * <p>
 * <p>
 * Author Joe
 * <p>
 * Date 2017年10月13日 下午4:43:29
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
@Slf4j
public class PromotionGoodsServiceImpl implements PromotionGoodsService {
    private static final DecimalFormat df = new DecimalFormat("0.00");//保留两位小数点
    //活动秒杀ID集合CODE
    private final static String SEC_KILL_IDS = "sec_kill_ids";
    //秒杀场次CODE
    private final static String SEC_KILL_NUM = "sec_kill_num";

    // 营销活动商品
    @Autowired
    private PromotionGoodsDao promotionGoodsDao;

    // 营销活动数据层
    @Autowired
    private PromotionDao promotionDao;

    // 营销活动商家报名
    @Autowired
    private PromotionEntryDao promotionEntryDao;

    // sku 商品
    @Autowired
    private GoodsSkuApi goodsSkuApi;

    // item商品
    @Autowired
    private GoodsApi goodsApi;

    // 商品类目
    @Autowired
    private BackendCategoryApi backendCategoryApi;

    @Autowired
    private ConfigApi configApi;

    @Autowired
    private PromotionStoresDao promotionStoresDao;

    @Autowired
    private RedisCache redisCache;

    private Random random = new Random();

    /**
     * Description 查询首页秒杀活动
     * <p>
     * Author Joe
     *
     * @return
     * @throws ParseException
     */
    @Override
    public HomeSeckillDto getHomePageSeckill() throws ParseException {
        /** 返回数据 */
        HomeSeckillDto homeSecKill = new HomeSeckillDto();
        //获取正在秒杀的列表
        List<PromotionEntity> promotionEntities = getSecKillPromotions(null, true, null);
        if (CollectionUtils.isNotEmpty(promotionEntities)) {
            for (PromotionEntity promotion : promotionEntities) {
                //符合规则的展示数据
                BeanCopyUtil.copy(promotion, homeSecKill);
                // 获取秒杀活动商品列表
                List<HomeSeckillGoodsDTO> homeGoodsList = getSecKillGoodsList(homeSecKill, true);
                if (CollectionUtils.isEmpty(homeGoodsList)) {
                    continue;
                }

                homeSecKill.setNowTime(new Date());
                homeSecKill.setPromotionGoodsDtos(homeGoodsList);
                break;

            }
        }
        return homeSecKill;
    }

    /**
     * 获取活动列表
     *
     * @param ids       活动ID集合
     * @param ongoing   是否必须限定在进行中
     * @param plateCode 锁价模块
     * @return
     */
    private List<PromotionEntity> getSecKillPromotions(List<Long> ids, Boolean ongoing, String plateCode) {
        //缓存CODE
        String redisCode;
        if (ongoing) {   //首页正在秒杀的列表
            redisCode = SEC_KILL_GOODS_ONGOING;
        } else if (plateCode == null) {     //C端日常秒杀
            redisCode = SEC_KILL_GOODS_DAILY;
        } else {        //锁价列表
            redisCode = StringUtils.join(PLATE_GOODS_PREFIX, plateCode);
        }
        List<PromotionEntity> secKillList = redisCache.getListValue(redisCode, PromotionEntity.class);
        //缓存中没有数据
        if (CollectionUtils.isEmpty(secKillList)) {
            //查询有效的限时秒杀活动
            EntityWrapper<PromotionEntity> promotionCond = new EntityWrapper<>();
            if (CollectionUtils.isNotEmpty(ids)) {
                promotionCond.in("id", ids);
            }
            promotionCond.eq("typeId", PromotionTypeEnum.PROMOTION_TYPE_SECKILL.getCode());
            promotionCond.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
            //状态：即将开始，进行中
            List<Integer> marketStates = new ArrayList<>();
            if (!ongoing) {
                marketStates.add(PROMOTION_STATE_NOT_START.getCode());
            }
            marketStates.add(PROMOTION_STATE_ONGOING.getCode());
            promotionCond.in("marketState", marketStates);
            //限制结束时间，避免状态未及时修改的情况
            promotionCond.gt("endTime", new Date());
            promotionCond.orderBy("startTime, createdTime");
            secKillList = promotionDao.selectList(promotionCond);

            //过期时间1分钟 + 随机补充1-15秒
            redisCache.set(redisCode, secKillList, 60 + random.nextInt(15));
        }
        return secKillList;
    }

    /**
     * 获取秒杀活动商品列表
     *
     * @param homeSecKill 秒杀活动
     * @param isHome      是否是首页秒杀精选
     * @return
     */
    private List<HomeSeckillGoodsDTO> getSecKillGoodsList(HomeSeckillDto homeSecKill, Boolean isHome) {
        //秒杀商品列表
        List<PromotionGoodsDTO> pgList = getPromotionGoodsList(homeSecKill.getId());
        if (CollectionUtils.isEmpty(pgList)) {
            return null;
        }

        Map<Long, List<PromotionGoodsDTO>> pgMap = pgList.stream().collect(Collectors.groupingBy(PromotionGoodsDTO::getItemId));

        //商品详情
        List<ItemDTO> sortedItemDTOS = getItems(homeSecKill.getId(), pgList);

        //是否首页展示
        if (isHome) {
            //取首页规定展示的数量
            if (sortedItemDTOS.size() > PromotionConstants.SecKillGoodsNum.SECKILL_GOODS_NUM) {
                sortedItemDTOS = sortedItemDTOS.subList(0, PromotionConstants.SecKillGoodsNum.SECKILL_GOODS_NUM);
            }
        }
        // 获取秒杀活动商品列表
        return getHomeSeckillGoodsDtoList(sortedItemDTOS, pgMap, homeSecKill);
    }

    /**
     * 根据活动ID获取商品列表
     *
     * @param promotionId
     * @return
     */
    private List<PromotionGoodsDTO> getPromotionGoodsList(Long promotionId) {
        String redisCode = StringUtils.join(SEC_KILL_GOODS_LIST_PREFIX, promotionId);
        List<PromotionGoodsDTO> promotionGoodsList = redisCache.getListValue(redisCode, PromotionGoodsDTO.class);
        //缓存数据为空
        if (CollectionUtils.isEmpty(promotionGoodsList)) {
            EntityWrapper<PromotionGoodsEntity> goodsEntityWrapper = new EntityWrapper<>();
            // 查询条件
            goodsEntityWrapper.eq("promotionId", promotionId);
            goodsEntityWrapper.eq("state", PromotionConstants.AuditState.APPROVED_AUDIT);
            goodsEntityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
            goodsEntityWrapper.orderBy("id", true);
            List<PromotionGoodsEntity> entityList = promotionGoodsDao.selectList(goodsEntityWrapper);

            promotionGoodsList = BeanCopyUtil.copyList(entityList, PromotionGoodsDTO.class);
            redisCache.set(redisCode, promotionGoodsList, 60 + random.nextInt(15));
        }
        // 营销活动所有商品列表
        return promotionGoodsList;
    }

    /**
     * 获取商品详情
     *
     * @param promotionId 活动ID
     * @param pgList      秒杀商品列表
     * @return
     */
    private List<ItemDTO> getItems(Long promotionId, List<PromotionGoodsDTO> pgList) {
        String redisCode = StringUtils.join(SEC_KILL_ITEMS_PREFIX, promotionId);
        List<ItemDTO> sortedItems = redisCache.getListValue(redisCode, ItemDTO.class);
        if (CollectionUtils.isEmpty(sortedItems)) {
            List<Long> itemIds = pgList.stream().map(promotion -> promotion.getItemId()).distinct().collect(Collectors.toList());
            List<ItemDTO> items = goodsApi.getItemMap(itemIds);
            if (CollectionUtils.isEmpty(items)) {
                return null;
            }

            //将item按照promotionGoods顺序加载
            Map<Long, ItemDTO> itemMap = items.stream().collect(Collectors.toMap(ItemDTO::getId, item -> item));
            sortedItems = new ArrayList<>(items.size());
            Set<Long> itemIdSet = new HashSet<>();
            for (PromotionGoodsDTO promotionGoods : pgList) {
                Long itemId = promotionGoods.getItemId();
                if (itemIdSet.contains(itemId)) {
                    continue;
                }
                ItemDTO item = itemMap.get(itemId);
                if (item == null) {
                    continue;
                }
                sortedItems.add(item);
                itemIdSet.add(itemId);
            }
            redisCache.set(redisCode, sortedItems, 60);
        }
        return sortedItems;
    }

    @Override
    public List<HomeSeckillDto> getSeckillList(String plateCode) {
        List<Long> secKillIds = null;
        //活动期间-展示特定的秒杀列表
        if (plateCode != null) {
            //活动期间-秒杀ID集合
            secKillIds = configApi.convertValueToList(plateCode);
            //config中未配置值
            if (CollectionUtils.isEmpty(secKillIds)) {
                log.warn("未配置 CODE为{} 的值！", plateCode);
                return new ArrayList<>();
            }
        }
        //获取秒杀列表
        List<PromotionEntity> promotionList = getSecKillPromotions(secKillIds, false, plateCode);

        List<HomeSeckillDto> secKillList = BeanCopyUtil.copyList(promotionList, HomeSeckillDto.class);
        if (CollectionUtils.isEmpty(secKillList)) {
            return new ArrayList<>();
        }
        //秒杀限制场次数
        Integer num = secKillList.size();
        if (plateCode == null || SEC_KILL_IDS.equals(plateCode)) {
            String secBatch = configApi.getConfig(SEC_KILL_NUM);

            if (StringUtils.isNotEmpty(secBatch) && secKillList.size() > Integer.parseInt(secBatch)) {
                num = Integer.parseInt(secBatch);
            }
        }

        //需要展示的秒杀集合
        List<HomeSeckillDto> showList = secKillList.subList(0, num);
        if (CollectionUtils.isEmpty(showList)) {
            return new ArrayList<>();
        }

        for (HomeSeckillDto secKill : showList) {
            // 获取秒杀活动商品列表
            List<HomeSeckillGoodsDTO> secKillGoodsList = getSecKillGoodsList(secKill, false);
            secKill.setNowTime(new Date());
            secKill.setPromotionGoodsDtos(secKillGoodsList);
        }

        return showList;
    }

    /**
     * 获取秒杀活动商品列表
     *
     * @param itemDTOS
     * @param pgMap
     * @param homeSecKill
     * @return
     */
    private List<HomeSeckillGoodsDTO> getHomeSeckillGoodsDtoList(List<ItemDTO> itemDTOS, Map<Long, List<PromotionGoodsDTO>> pgMap, HomeSeckillDto homeSecKill) {
        List<HomeSeckillGoodsDTO> homeSeckillGoodsDTOS = new ArrayList<>();
        for (ItemDTO item : itemDTOS) {
            HomeSeckillGoodsDTO secKillGoods = new HomeSeckillGoodsDTO();
            BeanCopyUtil.copy(item, secKillGoods);
            // 销量
            Long salesVolome = 0L;
            // 活动数量
            Integer promotionNum = 0;

            List<PromotionGoodsDTO> pgList = pgMap.get(item.getId());
            if (CollectionUtils.isEmpty(pgList)) {
                continue;
            }

            for (PromotionGoodsDTO promotionGoods : pgList) {
                // 计算活动销量
                if (null != promotionGoods.getQuantitySales()) {
                    salesVolome += promotionGoods.getQuantitySales();
                }
                // 计算活动数量
                if (null != promotionGoods.getPromotionNum()) {
                    promotionNum += promotionGoods.getPromotionNum();
                }
            }
            secKillGoods.setSalesVolome(salesVolome);
            secKillGoods.setPromotionNum(promotionNum);

            //真实的进度
            Integer realProgress = 0;
            if (!promotionNum.equals(0)) {
                realProgress = salesVolome.intValue() * 100 / promotionNum;
            }
            //通过规则计算出的销售进度
            Integer ruleProgress = SaleRateUtil.rendRate(item.getId(), homeSecKill.getStartTime(), homeSecKill.getEndTime());
            //进度条
            Integer progress = realProgress > ruleProgress ? realProgress : ruleProgress;

            //清楚已售完的活动商品
            if (progress.intValue() == 100) {
                cleanPromotionGoods(homeSecKill.getId(), item.getId());
                continue;
            }
            secKillGoods.setProgress(progress);

            List<BigDecimal> priceList = pgList.stream().map(pg -> pg.getPromotionPrice()).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(priceList)) {
                secKillGoods.setDefaultPrice(Collections.min(priceList));
            }

            homeSeckillGoodsDTOS.add(secKillGoods);
        }
        return homeSeckillGoodsDTOS;
    }

    /**
     * Description 根据营销活动id查询活动商品分页列表
     * <p>
     * Author Joe
     *
     * @param promotionDto
     * @return
     */
    @Override
    public PageInfo<ItemDTO> getPromotionApplicableGoods(PromotionDto promotionDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageSize(promotionDto.getPageSize());
        pagePO.setPageNo(promotionDto.getPageNo());
        // 商品分页数据
        Page<ItemDTO> page = PageDataUtil.buildPageParam(pagePO);
        List<ItemDTO> itemDTOS = new ArrayList<ItemDTO>();
        PromotionGoodsDto promotionGoodsDto = new PromotionGoodsDto();
        /** 列表查询条件 */
        promotionGoodsDto.setPromotionId(promotionDto.getId());
        PromotionEntity promotionEntity = promotionDao.selectById(promotionDto.getId());
        if (promotionEntity == null) {
            throw new GlobalException(PromotionExceptionEnum.ACTIVITY_DOES_NOT_EXIST);
        }
        // 是否指定商品/店铺可用
        Integer isGoodsArea = promotionEntity.getIsGoodsArea();
        if (isGoodsArea.equals(PromotionConstants.IsGoodsArea.NOT_ALL) || isGoodsArea.equals(PromotionConstants.IsGoodsArea.EXCLUDING_PART_OF_THE_GOODS)) {
            /** 指定商品可用/不可用 */
            if (promotionEntity.getSponsorType() == null) {
                promotionGoodsDto.setState(PromotionConstants.AuditState.APPROVED_AUDIT);
            } else {
                promotionGoodsDto.setStoreId(promotionEntity.getSponsorType());
            }
            promotionGoodsDto.setDeletedFlag(PromotionConstants.DeletedFlag.DELETED_NO);
            List<PromotionGoodsDto> promotionGoodsDtos = promotionGoodsDao.selectPromotionApplicableGoods(promotionGoodsDto);
            // 获取itemId
            List<Long> itemIds = promotionGoodsDtos.stream().map(promotionGoods -> promotionGoods.getItemId()).collect(Collectors.toList());
            if (isGoodsArea.equals(PromotionConstants.IsGoodsArea.NOT_ALL)) {
                // 指定商品可用
                itemDTOS = goodsApi.getItemMap(itemIds);
            } else {
                // 指定商品不可用
                itemDTOS = goodsApi.getItemDTOs(itemIds, promotionEntity.getSponsorType(), page);
            }

        } else if (isGoodsArea.equals(PromotionConstants.IsGoodsArea.ALL) || isGoodsArea.equals(PromotionConstants.IsGoodsArea.ALL_STORE)) {
            /** 当活动默认选择所有商品时 */
            itemDTOS = goodsApi.getItems(promotionEntity.getSponsorType(), page);
        } else if (isGoodsArea.equals(PromotionConstants.IsGoodsArea.INCLUDE_STORE) || isGoodsArea.equals(PromotionConstants.IsGoodsArea.EXCLUDE_STORE)) {

            // 查询条件
            EntityWrapper<PromotionStoresEntity> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
            entityWrapper.eq("promotionId", promotionDto.getId());
            List<PromotionStoresEntity> promotionStoresEntities = promotionStoresDao.selectList(entityWrapper);
            // 平台活动所圈店铺
            List<Long> storeIds = promotionStoresEntities.stream().map(promotionStores -> promotionStores.getStoreId()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(storeIds)) {
                if (isGoodsArea.equals(PromotionConstants.IsGoodsArea.INCLUDE_STORE)) {
                    // 指定部分店铺可用
                    goodsApi.getStoreItems(storeIds, page, GoodsConstants.BelongStore.YES_BELONGSTORE);
                } else {
                    // 指定部分店铺不可用
                    goodsApi.getStoreItems(storeIds, page, GoodsConstants.BelongStore.NO_BELONGSTORE);
                }
            }
        }
        page.setRecords(itemDTOS);
        return PageDataUtil.copyPageInfo(page);
    }

    /**
     * Description 商家报名商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     * @return
     */
    @Override
    public PageInfo<PromotionGoodsDto> getStoreEnrolGoodsList(PromotionGoodsDto promotionGoodsDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(promotionGoodsDto.getPageNo());
        pagePO.setPageSize(promotionGoodsDto.getPageSize());
        Page<PromotionGoodsDto> page = PageDataUtil.buildPageParam(pagePO);
        boolean equal = false;
        List<PromotionGoodsDto> promotionGoodsList = promotionGoodsDao.selectStoreEnrolGoodsList(page, promotionGoodsDto);
        for (PromotionGoodsDto promotionGoods : promotionGoodsList) {
            /** 库存*/
            Integer repertoryNum = promotionGoods.getRepertoryNum();
            Long promotionId = promotionGoods.getPromotionId();
            Long storeId = promotionGoods.getStoreId();
            Long itemId = promotionGoods.getItemId();
            if (itemId != null && storeId != null && promotionId != null) {
                EntityWrapper<PromotionGoodsEntity> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
                entityWrapper.eq("promotionId", promotionId);
                entityWrapper.eq("storeId", storeId);
                entityWrapper.eq("itemId", itemId);
                List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(entityWrapper);
                for (PromotionGoodsEntity promotionGoods2 : promotionGoodsEntities) {
                    /** 商品库存*/
                    if (promotionGoods2.getRepertoryNum() != null) {
                        repertoryNum += promotionGoods2.getRepertoryNum();
                    }
                    /** 判断该item商品下所有的sku商品设置的活动价格与活动数量和id限购是否一致*/
                    if (promotionGoods2.getPromotionPrice() != null && promotionGoods2.getPromotionNum() != null && promotionGoods2.getConfineNum() != null) {
                        for (PromotionGoodsEntity promotionGoods3 : promotionGoodsEntities) {
                            if (promotionGoods2.getPromotionPrice().equals(promotionGoods3.getPromotionPrice())) {
                                if (promotionGoods2.getPromotionNum().equals(promotionGoods3.getPromotionNum())) {
                                    if (promotionGoods2.getConfineNum().equals(promotionGoods3.getConfineNum())) {
                                        equal = true;
                                    }
                                }
                            }
                        }
                    }
                    if (equal) {
                        promotionGoods.setPromotionPrice(promotionGoods2.getPromotionPrice());
                        promotionGoods.setPromotionNum(promotionGoods2.getPromotionNum());
                        promotionGoods.setConfineNum(promotionGoods2.getConfineNum());
                    }
                }
                promotionGoods.setRepertoryNum(repertoryNum);
            }
        }
        page.setRecords(promotionGoodsList);
        return PageDataUtil.copyPageInfo(page);
    }

    /**
     * Description 报名商家商品审核列表
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     * @return
     */
    @Override
    public PageInfo<PromotionGoodsDto> getStoreGoodsAuditList(PromotionGoodsDto promotionGoodsDto) {
        PagePO pagePO = new PagePO();
        pagePO.setPageNo(promotionGoodsDto.getPageNo());
        pagePO.setPageSize(promotionGoodsDto.getPageSize());
        Page<PromotionGoodsDto> page = PageDataUtil.buildPageParam(pagePO);
        PromotionEntity promotionEntity = promotionDao.selectById(promotionGoodsDto.getPromotionId());
        List<PromotionGoodsDto> promotionGoodsDtoList = promotionGoodsDao.selectStoreGoodsAuditList(page, promotionGoodsDto);
        if (!CollectionUtils.isEmpty(promotionGoodsDtoList)) {
            for (PromotionGoodsDto promotionGoods : promotionGoodsDtoList) {
                /** 判断item商品下参与活动的sku商品的数值是否一直*/
                boolean equal = false;
                /** 通过*/
                Integer adopt = 0;
                /** 不通过*/
                Integer unAdopt = 0;
                /** 审核*/
                Integer audit = 0;
                // 库存
                Integer repertoryNum = 0;
                PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
                promotionGoodsEntity.clearInit();
                // 根据活动id，itemId查询数据
                promotionGoodsEntity.setPromotionId(promotionGoodsDto.getPromotionId());
                promotionGoodsEntity.setItemId(promotionGoods.getItemId());
                promotionGoodsEntity.setStoreId(promotionGoods.getStoreId());
                promotionGoodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
                // 查询该活动下所属item商品的所有sku商品
                List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(new EntityWrapper<>(promotionGoodsEntity));
                for (PromotionGoodsEntity promotionGoodsDto2 : promotionGoodsEntities) {
                    if (promotionGoodsDto2.getRepertoryNum() != null) {
                        repertoryNum += promotionGoodsDto2.getRepertoryNum();
                    }
                    /** 判断该item商品下所有的sku商品设置的活动价格与活动数量和id限购是否一致*/
                    if (promotionGoodsDto2.getPromotionPrice() != null && promotionGoodsDto2.getPromotionNum() != null && promotionGoodsDto2.getConfineNum() != null) {
                        for (PromotionGoodsEntity promotionGoodsDto3 : promotionGoodsEntities) {
                            if (promotionGoodsDto2.getPromotionPrice().equals(promotionGoodsDto3.getPromotionPrice())) {
                                if (promotionGoodsDto2.getPromotionNum().equals(promotionGoodsDto3.getPromotionNum())) {
                                    if (promotionGoodsDto2.getConfineNum().equals(promotionGoodsDto3.getConfineNum())) {
                                        equal = true;
                                    }
                                }
                            }
                        }
                    }
                    if (equal) {
                        promotionGoods.setPromotionPrice(promotionGoodsDto2.getPromotionPrice());
                        promotionGoods.setPromotionNum(promotionGoodsDto2.getPromotionNum());
                        promotionGoods.setConfineNum(promotionGoodsDto2.getConfineNum());
                    }
                    if (promotionGoodsDto2.getState() == null) {
                        audit++;
                    } else if (promotionGoodsDto2.getState() == 0) {
                        audit++;
                    } else if (promotionGoodsDto2.getState() == 1) {
                        adopt++;
                    } else if (promotionGoodsDto2.getState() == 2) {
                        unAdopt++;
                    }
                }
                if (!CollectionUtils.isEmpty(promotionGoodsEntities)) {
                    if (adopt == promotionGoodsEntities.size()) {
                        // 当通过的次数等于商品数量，那么该item商品通过审核
                        promotionGoods.setState(1);
                    } else if (unAdopt == promotionGoodsEntities.size()) {
                        // 当不通过的次数等于商品数量，那么该item商品未通过审核
                        promotionGoods.setState(2);
                    } else if (audit == promotionGoodsEntities.size()) {
                        // 当待审核次数等于商品数量，那么该item商品待审核
                        promotionGoods.setState(0);
                    } else {
                        // 部分通过
                        promotionGoods.setState(3);
                    }
                }
                promotionGoods.setRepertoryNum(repertoryNum);
            }
            promotionGoodsDtoList.get(0).setPromotionTypeId(promotionEntity.getTypeId());
        }
        return PageDataUtil.copyPageInfo(page.setRecords(promotionGoodsDtoList));
    }


    /**
     * Description 审核sku商品
     * <p>
     * Author Joe
     *
     * @param promotionGoodsId
     * @param state
     */
    @Override
    @Transactional
    public void modifyAuditSingleSkuGoods(Long promotionGoodsId, Integer state) {
        PromotionGoodsEntity promotionGoods = promotionGoodsDao.selectById(promotionGoodsId);
        promotionGoods.setState(state);
        promotionGoodsDao.updateById(promotionGoods);
    }

    /**
     * Description sku商品审核完成
     * <p>
     * Author Joe
     *
     * @param promotionGoodsList
     */
    @Override
    @Transactional
    public void modifyAuditSkuGoods(List<PromotionGoodsDto> promotionGoodsList) {
        for (PromotionGoodsDto promotionGoodsDto : promotionGoodsList) {
            if (null == promotionGoodsDto.getId()) {
                throw new GlobalException(PromotionExceptionEnum.PROMOTIONGOODS_NOT_NULL);
            }
            if (null == promotionGoodsDto.getState()) {
                throw new GlobalException(PromotionExceptionEnum.PRODUCT_AUDIT_STATE_CANNOT_BE_EMPTY);
            }
            modifyAuditSingleSkuGoods(promotionGoodsDto.getId(), promotionGoodsDto.getState());
        }
    }

    /**
     * Description 审核item商品通过/不通过
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     */
    @Override
    @Transactional
    public void modifyAuditItemGoods(PromotionGoodsDto promotionGoodsDto) {
        PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
        promotionGoodsEntity.clearInit();
        // 查询条件
        promotionGoodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
        promotionGoodsEntity.setPromotionId(promotionGoodsDto.getPromotionId());
        promotionGoodsEntity.setStoreId(promotionGoodsDto.getStoreId());
        promotionGoodsEntity.setItemId(promotionGoodsDto.getItemId());
        List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(new EntityWrapper<>(promotionGoodsEntity));
        for (PromotionGoodsEntity promotionGoods : promotionGoodsEntities) {
            modifyAuditSingleSkuGoods(promotionGoods.getId(), promotionGoodsDto.getState());
        }
    }


    /**
     * Description 商家商品审核完成
     * <p>
     * Author Joe
     *
     * @param promotionGoodsDto
     */
    @Override
    @Transactional
    public void modifyAuditGoods(PromotionGoodsDto promotionGoodsDto) {
        /** 通过*/
        Integer adopt = 0;
        /** 不通过*/
        Integer unAdopt = 0;
        /** 审核*/
        Integer audit = 0;
        /** 查询该店铺报名该活动的所有商品*/
        PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
        promotionGoodsEntity.clearInit();
        // 查询条件
        promotionGoodsEntity.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_NO);
        promotionGoodsEntity.setPromotionId(promotionGoodsDto.getPromotionId());
        promotionGoodsEntity.setStoreId(promotionGoodsDto.getStoreId());
        List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(new EntityWrapper<>(promotionGoodsEntity));
        /** 将未审核的商品状态改为未通过*/
        for (PromotionGoodsEntity promotionGoods : promotionGoodsEntities) {
            if (promotionGoods.getState() != 1 && promotionGoods.getState() != 2) {
                modifyAuditSingleSkuGoods(promotionGoods.getId(), PromotionConstants.AuditState.FAILURE_AUDIT);
            }
        }
        /** 查询该店铺报名该活动已经审核完成的所有商品*/
        List<PromotionGoodsEntity> promotionGoodsList = promotionGoodsDao.selectList(new EntityWrapper<>(promotionGoodsEntity));
        /** 计算已通过和未通过与待审核的商品数量*/
        for (PromotionGoodsEntity promotionGoods2 : promotionGoodsList) {
            switch (promotionGoods2.getState()) {
                case 1:
                    adopt++;
                    break;
                case 2:
                    unAdopt++;
                    break;
                default:
                    audit++;
                    break;
            }
        }
        // 商家报名数据
        EntityWrapper<PromotionEntryEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        entityWrapper.eq("promotionId", promotionGoodsDto.getPromotionId());
        entityWrapper.eq("storeId", promotionGoodsDto.getStoreId());
        List<PromotionEntryEntity> promotionEntryEntities = promotionEntryDao.selectList(entityWrapper);
        if (!CollectionUtils.isEmpty(promotionEntryEntities)) {
            PromotionEntryEntity promotionEntryEntity = promotionEntryEntities.get(0);
            if (audit != 0) {/** 未审核商品数量不为零,该店铺待审核状态*/
                promotionEntryEntity.setState(0);
            } else if (adopt != 0) {/** 已通过商品数量不为零,该店铺已通过状态*/
                promotionEntryEntity.setState(1);
            } else if (unAdopt != 0 && unAdopt == promotionGoodsList.size()) {/** 未通过商品数量不为零且全部商品未通过,该店铺未通过状态*/
                promotionEntryEntity.setState(2);
            }
            promotionEntryDao.updateById(promotionEntryEntity);
        }

    }

    /**
     * 查询所选item商品下所选sku商品
     *
     * @param promotionId
     * @param itemId
     * @return
     */
    @Override
    public List<PromotionGoodsDto> getPromotionSkuGoodsList(Long promotionId, Long itemId) {
        PromotionGoodsEntity promotionGoodsEntity = new PromotionGoodsEntity();
        promotionGoodsEntity.clearInit();
        // 查询条件
        promotionGoodsEntity.setPromotionId(promotionId);
        promotionGoodsEntity.setItemId(itemId);
        promotionGoodsEntity.setDeleteFlag(null);
        List<PromotionGoodsEntity> promotionGoodsEntities = promotionGoodsDao.selectList(new EntityWrapper<>(promotionGoodsEntity));
        // 返参DTO集合
        List<PromotionGoodsDto> promotionGoodsDtos = new ArrayList<>();
        // 循环添加其他需要的参数
        for (PromotionGoodsEntity promotionGoods : promotionGoodsEntities) {
            PromotionGoodsDto promotionGoodsDto = new PromotionGoodsDto();
            BeanCopyUtil.copy(promotionGoods, promotionGoodsDto);
            GoodsSkuDTO goodsSku = goodsSkuApi.getGoodsSku(promotionGoods.getGoodsSkuId());
            if (goodsSku != null) {
                if (!StringUtils.isBlank(goodsSku.getSaleFieldValue())) {
                    String saleFieldValue = backendCategoryApi.jointSaleFieldValue(goodsSku.getSaleFieldValue());
                    if (!StringUtils.isBlank(saleFieldValue)) {
                        promotionGoodsDto.setSaleFieldValue(saleFieldValue);
                    }
                }
                promotionGoodsDto.setGoodsPrice(goodsSku.getPrice());
                promotionGoodsDto.setRepertoryNum(goodsSku.getStockNumber().intValue());
            }
            promotionGoodsDtos.add(promotionGoodsDto);
        }
        return promotionGoodsDtos;
    }

    @Override
    public void cleanPromotionGoods(Long promotionId, Long itemId) {
        //获取正在进行中的活动
        EntityWrapper<PromotionGoodsEntity> pCond = new EntityWrapper<>();
        pCond.eq("promotionId", promotionId);
        pCond.eq("itemId", itemId);
        pCond.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);

        PromotionGoodsEntity promotionGoods = new PromotionGoodsEntity();
        promotionGoods.clearInit();
        promotionGoods.setDeleteFlag(PromotionConstants.DeletedFlag.DELETED_YES);
        promotionGoods.setLastModifiedTime(new Date());
        promotionGoodsDao.update(promotionGoods, pCond);
    }

}
