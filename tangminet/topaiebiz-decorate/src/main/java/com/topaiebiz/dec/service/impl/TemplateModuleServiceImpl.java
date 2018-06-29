package com.topaiebiz.dec.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nebulapaas.base.contants.Constants;
import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.nebulapaas.common.redis.aop.JedisContext;
import com.nebulapaas.common.redis.aop.JedisOperation;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.dec.constans.RedisKey;
import com.topaiebiz.dec.dao.ModuleGoodsDao;
import com.topaiebiz.dec.dao.TemplateModuleDao;
import com.topaiebiz.dec.dto.TemplateModuleDto;
import com.topaiebiz.dec.entity.ModuleGoodsEntity;
import com.topaiebiz.dec.entity.TemplateModuleEntity;
import com.topaiebiz.dec.exception.DecExceptionEnum;
import com.topaiebiz.dec.service.AppHomePageService;
import com.topaiebiz.dec.service.MQProducerService;
import com.topaiebiz.dec.service.TemplateModuleService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * <p>
 * 装修模板模块表 服务实现类
 * </p>
 *
 * @author 王钟剑
 * @since 2018-01-08
 */
@Service
public class TemplateModuleServiceImpl extends ServiceImpl<TemplateModuleDao, TemplateModuleEntity> implements TemplateModuleService {
    @Autowired
    private TemplateModuleDao templateModuleDao;

    @Autowired
    private ModuleGoodsDao moduleGoodsDao;

    @Autowired
    private MQProducerService mqProducerService;

    @Autowired
    private MessageSender sender;

    @Autowired
    private AppHomePageService appHomePageService;

    @Override
    public void saveTemplateModuleDto(List<TemplateModuleDto> templateModuleDtos) {
        if (CollectionUtils.isEmpty(templateModuleDtos)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        List<TemplateModuleEntity> templateModuleEntityList = new ArrayList<>();
        List<Long> templateIds = new ArrayList<>();
        for (TemplateModuleDto templateModuleDto : templateModuleDtos) {
            TemplateModuleEntity templateModuleEntity = new TemplateModuleEntity();
            BeanUtils.copyProperties(templateModuleDto, templateModuleEntity);
            templateIds.add(templateModuleDto.getInfoId());
            templateModuleEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
            templateModuleEntityList.add(templateModuleEntity);
        }
        templateModuleDao.insertBatch(templateModuleEntityList);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.getParams().put("templateId", templateIds.get(0));
        messageDTO.setMemberId(SecurityContextUtils.getCurrentUserDto().getId());
        messageDTO.setType(MessageTypeEnum.MODIFY_MODULE);
        sender.publicMessage(messageDTO);
    }


    @Override
    public List<TemplateModuleDto> getTemplateModuleDto(Long infoId) {
        if (StringUtils.isEmpty(infoId)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }

        List<TemplateModuleDto> templateModuleDtoList = new ArrayList<>();
        EntityWrapper<TemplateModuleEntity> condition = new EntityWrapper();
        condition.eq("infoId", infoId);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<TemplateModuleEntity> result = templateModuleDao.selectList(condition);
        if (CollectionUtils.isEmpty(result)) {
            return Collections.emptyList();
        }
        for (TemplateModuleEntity templateModuleEntity : result) {
            TemplateModuleDto templateModuleDto = new TemplateModuleDto();
            BeanUtils.copyProperties(templateModuleEntity, templateModuleDto);
            templateModuleDtoList.add(templateModuleDto);
        }
        return templateModuleDtoList;
    }

    @Override
    @Transactional
    @JedisOperation
    public Integer deleteById(Long id) {
        if (StringUtils.isEmpty(id)) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        Jedis jedis = JedisContext.getJedis();
        TemplateModuleEntity templateModuleEntity = new TemplateModuleEntity();
        templateModuleEntity.cleanInit();
        templateModuleEntity.setId(id);
        templateModuleEntity.setDeleteFlag(Constants.DeletedFlag.DELETED_YES);
        templateModuleEntity.setLastModifiedTime(new Date());
        templateModuleEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        mqProducerService.produceMQByModuleId(id);
        //删除模块直接删除模块下的商品
        //查看该模块下是否存在商品
        EntityWrapper<ModuleGoodsEntity> condition = new EntityWrapper<>();
        condition.eq("moduleId", id);
        condition.eq("deletedFlag", Constants.DeletedFlag.DELETED_NO);
        List<ModuleGoodsEntity> moduleGoodsEntities = moduleGoodsDao.selectList(condition);
        if (!CollectionUtils.isEmpty(moduleGoodsEntities)) {
            jedis.del(RedisKey.DECORATE_MODULE_GOODS_PREFIX + id);
        }
        return templateModuleDao.updateById(templateModuleEntity);
    }

    @Override
    public Integer updateTemplateModule(TemplateModuleDto templateModuleDto) {
        if (StringUtils.isEmpty(templateModuleDto.getId())) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        TemplateModuleEntity templateModuleEntity = new TemplateModuleEntity();
        templateModuleEntity.cleanInit();
        BeanUtils.copyProperties(templateModuleDto, templateModuleEntity);
        templateModuleEntity.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
        templateModuleEntity.setLastModifiedTime(new Date());
        mqProducerService.produceMQByModuleId(templateModuleDto.getId());
        return templateModuleDao.updateById(templateModuleEntity);
    }

    @Override
    @JedisOperation
    public void refreshCacheByModuleId(Long moduleId) {
        if (null == moduleId) {
            throw new GlobalException(DecExceptionEnum.ID_NOT_NULL);
        }
        TemplateModuleEntity moduleEntity = templateModuleDao.selectById(moduleId);
        appHomePageService.refreshCache(moduleEntity.getInfoId());
    }
}
