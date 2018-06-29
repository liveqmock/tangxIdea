package com.topaiebiz.goods.comment.service;

import java.util.List;

import com.nebulapaas.base.model.PageInfo;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentDto;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentPictureDto;
import com.topaiebiz.member.dto.member.MemberTokenDto;

/**
 * Description 商品评价 接口
 * <p>
 * Author Hedda
 * <p>
 * Date 2017年10月2日 下午8:09:45
 * <p>
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * <p>
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 */
public interface GoodsSkuCommentService {

    /**
     * Description 商家端商品评价列表
     * <p>
     * Author Hedda
     *
     * @param goodsSkuCommentDto 商品sku评价dto
     * @return
     * @throws GlobalException
     */
    PageInfo<GoodsSkuCommentDto> getMerchentListGoodsSkuComment(GoodsSkuCommentDto goodsSkuCommentDto) throws GlobalException;

    /**
     * Description 商品评价删除
     * <p>
     * Author Hedda
     *
     * @param id 商品评价id
     * @return
     * @throws GlobalException
     */
    Integer removeGoodsSkuComment(Long id) throws GlobalException;

    /**
     * Description 添加商品回复评价
     * <p>
     * Author Hedda
     *
     * @param goodsSkuCommentDto 商品回复评价
     * @return
     * @throws GlobalException
     */
    Integer saveGoodsSkuCommentReply(GoodsSkuCommentDto goodsSkuCommentDto) throws GlobalException;

    /**
     * Description 查看商品详细评价
     * <p>
     * Author Hedda
     *
     * @param id 商品评价id
     * @return
     * @throws GlobalException
     */
    GoodsSkuCommentDto findGoodsSkuCommentById(Long id) throws GlobalException;

    /**
     * Description 平台商品评价列表
     * <p>
     * Author Hedda
     *
     * @param goodsSkuCommentDto 商品sku评价dto
     * @return
     */
    PageInfo<GoodsSkuCommentDto> getListGoodsSkuComment(GoodsSkuCommentDto goodsSkuCommentDto);

    /**
     * Description 添加商品评价
     * <p>
     * Author Hedda
     *
     * @param goodsSkuCommentDtos         商品sku评价
     * @param memberId 会员id
     * @return
     */
    Integer saveGoodsSkuCommentDto(List<GoodsSkuCommentDto> goodsSkuCommentDtos, Long memberId,MemberTokenDto memberTokenDto);

    /**
     * Description 根据skuId查询商品评价
     * <p>
     * Author Hedda
     *
     * @param skuId
     * @return
     */
    List<GoodsSkuCommentDto> getGoodsSkuCommentListBySkuId(Long skuId);

    /**
     * Description 通过商品id和订单id查询该商品是否评价过
     * <p>
     * Author Hedda
     *
     * @param skuId   商品skuId
     * @param orderId 订单id
     * @return
     */
    GoodsSkuCommentDto getGoodsSkuCommentBySkuIdAndOrderId(Long skuId, Long orderId);

}
