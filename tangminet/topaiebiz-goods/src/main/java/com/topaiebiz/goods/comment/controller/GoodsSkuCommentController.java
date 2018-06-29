package com.topaiebiz.goods.comment.controller;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.base.po.PagePO;
import com.topaiebiz.system.annotation.PermissionController;
import com.topaiebiz.system.annotation.PermitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.nebulapaas.web.exception.GlobalException;
import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentDto;
import com.topaiebiz.goods.comment.service.GoodsSkuCommentService;

/**
 * Description 商品spu评价
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年10月2日 下午8:06:24
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
@RestController
@RequestMapping(value = "/goods/skucomment", method = RequestMethod.POST)
public class GoodsSkuCommentController {

    @Autowired
    private GoodsSkuCommentService goodsSkuCommentService;

    /**
     * Description 商家端商品评价列表
     * <p>
     * Author Hedda
     *
     * @param goodsSkuCommentDto 商品sku评价dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商家商品评论列表")
    @RequestMapping(path = "/getMerchentListGoodsSkuComment")
    public ResponseInfo getMerchentListGoodsSkuComment(@RequestBody GoodsSkuCommentDto goodsSkuCommentDto)
            throws GlobalException {
        PageInfo<GoodsSkuCommentDto> list = goodsSkuCommentService.getMerchentListGoodsSkuComment(goodsSkuCommentDto);
        return new ResponseInfo(list);

    }

    /**
     * Description 平台商品评价列表
     * <p>
     * Author Hedda
     *
     * @param goodsSkuCommentDto 商品sku评价dto
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "所有商家商品评价列表")
    @RequestMapping(path = "/getListGoodsSkuComment")
    public ResponseInfo getListGoodsSkuComment(@RequestBody GoodsSkuCommentDto goodsSkuCommentDto)
            throws GlobalException {
        PageInfo<GoodsSkuCommentDto> goodsSkuCommentList = goodsSkuCommentService.getListGoodsSkuComment(goodsSkuCommentDto);
        return new ResponseInfo(goodsSkuCommentList);

    }

    /**
     * Description 删除商品sku评价
     * <p>
     * Author Hedda
     *
     * @param id 商品评价id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "删除商品评价")
    @RequestMapping(path = "/cancelGoodsSkuComment/{id}")
    public ResponseInfo cancelGoodsSkuComment(@PathVariable Long id) throws GlobalException {
        return new ResponseInfo(goodsSkuCommentService.removeGoodsSkuComment(id));
    }

    /**
     * Description 添加商品回复评价
     * <p>
     * Author Hedda
     *
     * @param goodsSkuCommentDto 商品回复评价
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.MERCHANT, operationName = "商品评价回复")
    @RequestMapping(path = "/addGoodsSkuCommentReply")
    public ResponseInfo addGoodsSkuCommentReply(@RequestBody GoodsSkuCommentDto goodsSkuCommentDto) throws GlobalException {
        return new ResponseInfo(goodsSkuCommentService.saveGoodsSkuCommentReply(goodsSkuCommentDto));
    }

    /**
     * Description 查看商品详细评价
     * <p>
     * Author Hedda
     *
     * @param id 商品评价id
     * @return
     * @throws GlobalException
     */
    @PermissionController(value = PermitType.PLATFORM, operationName = "查询商品评价详情")
    @RequestMapping(path = "/findGoodsSkuCommentById/{id}")
    public ResponseInfo findGoodsSkuCommentById(@PathVariable Long id) throws GlobalException {
        GoodsSkuCommentDto goodsSkuCommentDto = goodsSkuCommentService.findGoodsSkuCommentById(id);
        return new ResponseInfo(goodsSkuCommentDto);
    }

}
