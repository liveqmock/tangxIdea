package com.topaiebiz.dec.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.dec.constans.RedisKey;
import com.topaiebiz.dec.dao.ModuleGoodsDao;
import com.topaiebiz.dec.dto.GoodsInfoDto;
import com.topaiebiz.dec.dto.ModuleGoodsDto;
import com.topaiebiz.dec.dto.ModuleItemDto;
import com.topaiebiz.dec.entity.ModuleGoodsEntity;
import com.topaiebiz.dec.exception.DecExceptionEnum;
import com.topaiebiz.dec.service.AppHomePageService;
import com.topaiebiz.dec.service.MQProducerService;
import com.topaiebiz.dec.service.ModuleGoodsService;
import com.topaiebiz.dec.service.TemplateModuleService;
import com.topaiebiz.goods.api.GoodsApi;
import com.topaiebiz.goods.dto.sku.GoodsDTO;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.*;


/**
 * <p>
 * 模块商品详情表 服务实现类
 * </p>
 *
 * @author hzj
 * @since 2018-01-08
 */
@Service
public class ModuleGoodsServiceImpl extends ServiceImpl<ModuleGoodsDao, ModuleGoodsEntity> implements ModuleGoodsService {

    @Autowired
    private ModuleGoodsDao moduleGoodsDao;

    @Autowired
    private MQProducerService mqProducerService;

    @Autowired
    private GoodsApi goodsApi;

    @Autowired
    private TemplateModuleService templateModuleService;

    @Autowired
    private AppHomePageService appHomePageService;

    @Override
    public List<ModuleItemDto> getModuleItemDtos(List<Long> moduleIds) {
        if (CollectionUtils.isEmpty(moduleIds)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        EntityWrapper<ModuleGoodsEntity> condition = new EntityWrapper();
        condition.in("moduleId", moduleIds);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        condition.orderBy("sortNo", true);
        List<ModuleGoodsEntity> moduleGoodsEntityList = moduleGoodsDao.selectList(condition);

        Map<Long, List<ModuleGoodsEntity>> moduleGoodsMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(moduleGoodsEntityList)) {
            for (Long moduleId : moduleIds) {
                List<ModuleGoodsEntity> moduleGoodsList = new ArrayList<>();
                for (ModuleGoodsEntity moduleGoodsEntity : moduleGoodsEntityList) {
                    if (moduleGoodsEntity.getModuleId().equals(moduleId)) {
                        moduleGoodsList.add(moduleGoodsEntity);
                        moduleGoodsMap.put(moduleId, moduleGoodsList);
                    }
                }
            }
        }
        List<ModuleItemDto> moduleItemDtos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(moduleGoodsMap)) {
            for (Long moduleId : moduleIds) {
                ModuleItemDto moduleItemDto = new ModuleItemDto();
                List<GoodsInfoDto> goodsInfoDtoList = new ArrayList<>();
                for (ModuleGoodsEntity moduleGoodsEntity : moduleGoodsMap.get(moduleId)) {
                    GoodsInfoDto goodsInfoDto = new GoodsInfoDto();
                    goodsInfoDto.setSortNo(moduleGoodsEntity.getSortNo());
                    goodsInfoDto.setGoodsId(moduleGoodsEntity.getGoodsId());
                    goodsInfoDto.setId(moduleGoodsEntity.getId());
                    goodsInfoDtoList.add(goodsInfoDto);
                }

                List<GoodsDTO> moduleGoods = new ArrayList<>();
                if (!CollectionUtils.isEmpty(goodsInfoDtoList)) {
                    for (GoodsInfoDto goodsInfoDto : goodsInfoDtoList) {
                        GoodsDTO goodsDTO = new GoodsDTO();
                        BeanUtils.copyProperties(goodsInfoDto, goodsDTO);
                        moduleGoods.add(goodsDTO);
                    }
                }
                List<GoodsDTO> goodsItem = goodsApi.getGoodsSort(moduleGoods);
                moduleItemDto.setModuleId(moduleId);
                moduleItemDto.setItems(goodsItem);
                moduleItemDtos.add(moduleItemDto);
            }
        }
        return moduleItemDtos;
    }

    @Override
    @JedisOperation
    public void saveModuleGoods(ModuleGoodsDto moduleGoodsDto) {
        if (StringUtils.isEmpty(moduleGoodsDto)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        for (GoodsInfoDto goodsInfoDto : moduleGoodsDto.getGoodsInfoDetail()) {
            ModuleGoodsEntity moduleGoodsEntity = new ModuleGoodsEntity();
            moduleGoodsEntity.setModuleId(moduleGoodsDto.getModuleId());
            moduleGoodsEntity.setGoodsId(goodsInfoDto.getGoodsId());
            moduleGoodsEntity.setSortNo(goodsInfoDto.getSortNo());
            moduleGoodsEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
            moduleGoodsDao.insert(moduleGoodsEntity);
        }
        // mqProducerService.produceMQByModuleId(moduleGoodsDto.getModuleId());
        //更新缓存
        templateModuleService.refreshCacheByModuleId(moduleGoodsDto.getModuleId());
    }


    @Override
    @JedisOperation
    public void modifyModuleGoods(ModuleGoodsDto moduleGoodsDto) {
        if (StringUtils.isEmpty(moduleGoodsDto.getModuleId()) || CollectionUtils.isEmpty(moduleGoodsDto.getGoodsInfoDetail())) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        EntityWrapper<ModuleGoodsEntity> condition = new EntityWrapper();
        condition.eq("moduleId", moduleGoodsDto.getModuleId());
        ModuleGoodsEntity moduleGoodsEntity = new ModuleGoodsEntity();
        moduleGoodsEntity.cleanInit();
        moduleGoodsEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        moduleGoodsEntity.setLastModifiedTime(new Date());
        moduleGoodsEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        moduleGoodsDao.update(moduleGoodsEntity, condition);
        saveModuleGoods(moduleGoodsDto);

        //   mqProducerService.produceMQByModuleId(moduleGoodsDto.getModuleId());
    }

    @Override
    @JedisOperation
    public void deleteModuleGoods(Long id) {
        if (StringUtils.isEmpty(id)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        ModuleGoodsEntity moduleGoodsEntity = new ModuleGoodsEntity();
        moduleGoodsEntity.cleanInit();
        moduleGoodsEntity.setId(id);
        moduleGoodsEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        moduleGoodsEntity.setLastModifiedTime(new Date());
        moduleGoodsEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        moduleGoodsDao.updateById(moduleGoodsEntity);

        //更新缓存
        ModuleGoodsEntity entity = moduleGoodsDao.selectById(id);
        //mqProducerService.produceMQByModuleId(entity.getModuleId());
        templateModuleService.refreshCacheByModuleId(entity.getModuleId());
    }

    /**
     * 获取某个模块下的商品
     *
     * @param moduleId
     * @return
     */
    @Override
    @JedisOperation
    public ModuleItemDto getModuleItemDto(Long moduleId) {
        if (null == moduleId) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        Jedis jedis = JedisContext.getJedis();
        ModuleItemDto moduleItemDto = new ModuleItemDto();
        String key = RedisKey.DECORATE_MODULE_GOODS_PREFIX + moduleId;
        if (jedis.exists(key)) {
            moduleItemDto = JSON.parseObject(jedis.get(key), ModuleItemDto.class);
        } else {
            moduleItemDto.setModuleId(moduleId);
            //通过模板ID来获取该模板下的所有模块商品
            EntityWrapper<ModuleGoodsEntity> condition = new EntityWrapper<>();
            condition.eq("moduleId", moduleId);
            condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
            condition.orderBy("sortNo", true);
            List<ModuleGoodsEntity> entities = moduleGoodsDao.selectList(condition);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(entities)) {
                //SDK获取所有的商品详情
                List<GoodsDTO> params = new ArrayList<>();//参数
                for (ModuleGoodsEntity entity : entities) {
                    GoodsDTO goodsDTO = new GoodsDTO();
                    goodsDTO.setId(entity.getId());
                    goodsDTO.setGoodsId(entity.getGoodsId());
                    goodsDTO.setSortNo(entity.getSortNo());
                    params.add(goodsDTO);
                }
                List<GoodsDTO> items = goodsApi.getGoodsSort(params);
                moduleItemDto.setItems(items);
            }
            jedis.set(key, JSON.toJSONString(moduleItemDto));
        }
        return moduleItemDto;
    }


}
