package com.topaiebiz.system.dict.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.PageDataUtil;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.system.common.PageUtil;
import com.topaiebiz.system.dict.dao.DataDictDao;
import com.topaiebiz.system.dict.dto.DataDictDto;
import com.topaiebiz.system.dict.dto.DataDictTypeDto;
import com.topaiebiz.system.dict.entity.DataDictEntity;
import com.topaiebiz.system.dict.entity.DataDictTypeEntity;
import com.topaiebiz.system.dict.exception.DataDictTypeExceptionEnum;
import com.topaiebiz.system.dict.service.DataDictService;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Description: 业务逻辑层实现类
 * 
 * Author : Anthony
 * 
 * Date :2017年9月25日 下午8:41:04
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
public class DataDictServiceImpl implements DataDictService {

	@Autowired
	private DataDictDao dataDictDao;

	@Override
	public List<DataDictTypeDto> getDataDictTypeById() {
		List<DataDictTypeEntity> dataDictTypeEntities = dataDictDao.selectDataDictTypeById();
		List<DataDictTypeDto> dataDictTypeDtos = PageUtil.copyList(dataDictTypeEntities,DataDictTypeDto.class);
		return dataDictTypeDtos;
	}

	@Override
	public Integer saveDataDict(DataDictDto dto) throws GlobalException {
		// 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象。
		DataDictEntity entity = new DataDictEntity();
		BeanUtils.copyProperties(dto, entity);
		/** 数据字典的编码进行重复验证 */
		DataDictEntity findDataDictByDictCode = dataDictDao.selectDataDictByDictCodes(entity);
		if (findDataDictByDictCode != null) {
			throw new GlobalException(DataDictTypeExceptionEnum.DICTIONARY_DICTCODE_NOT_REPETITION);
		}
		/** 对数据字典的值重复验证 */
		DataDictEntity findDataDictByDictValue = dataDictDao.selectDataDictByDictValues(entity);
		if (findDataDictByDictValue != null) {
			throw new GlobalException(DataDictTypeExceptionEnum.DICTIONARY_DICTVALUE_NOT_REPETITION);
		}
		entity.setCreatedTime(new Date());
		entity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
		return dataDictDao.insert(entity);
	}

	@Override
	public DataDictDto getDataDictById(Long id) {
		DataDictEntity dataDictEntity = dataDictDao.selectDataDictById(id);
		DataDictDto dataDictDto = new DataDictDto();
		BeanUtils.copyProperties(dataDictEntity,dataDictDto);
		return dataDictDto;
	}

	@Override
	public Integer modifyDataDictById(DataDictDto dto) throws GlobalException {
		/** 数据字典的编码进行重复验证 */
		DataDictEntity findDataDictByDictCode = dataDictDao.selectDataDictByDictCode(dto);
		if (findDataDictByDictCode != null) {
			throw new GlobalException(DataDictTypeExceptionEnum.DICTIONARY_DICTCODE_NOT_REPETITION);
		}
		/** 对数据字典的值重复验证 */
		DataDictEntity findDataDictByDictValue = dataDictDao.selectDataDictByDictValue(dto);
		if (findDataDictByDictValue != null) {
			throw new GlobalException(DataDictTypeExceptionEnum.DICTIONARY_DICTVALUE_NOT_REPETITION);
		}
		DataDictEntity dataDict = dataDictDao.selectById(dto.getId());
		BeanUtils.copyProperties(dto, dataDict);
		dataDict.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
		dataDict.setLastModifiedTime(new Date());
		return dataDictDao.updateById(dataDict);
	}

	@Override
	public PageInfo<DataDictDto> getDataDictList(PagePO pagePO, DataDictDto dataDictDto) throws GlobalException {
		Page<DataDictDto> page = PageDataUtil.buildPageParam(pagePO);
		page.setRecords(dataDictDao.selectDataDictList(page, dataDictDto));
		return PageDataUtil.copyPageInfo(page);
	}

	@Override
	public Integer removeDataDictByIds(Long[] id) throws GlobalException {
		for (Long long1 : id) {
			/** 对id进行查询 */
			DataDictEntity selectDataDictById = dataDictDao.selectDataDictById(long1);
			if (selectDataDictById == null) {
				throw new GlobalException(DataDictTypeExceptionEnum.DICTIONARY_ID_NOT_NULL);
			}
		}
		return dataDictDao.deleteDataDictByIds(id);
	}

	@Override
	public Integer removeDataDictById(Long id) {
		return dataDictDao.deleteDataDictById(id);
	}

}
