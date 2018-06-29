package com.topaiebiz.goods.comment.controller;

import com.alibaba.fastjson.JSONObject;
import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentDto;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentPictureDto;
import com.topaiebiz.goods.comment.exception.GoodsSkuCommentExceptionEnum;
import com.topaiebiz.goods.comment.service.GoodsSkuCommentService;
import com.topaiebiz.member.dto.member.MemberTokenDto;
import com.topaiebiz.member.login.MemberContext;
import com.topaiebiz.member.login.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description 商品spu评价
 * 
 * Author Hedda
 * 
 * Date 2017年10月2日 下午8:06:24
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@MemberLogin
@RestController
@RequestMapping(value = "/goods/skucomment",method = RequestMethod.POST)
public class GoodsSkuCommentMobileController {

	@Autowired
	private GoodsSkuCommentService goodsSkuCommentService;

	/**
	 * Description 添加商品评价
	 * 
	 * Author Hedda
	 * 
	 * @param goodsSkuCommentDtos
	 *            商品评价dto
	 * @return
	 * @throws GlobalException
	 */
	@RequestMapping(path = "/addGoodsSkuCommentDto")
	public ResponseInfo addGoodsSkuCommentDto(@RequestBody List<GoodsSkuCommentDto> goodsSkuCommentDtos) throws GlobalException {
		// 判断是否是匿名评价
		Long memberId = MemberContext.getMemberId();
		MemberTokenDto memberTokenDto =  MemberContext.getCurrentMemberToken();
		return new ResponseInfo(goodsSkuCommentService.saveGoodsSkuCommentDto(goodsSkuCommentDtos,memberId,memberTokenDto));
	}

}
