package com.topaiebiz.dec.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.nebulapaas.common.redis.lock.DistLockSservice;
import com.nebulapaas.common.redis.vo.LockResult;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.dec.constans.RedisKey;
import com.topaiebiz.dec.dao.TemplateTitleDao;
import com.topaiebiz.dec.dao.TitleGoodsDao;
import com.topaiebiz.dec.dto.GoodsInfoDto;
import com.topaiebiz.dec.dto.TitleGoodsDto;
import com.topaiebiz.dec.dto.TitleItemDto;
import com.topaiebiz.dec.entity.TemplateTitleEntity;
import com.topaiebiz.dec.entity.TitleGoodsEntity;
import com.topaiebiz.dec.exception.DecExceptionEnum;
import com.topaiebiz.dec.service.MQProducerService;
import com.topaiebiz.dec.service.TitleGoodsService;
import com.topaiebiz.decorate.constant.Constant;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.dto.sku.GoodsDTO;
import com.topaiebiz.system.util.SecurityContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.*;


/**
 * <p>
 * 标题商品详情表 服务实现类
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@Service
@Slf4j
public class TitleGoodsServiceImpl extends ServiceImpl<TitleGoodsDao, TitleGoodsEntity> implements TitleGoodsService {

    @Autowired
    private TitleGoodsDao titleGoodsDao;

    @Autowired
    private TemplateTitleDao templateTitleDao;

    @Autowired
    private GoodsApi goodsApi;

    @Autowired
    private RedisCache redisCache;

    @Override
    @Transactional
    @JedisOperation
    public void saveTitleGoodsDto(TitleGoodsDto titleGoodsDto) {
        if (StringUtils.isEmpty(titleGoodsDto) || CollectionUtils.isEmpty(titleGoodsDto.getGoodsInfoDetail())) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        Long titleId = titleGoodsDto.getTitleId();
        String memo = titleGoodsDto.getMemo();
        TemplateTitleEntity titleEntity = templateTitleDao.selectById(titleId);
        Long parentTitleId = titleEntity.getParentId();
        for (GoodsInfoDto goodsInfoDto : titleGoodsDto.getGoodsInfoDetail()) {
            TitleGoodsEntity titleGoodsEntity = new TitleGoodsEntity();
            titleGoodsEntity.setTitleId(titleId);
            titleGoodsEntity.setGoodsId(goodsInfoDto.getGoodsId());
            titleGoodsEntity.setSortNo(goodsInfoDto.getSortNo());
            titleGoodsEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
            titleGoodsEntity.setParentId(parentTitleId);
            titleGoodsEntity.setMemo(memo);
            titleGoodsDao.insert(titleGoodsEntity);
        }
        //更新REDIS缓存数据
        // mqProducerService.produceMQByTitileId(titleGoodsDto.getTitleId());
        refreshCache(titleGoodsDto.getTitleId());
    }

    @Override
    @Transactional
    @JedisOperation
    public void deleteTitleGoodsDto(Long id) {
        if (StringUtils.isEmpty(id)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        TitleGoodsEntity titleGoodsEntity = new TitleGoodsEntity();
        titleGoodsEntity.cleanInit();
        titleGoodsEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        titleGoodsEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        titleGoodsEntity.setLastModifiedTime(new Date());
        titleGoodsEntity.setId(id);
        titleGoodsDao.updateById(titleGoodsEntity);
        //更新缓存
        TitleGoodsEntity entity = titleGoodsDao.selectById(id);
        //mqProducerService.produceMQByTitileId(entity.getTitleId());
        refreshCache(entity.getTitleId());
    }

    @Override
    public TitleGoodsDto getTitleGoodsDtos(Long titleId) {
        TitleGoodsDto titleGoodsDto = new TitleGoodsDto();
        List<GoodsInfoDto> goodsInfoDtoList = new ArrayList<>();
        titleGoodsDto.setTitleId(titleId);
        EntityWrapper<TitleGoodsEntity> condition = new EntityWrapper();
        condition.eq("titleId", titleId);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.orderBy("sortNo", true);
        for (TitleGoodsEntity titleGoodsEntity : titleGoodsDao.selectList(condition)) {
            GoodsInfoDto goodsInfoDto = new GoodsInfoDto();
            BeanUtils.copyProperties(titleGoodsEntity, goodsInfoDto);
            goodsInfoDtoList.add(goodsInfoDto);
        }
        titleGoodsDto.setGoodsInfoDetail(goodsInfoDtoList);
        return titleGoodsDto;
    }

    @Override
    @JedisOperation
    public TitleItemDto getTitleItemDto(Long titleId) {
        if (null == titleId) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        Jedis jedis = JedisContext.getJedis();
        String key = RedisKey.DECORATE_TITLE_GOODS_PREFIX + titleId;
        TitleItemDto titleItemDto = new TitleItemDto();
        if (jedis.exists(key)) {
            titleItemDto = JSON.parseObject(jedis.get(key), TitleItemDto.class);
        } else {
            titleItemDto = makeUpTitleItemCache(titleId);
        }
        return titleItemDto;
    }

    @Override
    public PageInfo<TitleGoodsEntity> getTitlePageList(PagePO pagePO, Long titleId) {
        Page<TitleGoodsEntity> page = PageDataUtil.buildPageParam(pagePO);
        EntityWrapper<TitleGoodsEntity> condition = new EntityWrapper();
        condition.eq("titleId", titleId);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.orderBy("sortNo", true);
        List<TitleGoodsEntity> titleGoodsEntityList = titleGoodsDao.selectPage(page, condition);
        page.setRecords(titleGoodsEntityList);
        return PageDataUtil.copyPageInfo(page);
    }

    @Override
    @Transactional
    public void modifyTitleGoods(TitleGoodsDto titleGoodsDto) {
        if (StringUtils.isEmpty(titleGoodsDto.getTitleId())) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        EntityWrapper<TitleGoodsEntity> condition = new EntityWrapper();
        condition.eq("titleId", titleGoodsDto.getTitleId());
        TitleGoodsEntity titleGoodsEntity = new TitleGoodsEntity();
        titleGoodsEntity.cleanInit();
        titleGoodsEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        titleGoodsEntity.setLastModifiedTime(new Date());
        titleGoodsEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        titleGoodsDao.update(titleGoodsEntity, condition);
        saveTitleGoodsDto(titleGoodsDto);
    }

    @Override
    @JedisOperation
    @Transactional
    public void refreshCache(Long titleId) {
        if (null == titleId) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        makeUpTitleItemCache(titleId);
    }

    @Transactional
    public TitleItemDto makeUpTitleItemCache(Long titleId) {
        TitleItemDto titleItemDto = new TitleItemDto();
        List<GoodsInfoDto> goodsInfoDtoList = new ArrayList<>();
        titleItemDto.setTitleId(titleId);
        //获取标题名称
        TemplateTitleEntity titleEntity = templateTitleDao.selectById(titleId);
        titleItemDto.setTitleName(titleEntity.getTitleName());
        //获取标题商品
        EntityWrapper<TitleGoodsEntity> condition = new EntityWrapper();
        condition.eq("titleId", titleId);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.orderBy("sortNo", true);
        List<TitleGoodsEntity> goodsEntities = titleGoodsDao.selectList(condition);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>商品数据:" + JSON.toJSONString(goodsEntities));
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>商品数据:" + JSON.toJSONString(goodsEntities));
        for (TitleGoodsEntity titleGoodsEntity : goodsEntities) {
            GoodsInfoDto goodsInfoDto = new GoodsInfoDto();
            BeanUtils.copyProperties(titleGoodsEntity, goodsInfoDto);
            goodsInfoDtoList.add(goodsInfoDto);
        }
        List<GoodsDTO> titleGoods = new ArrayList<>();
        List<GoodsDTO> titleItem = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsInfoDtoList)) {
            for (GoodsInfoDto goodsInfoDto : goodsInfoDtoList) {
                GoodsDTO goodsDTO = new GoodsDTO();
                BeanUtils.copyProperties(goodsInfoDto, goodsDTO);
                titleGoods.add(goodsDTO);
            }
            titleItem = goodsApi.getGoodsSort(titleGoods);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + JSON.toJSONString(titleItem));
        }
        titleItemDto.setItems(titleItem);
        // Jedis jedis = JedisContext.getJedis();
        //  String result = jedis.set(RedisKey.DECORATE_TITLE_GOODS_PREFIX + titleId, JSON.toJSONString(titleItemDto));
        redisCache.set(RedisKey.DECORATE_TITLE_GOODS_PREFIX + titleId, JSON.toJSONString(titleItemDto));
        return titleItemDto;
    }
}
