package com.topaiebiz.goods.spu.controller;

import java.util.List;

import javax.validation.Valid;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.nebulapaas.web.util.IllegalParamValidationUtils;
import com.topaiebiz.goods.category.backend.dto.BackendCategorysDto;
import com.topaiebiz.goods.spu.dto.GoodsSpuAttrDto;
import com.topaiebiz.goods.spu.dto.GoodsSpuDto;
import com.topaiebiz.goods.spu.dto.GoodsSpuPictureDto;
import com.topaiebiz.goods.spu.service.GoodsSpuService;

/**
 * Description 商品SPU管理控制层
 * 
 * Author Hedda
 * 
 * Date 2017年9月29日 下午8:01:27
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@RestController
@RequestMapping(value = "/goods/spu",method = RequestMethod.POST)
public class GoodsSpuController {

	@Autowired
	private GoodsSpuService goodsSpuService;

	/**
	 * Description 商品spu列表
	 * 
	 * Author Hedda
	 * 
	 * @param goodsSpuDto
	 *            商品spudto
	 * @return
	 * @throws GlobalException
	 */
	@PermissionController(value = PermitType.PLATFORM,operationName = "商品SPU列表分页检索")
	@RequestMapping(value = "/getListGoodsSpu")
	public ResponseInfo getListProductSpuInfoDto(@RequestBody GoodsSpuDto goodsSpuDto)
			throws GlobalException {
		PageInfo<GoodsSpuDto> listgoodsSpu = goodsSpuService.getListGoodsSpuDto(goodsSpuDto);
		return new ResponseInfo(listgoodsSpu);
	}
	
	/**
	 * Description 商家根据商品模板发布商品列表
	 * 
	 * Author Hedda
	 * 
	 * @param goodsSpuDto
	 *            商品spudto
	 * @return
	 * @throws GlobalException
	 */
	@PermissionController(value = PermitType.MERCHANT,operationName = "商家根据商品模板发布商品列表")
	@RequestMapping(value = "/getGoodsSpuListByBelongCategory")
	public ResponseInfo getGoodsSpuListByStoreId(@RequestBody GoodsSpuDto goodsSpuDto){
		PageInfo<GoodsSpuDto> listgoodsSpu = goodsSpuService.getGoodsSpuListByBelongCategory(goodsSpuDto);
		return new ResponseInfo(listgoodsSpu);
	}

	/**
	 * Description 商品spu批量逻辑删除
	 * 
	 * Author Hedda
	 * 
	 * @param id
	 *            商品spu的id
	 * @return
	 * @throws GlobalException
	 */
	@PermissionController(value = PermitType.PLATFORM,operationName = "商品SPU批量删除")
	@RequestMapping(value = "/cancelGoodsSpu")
	public ResponseInfo cancelGoodsSpu(@RequestBody Long[] id) throws GlobalException {
		goodsSpuService.removeGoodsSpu(id);
		return new ResponseInfo("删除成功！");
	}

	/**
	 * Description 查询最近使用类目
	 * 
	 * Author Hedda
	 * 
	 * @return
	 * @throws GlobalException
	 */
	@PermissionController(value = PermitType.PLATFORM,operationName = "商品SPU查看最近使用类目")
	@RequestMapping(value = "/getRecentlyCategoryList")
	public ResponseInfo getRecentlyCategoryList() throws GlobalException {
		List<BackendCategorysDto> backendCategorysDto = goodsSpuService.getRecentlyCategoryList();
		return new ResponseInfo(backendCategorysDto);
	}

	/**
	 * Description 商品spu添加
	 * 
	 * Author Hedda
	 * 
	 * @param goodsSpuDto
	 *            商品spu
	 * @param result
	 *            错误结果
	 * @return
	 * @throws GlobalException
	 */
	@PermissionController(value = PermitType.PLATFORM,operationName = "商品SPU添加")
	@RequestMapping(path = "/addGoodsSpu")
	public ResponseInfo addGoodsSpu(@Valid @RequestBody GoodsSpuDto goodsSpuDto, BindingResult result)
			throws GlobalException {
		/** 对商品spu字段进行校验 */
		if (result.hasErrors()) {
			/** 初始化非法参数的提示信息。 */
			IllegalParamValidationUtils.initIllegalParamMsg(result);
			/** 获取非法参数异常信息对象，并抛出异常。 */
			throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
		}
		List<GoodsSpuAttrDto> goodsSpuAttrDtos = goodsSpuDto.getGoodsSpuAttrDtos();
		List<GoodsSpuPictureDto> goodsSpuPictureDtos = goodsSpuDto.getGoodsSpuPictureDtos();
		return new ResponseInfo(goodsSpuService.saveGoodsSpu(goodsSpuDto, goodsSpuAttrDtos, goodsSpuPictureDtos));
	}

	/**
	 * Description 商品spu修改
	 * 
	 * Author Hedda
	 * 
	 * @param goodsSpuDto
	 *            商品spu
	 * @param result
	 *            错误结果
	 * @return
	 * @throws GlobalException
	 */
	@PermissionController(value = PermitType.PLATFORM,operationName = "商品SPU修改")
	@RequestMapping(path = "/editGoodsSpu")
	public ResponseInfo editGoodsSpu(@RequestBody @Valid GoodsSpuDto goodsSpuDto, BindingResult result)
			throws GlobalException {
		/** 对商品spu字段进行校验 */
		if (result.hasErrors()) {
			/** 初始化非法参数的提示信息。 */
			IllegalParamValidationUtils.initIllegalParamMsg(result);
			/** 获取非法参数异常信息对象，并抛出异常。 */
			throw new GlobalException(IllegalParamValidationUtils.getIllegalParamExceptionInfo());
		}
		List<GoodsSpuAttrDto> goodsSpuAttrDtos = goodsSpuDto.getGoodsSpuAttrDtos();
		List<GoodsSpuPictureDto> goodsSpuPictureDtos = goodsSpuDto.getGoodsSpuPictureDtos();
		return new ResponseInfo(goodsSpuService.modifyGoodsSpu(goodsSpuDto, goodsSpuAttrDtos, goodsSpuPictureDtos));
	}

	/**
	 * Description 根据id查询商品spu信息
	 * 
	 * Author Hedda
	 * 
	 * @param id
	 *            商品spu的id
	 * @return
	 * @throws GlobalException
	 */
	@PermissionController(value = PermitType.PLATFORM,operationName = "商品SPU回显")
	@RequestMapping(path = "/findGoodsSpuById/{id}")
	public ResponseInfo findGoodsSpuById(@PathVariable Long id) throws GlobalException {
		// 商品spu对象
		GoodsSpuDto goodsSpuDto = goodsSpuService.findGoodsSpuById(id);
		// 商品spu属性
		List<GoodsSpuAttrDto> goodsSpuAttrDtos = goodsSpuService.findGoodsSpuAttrById(id);
		// 商品spu图片
		List<GoodsSpuPictureDto> goodsSpuPictureDtos = goodsSpuService.findGoodsSpuPictureById(id);
		if (!(goodsSpuAttrDtos == null || goodsSpuAttrDtos.size() == 0)) {
			goodsSpuDto.setGoodsSpuAttrDtos(goodsSpuAttrDtos);
		}
		if (!(goodsSpuPictureDtos == null || goodsSpuPictureDtos.size() == 0)) {
			goodsSpuDto.setGoodsSpuPictureDtos(goodsSpuPictureDtos);
		}
		return new ResponseInfo(goodsSpuDto);
	}
	
}
