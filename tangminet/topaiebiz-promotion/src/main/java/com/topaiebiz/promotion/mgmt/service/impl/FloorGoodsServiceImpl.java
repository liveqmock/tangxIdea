package com.topaiebiz.promotion.mgmt.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.nebulapaas.common.redis.cache.RedisCache;
import com.topaiebiz.basic.api.ConfigApi;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.dto.sku.ItemDTO;
import com.topaiebiz.promotion.common.util.DozerUtils;
import com.topaiebiz.promotion.constants.PromotionConstants;
import com.topaiebiz.promotion.mgmt.dao.FloorGoodsDao;
import com.topaiebiz.promotion.mgmt.dto.floor.FloorConfigDTO;
import com.topaiebiz.promotion.mgmt.dto.floor.FloorDTO;
import com.topaiebiz.promotion.mgmt.dto.floor.FloorGoodsDTO;
import com.topaiebiz.promotion.mgmt.entity.FloorGoodsEntity;
import com.topaiebiz.promotion.mgmt.service.FloorGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.topaiebiz.promotion.constants.PromotionConstants.CacheKey.FLOOR_GOODS_PREFIX;
import static com.topaiebiz.promotion.constants.PromotionConstants.CacheKey.FLOOR_PREFIX;

@Slf4j
@Service
public class FloorGoodsServiceImpl implements FloorGoodsService {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ConfigApi configApi;
    @Autowired
    private FloorGoodsDao floorGoodsDao;
    @Autowired
    private GoodsApi goodsApi;

    @Override
    public List<FloorDTO> selectListByConfigCode(String configCode) {
        //从缓存中取配置楼层信息
        List<FloorDTO> results = redisCache.getListValue(FLOOR_PREFIX + configCode, FloorDTO.class);
        if (CollectionUtils.isNotEmpty(results)) {
            return results;
        }
        //获取配置里面存储的楼层CODE信息
        String config = configApi.getConfig(configCode);
        List<FloorConfigDTO> floorConfigs = JSON.parseArray(config, FloorConfigDTO.class);
        //没有配置数据
        if (CollectionUtils.isEmpty(floorConfigs)) {
            log.warn("------------限量折扣没有配置信息，配置CODE-configCode:" + configCode);
            return null;
        }

        List<String> floorCodes = floorConfigs.stream().map(floorConfig -> floorConfig.getFloorCode()).collect(Collectors.toList());
        //根据楼层code，查询商品楼层列表
        EntityWrapper<FloorGoodsEntity> cond = new EntityWrapper<>();
        cond.in("floorCode", floorCodes);
        cond.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<FloorGoodsEntity> entities = floorGoodsDao.selectList(cond);

        if (CollectionUtils.isEmpty(entities)) {
            log.warn("------------限量折扣没有配置楼层数据，楼层CODE-floorCodes:" + floorCodes);
            return null;
        }
        //匹配商品信息
        List<FloorGoodsDTO> floorGoodsDTOS = convertToDTO(entities);
        //封装商品楼层信息
        List<FloorDTO> floorDTOS = new ArrayList<>();
        for (FloorConfigDTO floorConfig : floorConfigs) {
            FloorDTO floorDTO = new FloorDTO();
            floorDTO.setTypeName(floorConfig.getName());
            List<FloorGoodsDTO> codeEntities = floorGoodsDTOS.stream().filter(entity -> entity.getFloorCode().equals(floorConfig.getFloorCode())).collect(Collectors.toList());
            floorDTO.setGoodsList(codeEntities);
            floorDTOS.add(floorDTO);
        }
        //过期时间为5分钟
        redisCache.set(FLOOR_PREFIX + configCode, floorDTOS, 300);
        return floorDTOS;
    }

    @Override
    public List<FloorGoodsDTO> selectGoodsListByFloorCode(String floorCode) {
        //从缓存中取配置楼层信息
        List<FloorGoodsDTO> results = redisCache.getListValue(FLOOR_GOODS_PREFIX + floorCode, FloorGoodsDTO.class);
        if (CollectionUtils.isNotEmpty(results)) {
            return results;
        }
        //根据楼层code，查询商品楼层列表
        EntityWrapper<FloorGoodsEntity> cond = new EntityWrapper<>();
        cond.eq("floorCode", floorCode);
        cond.eq("deletedFlag", PromotionConstants.DeletedFlag.DELETED_NO);
        List<FloorGoodsEntity> entities = floorGoodsDao.selectList(cond);
        if (CollectionUtils.isEmpty(entities)) {
            log.warn("------------限时秒杀没有配置楼层数据，楼层CODE-floorCode:" + floorCode);
            return null;
        }
        //匹配商品信息
        List<FloorGoodsDTO> floorGoodsDTOS = convertToDTO(entities);
        //过期时间为5分钟
        redisCache.set(FLOOR_GOODS_PREFIX + floorCode, floorGoodsDTOS, 300);
        return floorGoodsDTOS;
    }

    /**
     * 转换成DTO，匹配商品信息
     *
     * @param entities
     * @return
     */
    private List<FloorGoodsDTO> convertToDTO(List<FloorGoodsEntity> entities) {
        //把楼层实体以流的形式输入，并和id组合成list集合的形式，代替原来for循环
        List<Long> goodsIds = entities.stream().map(floorGoodsEntity -> floorGoodsEntity.getGoodsId()).collect(Collectors.toList());
        List<FloorGoodsDTO> floorGoodsDTOS = DozerUtils.maps(entities, FloorGoodsDTO.class);
        //商品属性集合
        List<ItemDTO> itemDTOS = goodsApi.getItemMap(goodsIds);
        if (CollectionUtils.isEmpty(itemDTOS)) {
            log.warn("------------商品信息未找到，请核对数据！");
        } else {
            //拼接商品信息
            for (FloorGoodsDTO dto : floorGoodsDTOS) {
                List<ItemDTO> tempItemList = itemDTOS.stream().filter(tempItem -> tempItem.getId().equals(dto.getGoodsId())).distinct().collect(Collectors.toList());
                if (CollectionUtils.isEmpty(tempItemList)) {
                    log.warn("------------商品不存在，商品ID-itemId:" + dto.getGoodsId());
                    continue;
                }
                dto.setImage(tempItemList.get(0).getPictureName());//图片名
                dto.setMarketPrice(tempItemList.get(0).getMarketPrice());//商品原价
            }
        }
        return floorGoodsDTOS;
    }

}