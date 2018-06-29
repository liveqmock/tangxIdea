package com.topaiebiz.merchant.grade.service.impl;

import java.util.Date;
import java.util.List;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.common.PageDataUtil;
import com.topaiebiz.system.util.SecurityContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.grade.dao.MerchantGradeDao;
import com.topaiebiz.merchant.grade.dto.MerchantGradeDto;
import com.topaiebiz.merchant.grade.entity.MerchantGradeEntity;
import com.topaiebiz.merchant.grade.exception.MerchantGradeException;
import com.topaiebiz.merchant.grade.service.MerchantGradeService;

/**
 * Description: 商家等级管理
 * 
 * Author : Anthony
 * 
 * Date :2017年9月28日 下午5:20:28
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@Service
public class MerchantGradeServiceImpl implements MerchantGradeService {

	@Autowired
	private MerchantGradeDao merchantGradeDao;

	@Override
	public List<MerchantGradeDto> MerchantGradeByName() {
		// TODO Auto-generated method stub
		return merchantGradeDao.selectMerchantGradeByName();
	}

	@Override
	public MerchantGradeDto getMerchantGradeById(Long id) {
		return merchantGradeDao.selectMerchantGradeById(id);
	}

	@Override
	public Integer modifyMerchantGradeById(MerchantGradeDto dto) {
		MerchantGradeEntity merchantGrade = merchantGradeDao.selectById(dto.getId());
		BeanUtils.copyProperties(dto, merchantGrade);
		merchantGrade.setLastModifierId(SecurityContextUtils.getCurrentUserDto().getId());
		merchantGrade.setLastModifiedTime(new Date());
		return merchantGradeDao.updateById(merchantGrade);
	}

	@Override
	public PageInfo<MerchantGradeDto> getMerchantGradeList(PagePO pagePO, MerchantGradeDto merchantGradeDto) {
		Page<MerchantGradeDto> page = PageDataUtil.buildPageParam(pagePO);
		page.setRecords(merchantGradeDao.selectMerchantGradeList(page, merchantGradeDto));
		return PageDataUtil.copyPageInfo(page);
	}

	@Override
	public Integer removeMerchantGradeByIds(Long[] id) throws GlobalException {
		// 检验商家等级表的id是否已经被商家所占用，如果被占用的话提示不让删除
		for (Long long2 : id) {
			List<MerchantGradeEntity> merchantGradeEntities = merchantGradeDao.selectMerchantInfoById(long2);
			System.out.println("*************"+merchantGradeEntities);
			if (CollectionUtils.isNotEmpty(merchantGradeEntities)){
				throw new GlobalException(MerchantGradeException.MERCHANTINFO_GRADEID_EXIST);
			}
		}
		for (Long long1 : id) {
			/** 对id进行查询 */
			MerchantGradeEntity selectMerchantGradeById = merchantGradeDao.selectMerchantGradeByIds(long1);
			if (selectMerchantGradeById == null) {
				throw new GlobalException(MerchantGradeException.MERCHANTGRADE_ID_NOT_NULL);
			}
		}
		return merchantGradeDao.deleteMerchantGradeByIds(id);
	}

	@Override
	public Integer saveMerchantGradeInfo(MerchantGradeDto merchantGradeDto) throws GlobalException {
		// 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象。
		MerchantGradeEntity merchantGradeEntity = new MerchantGradeEntity();
		BeanUtils.copyProperties(merchantGradeDto, merchantGradeEntity);
			/** 等级名称进行重复验证 */
		MerchantGradeEntity findMerchantGradeInfoByName = merchantGradeDao
				.selectMerchantGradeInfoByName(merchantGradeEntity);
		if (findMerchantGradeInfoByName != null) {
			throw new GlobalException(MerchantGradeException.MERCHANTGRADE_NAEM_NOT_REPETITION);
		}
		merchantGradeEntity.setCreatedTime(new Date());
		merchantGradeEntity.setCreatorId(SecurityContextUtils.getCurrentUserDto().getId());
		return merchantGradeDao.insert(merchantGradeEntity);
	}
}
