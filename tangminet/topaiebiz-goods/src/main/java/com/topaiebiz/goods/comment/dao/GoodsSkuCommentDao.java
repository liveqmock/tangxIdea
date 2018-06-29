package com.topaiebiz.goods.comment.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.nebulapaas.data.mybatis.common.BaseDao;
import com.nebulapaas.web.exception.GlobalException;
import com.topaiebiz.goods.comment.dto.GoodsSkuCommentDto;
import com.topaiebiz.goods.comment.entity.GoodsSkuCommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description 商品sku评价dao
 * 
 * Author Hedda
 * 
 * Date 2017年10月2日 下午8:14:34
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商业目的。
 *
 */
@Mapper
public interface GoodsSkuCommentDao extends BaseDao<GoodsSkuCommentEntity> {

	/**
	 * Description 商家商品评价列表
	 * 
	 * Author Hedda
	 * 
	 * @param page
	 *            分页单位
	 * @param goodsSkuCommentDto
	 *            商品sku评价dto
	 * @return
	 */
	List<GoodsSkuCommentDto> selectMerchentListGoodsSkuComment(Page<GoodsSkuCommentDto> page,
			GoodsSkuCommentDto goodsSkuCommentDto);

	/**
	 * Description 查看商品详细评价
	 * 
	 * Author Hedda
	 * 
	 * @param id
	 *            商品评价id
	 * @return
	 */
	GoodsSkuCommentDto selectGoodsSkuComentById(Long id);

	/**
	 * Description 平台商品评价列表
	 * 
	 * Author Hedda
	 * 
	 * @param page
	 *            分页单位
	 * @param goodsSkuCommentDto
	 *            商品sku评价dto
	 * @return
	 * @throws GlobalException
	 */
	List<GoodsSkuCommentDto> selectListGoodsSkuComment(Page<GoodsSkuCommentDto> page,
			GoodsSkuCommentDto goodsSkuCommentDto);

	/**
	 * Description 根据skuId查询商品评价
	 * 
	 * Author Hedda
	 * 
	 * @param skuId
	 * @return
	 */
	List<GoodsSkuCommentDto> selectGoodsSkuCommentListBySkuId(Long skuId);

	/**
	 * Description 平台商品评价列表
	 * <p>
	 * Author Hedda
	 * <p>
	 * 分页单位
	 * 商品sku评价dto
	 *
	 * @return
	 * @throws GlobalException
	 * @parm limit
	 * @parm startId
	 */
	List<GoodsSkuCommentDto> selectGoodsSkuCommetListByIdStep(@Param("limit") Integer limit, @Param("startId") Long startId);



	/**
	 * Description 通过商品id和订单id查询该商品是否评价过
	 * 
	 * Author Hedda
	 * 
	 * @param skuId
	 *            商品skuId
	 * @param orderId
	 *            订单id
	 * @return
	 */
	GoodsSkuCommentDto selectGoodsSkuCommentBySkuIdAndOrderId(@Param("skuId")Long skuId,@Param("orderId") Long orderId);


	GoodsSkuCommentDto findByOrderIdAndSkuId(@Param("orderId") Long orderId, @Param("skuId") Long skuId);
}
