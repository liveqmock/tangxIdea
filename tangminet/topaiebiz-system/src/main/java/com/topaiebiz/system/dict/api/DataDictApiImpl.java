package com.topaiebiz.system.dict.api;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.topaiebiz.system.api.DataDictApi;
import com.topaiebiz.system.dict.dao.DataDictDao;
import com.topaiebiz.system.dict.entity.DataDictEntity;
import com.topaiebiz.system.dto.DataDictDto;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description TODO
 * <p>
 * Author hxpeng
 * <p>
 * Date 2018/1/10 17:57
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
public class DataDictApiImpl implements DataDictApi {

    @Autowired
    private DataDictDao dataDictDao;


    @Override
    public DataDictDto getById(Long id) {
        if (null != id){
            DataDictEntity dataDictEntity = dataDictDao.selectById(id);
            if (null != dataDictEntity){
                DataDictDto dataDictDto = new DataDictDto();
                BeanUtils.copyProperties(dataDictEntity, dataDictDto);
                return dataDictDto;
            }
        }
        return null;
    }

    @Override
    public List<DataDictDto> getByCode(String code) {
        EntityWrapper<DataDictEntity> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("code",code);
        entityWrapper.eq("deletedFlag", 0);

        List<DataDictEntity> dataDictEntities = dataDictDao.selectList(entityWrapper);
        if (!CollectionUtils.isEmpty(dataDictEntities)){
            List<DataDictDto> dataDictDtos = new ArrayList<>(dataDictEntities.size());
            for (DataDictEntity dataDictEntity : dataDictEntities){
                DataDictDto dataDictDto = new DataDictDto();
                BeanUtils.copyProperties(dataDictEntity, dataDictDto);
                dataDictDto.setId(dataDictEntity.getId());
                dataDictDtos.add(dataDictDto);
            }
            return dataDictDtos;
        }
        return null;
    }

    @Override
    public Long insert(DataDictDto dataDictDto) {
        if (null == dataDictDto || StringUtils.isBlank(dataDictDto.getCode()) || StringUtils.isBlank(dataDictDto.getValue())){
            return null;
        }
        DataDictEntity dataDictEntity = new DataDictEntity();
        BeanUtils.copyProperties(dataDictDto, dataDictEntity);
        dataDictEntity.setCreatedTime(new Date());
        dataDictEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
        dataDictDao.insert(dataDictEntity);
        return dataDictEntity.getId();
    }

    @Override
    public boolean deleteById(Long id) {
        int result = 0;
        if (null != id){
            DataDictEntity dataDictEntity = dataDictDao.selectById(id);
            if (null != dataDictEntity){
                dataDictEntity.setDeleteFlag((byte)0);
                result = dataDictDao.updateById(dataDictEntity);
            }
        }
        return result > 0;
    }


}
