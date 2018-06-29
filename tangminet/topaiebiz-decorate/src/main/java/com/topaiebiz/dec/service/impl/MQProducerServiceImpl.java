package com.topaiebiz.dec.service.impl;

import com.nebulapaas.common.msg.core.MessageSender;
import com.nebulapaas.common.msg.dto.MessageDTO;
import com.nebulapaas.common.msg.dto.MessageTypeEnum;
import com.topaiebiz.dec.dao.TemplateModuleDao;
import com.topaiebiz.dec.entity.TemplateModuleEntity;
import com.topaiebiz.dec.service.MQProducerService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class MQProducerServiceImpl implements MQProducerService {


    @Autowired
    private MessageSender sender;

    @Autowired
    private TemplateModuleDao templateModuleDao;

    @Override
    public void produceMQByModuleId(Long moduleId) {
        TemplateModuleEntity entity = templateModuleDao.selectById(moduleId);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMemberId(SecurityContextUtils.getCurrentUserDto().getId());
        messageDTO.setType(MessageTypeEnum.MODIFY_MODULE);
        messageDTO.getParams().put("templateId", entity.getInfoId());
        sender.publicMessage(messageDTO);
    }

    @Override
    public void produceMQByModuleIds(List<Long> moduleIds) {
        if (CollectionUtils.isNotEmpty(moduleIds)) {
            List<TemplateModuleEntity> moduleEntities = templateModuleDao.selectBatchIds(moduleIds);
            List<Long> templateIds = new ArrayList<>();
            for (TemplateModuleEntity entity : moduleEntities) {
                templateIds.add(entity.getInfoId());
            }
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setType(MessageTypeEnum.MODIFY_MODULE);
            messageDTO.setMemberId(SecurityContextUtils.getCurrentUserDto().getId());
            messageDTO.getParams().put("templateId", templateIds.get(0));
            sender.publicMessage(messageDTO);
        }
    }

    /**
     * 修改标题更新REDIS缓存数据
     *
     * @param titleId
     */
    @Override
    public void produceMQByTitileId(Long titleId) {
        MessageDTO msg = new MessageDTO();
        msg.setType(MessageTypeEnum.MODIFY_TITLE_ITEM);
        msg.getParams().put("titleId", titleId);
        sender.publicMessage(msg);
    }
}
