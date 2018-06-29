package com.topaiebiz.merchant.grade.service;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.merchant.grade.dto.MerchantGradeDto;

/**
 * Description: 商家等级管理业务逻辑层接口
 * 
 * Author : Anthony
 * 
 * Date :2017年9月28日 下午5:01:44
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface MerchantGradeService {

	/**
	 * Description： 商家等级名称下拉框展示
	 * 
	 * Author: Anthony
	 * 
	 * return merchantGrade 商家等级的id和Name数据信息对象
	 */
	List<MerchantGradeDto> MerchantGradeByName();

	/**
	 * Description：查看商家等级信息(根据Id查看商家等级信息数据回显)。
	 * 
	 * Author: Anthony
	 * 
	 * @param id
	 *            商家等级id
	 * @return 商家等级信息实体类对象
	 * 
	 */
	MerchantGradeDto getMerchantGradeById(Long id);

	/**
	 * Description：编辑(修改)商家等级信息。
	 * 
	 * Author: Anthony
	 * 
	 * @param dto
	 *            商家等级信息dto
	 * @return 执行成功与否的参数
	 */
	Integer modifyMerchantGradeById(MerchantGradeDto dto);

	/**
	 * Description： 商家等级信息列表分页检索。
	 * 
	 * Author: Anthony
	 * 
	 * @param pagePO
	 *            分页参数
	 * @param merchantGradeDto
	 *            商家等级信息Dto
	 * @return
	 */
	PageInfo<MerchantGradeDto> getMerchantGradeList(PagePO pagePO, MerchantGradeDto merchantGradeDto);

	/**
	 * Description： 批量删除商家等级信息
	 * 
	 * Author: Anthony
	 * 
	 * @param id
	 * @return
	 */
	Integer removeMerchantGradeByIds(Long[] id) throws GlobalException;

	/**
	 * Description：添加商家等级信息
	 * 
	 * Author: Anthony
	 * 
	 * @param merchantGradeDto
	 * @return
	 */
	Integer saveMerchantGradeInfo(MerchantGradeDto merchantGradeDto) throws GlobalException;

}
