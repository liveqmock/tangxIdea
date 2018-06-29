package com.topaiebiz.merchant.grade.controller;

import javax.validation.Valid;

import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.merchant.grade.dto.MerchantGradeDto;
import com.topaiebiz.merchant.grade.exception.MerchantGradeException;
import com.topaiebiz.merchant.grade.service.MerchantGradeService;


/**
 * Description: 商家等级管理控制层
 * 
 * Author : Anthony
 * 
 * Date :2017年9月28日 下午4:59:30
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice: 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(value = "/merchant/merchantGrade/",method = RequestMethod.POST)
public class MerchantGradeController {

	@Autowired
	private MerchantGradeService merchantGradeService;

	/**
	 * Description： 商家等级信息列表分页检索
	 * Author: Anthony
	 * param page 分页参数
	 * 
	 * param merchantGradeDto 商家等级信息Dto
	 * 
	 * return list 商家等级信息列表数据
	 * 
	 * throws GlobalException 全局统一异常类
	 */
	@PermissionController(value = PermitType.PLATFORM,operationName = "商家等级列表分页检索")
	@RequestMapping(path = "/getMerchantGradeList")
	public ResponseInfo getMerchantGradeList(@RequestBody MerchantGradeDto merchantGradeDto)
			throws GlobalException {
		int pageNo = merchantGradeDto.getPageNo();
		int pageSize = merchantGradeDto.getPageSize();
		PagePO pagePO = new PagePO();
		pagePO.setPageNo(pageNo);
		pagePO.setPageSize(pageSize);
		return new ResponseInfo(merchantGradeService.getMerchantGradeList(pagePO,
				merchantGradeDto));
	}
	
	/**
	 * Description：添加商家等级信息
	 * 
	 * Author: Anthony
	 * 
	 * @param merchantGradeDto
	 * @param result
	 * @return
	 * @throws GlobalException
	 */
	@PermissionController(value = PermitType.PLATFORM,operationName = "添加商家等级信息")
	@RequestMapping(path = "/insertMerchantGradeInfo")
	public ResponseInfo insertMerchantGradeInfo( @RequestBody @Valid MerchantGradeDto merchantGradeDto, BindingResult result)
			throws GlobalException {
		if (result.hasErrors()) {
			// 初始化非法参数的提示信息。
			IllegalParamValidationUtils.initIllegalParamMsg(result);
			// 获取非法参数异常信息对象，并抛出异常。
			throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
		}
		/*// 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象。
		MerchantGradeEntity merchantGradeEntity = new MerchantGradeEntity();
		BeanUtils.copyProperties(merchantGradeDto, merchantGradeEntity);*/
		return new ResponseInfo(merchantGradeService.saveMerchantGradeInfo(merchantGradeDto));
	}
    
	/**
	 * Description：批量删除商家等级信息（修改状态）
	 * 
	 * Author: Anthony
	 * 
	 * param id 商家等级id
	 * 
	 * return 执行成功与否参数
	 * 
	 * throws GlobalException 全局结果异常类
	 */
	@PermissionController(value = PermitType.PLATFORM,operationName = "批量删除商家等级信息")
	@RequestMapping(path = "/deleteMerchantGradeByIds")
	public ResponseInfo cancelMerchantGradeByIds(@RequestBody  Long[] id) throws GlobalException {
		return new ResponseInfo(merchantGradeService.removeMerchantGradeByIds(id));
	}
	
	/**
	 * Description：查看商家等级信息(根据Id查看商家等级信息数据回显)。
	 * 
	 * Author: Anthony
	 * 
	 * param : id 商家等级id
	 * 
	 * return : 商家等级信息实体类对象
	 * 
	 * throws : GlobalException 全局异常类
	 */
	@PermissionController(value = PermitType.PLATFORM,operationName = "查看商家等级信息")
	@RequestMapping(path = "/getMerchantGradeById/{id}")
	public ResponseInfo getMerchantGradeById(@PathVariable Long id) throws GlobalException {
		if (StringUtils.isEmpty(id)) {
			throw new GlobalException(MerchantGradeException.MERCHANTGRADE_ID_NOT_NULL);
		}
		return new ResponseInfo(merchantGradeService.getMerchantGradeById(id));
	}

	/**
	 * Description：编辑(修改)商家等级信息。
	 * 
	 * Author: Anthony
	 * 
	 * param dto 商家等级信息dto对象
	 * 
	 * param result 绑定异常结果集
	 * 
	 * return 执行成功与否的信息
	 * 
	 * throws GlobalException 全局异常类
	 */
	@PermissionController(value = PermitType.PLATFORM,operationName = "修改商家等级信息")
	@RequestMapping(path = "updateMerchantGradeById")
	public ResponseInfo editMerchantGradeById(@RequestBody @Valid MerchantGradeDto dto, BindingResult result)
			throws GlobalException {
		if (result.hasErrors()) {
			// 初始化非法参数的提示信息。
			IllegalParamValidationUtils.initIllegalParamMsg(result);
			// 获取非法参数异常信息对象，并抛出异常。
			throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
		}

		// 成功时，直接分发数据并调用业务逻辑方法，并返回响应信息对象。
		return new ResponseInfo(merchantGradeService.modifyMerchantGradeById(dto));
	}
	
	/**
	 * Description： 商家等级名称下拉框展示
	 * 
	 * Author: Anthony
	 * 
	 * return merchantGrade 商家等级的id和Name数据信息对象
	 */
	@PermissionController(value = PermitType.PLATFORM,operationName = "商家等级列表")
	@RequestMapping(path = "/selecMerchantGradeByName")
	public ResponseInfo getMerchantGradeByName() throws GlobalException {
		return new ResponseInfo(merchantGradeService.MerchantGradeByName());
	}
}
