package com.topaiebiz.sms.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.topaiebiz.sms.entity.MessageLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageLogDao extends BaseMapper<MessageLogEntity> {

}
